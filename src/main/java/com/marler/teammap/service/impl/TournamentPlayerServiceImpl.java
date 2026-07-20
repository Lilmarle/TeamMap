package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.TournamentPlayerInfoVO;
import com.marler.teammap.mapper.PlayerMapper;
import com.marler.teammap.mapper.TeamMapper;
import com.marler.teammap.mapper.TournamentMapper;
import com.marler.teammap.mapper.TournamentPlayerMapper;
import com.marler.teammap.mapper.TournamentTeamMapper;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.pojo.Tournament;
import com.marler.teammap.pojo.TournamentPlayer;
import com.marler.teammap.pojo.TournamentTeam;
import com.marler.teammap.service.TournamentPlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TournamentPlayerServiceImpl implements TournamentPlayerService {

    @Autowired
    private TournamentPlayerMapper tournamentPlayerMapper;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TournamentTeamMapper tournamentTeamMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Override
    @Transactional
    public String registerBatch(Long tournamentId, Long teamId, List<Long> userIds, Long operatorUserId) {
        log.info("球队批量报名球员参赛 - tournamentId: {}, teamId: {}, userIds数量: {}, operatorUserId: {}",
                tournamentId, teamId, userIds != null ? userIds.size() : 0, operatorUserId);

        // 1. 查询赛事是否存在
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            log.warn("报名失败：赛事不存在 - tournamentId: {}", tournamentId);
            throw new RuntimeException("赛事不存在");
        }

        // 2. 校验赛事状态：只能报名 status = 1（筹办中）的赛事
        if (tournament.getStatus() != 1) {
            log.warn("报名失败：赛事状态不允许报名 - tournamentId: {}, status: {}",
                    tournamentId, tournament.getStatus());
            throw new RuntimeException("当前赛事状态不允许报名，仅可报名筹办中的赛事");
        }

        // 3. 查询球队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            log.warn("报名失败：球队不存在 - teamId: {}", teamId);
            throw new RuntimeException("球队不存在");
        }

        // 4. 校验运动类型是否一致
        if (!team.getType().equals(tournament.getType())) {
            log.warn("报名失败：运动类型不匹配 - teamType: {}, tournamentType: {}",
                    team.getType(), tournament.getType());
            throw new RuntimeException("球队与赛事的运动类型不一致");
        }

        // 5. 检查球队是否已加入该赛事（状态为已通过/已加入）
        TournamentTeam tournamentTeam = tournamentTeamMapper.selectByTournamentIdAndTeamId(tournamentId, teamId);
        if (tournamentTeam == null || tournamentTeam.getStatus() != 1) {
            log.warn("报名失败：球队未加入该赛事 - tournamentId: {}, teamId: {}, relStatus: {}",
                    tournamentId, teamId, tournamentTeam != null ? tournamentTeam.getStatus() : null);
            throw new RuntimeException("球队未加入该赛事或尚未通过审核，请先申请球队加入赛事");
        }

        // 6. 参数校验
        if (userIds == null || userIds.isEmpty()) {
            log.warn("报名失败：用户ID列表为空");
            throw new RuntimeException("请选择要报名的球员");
        }

        // 7. 去重
        List<Long> distinctUserIds = userIds.stream().distinct().collect(Collectors.toList());

        // 8. 校验每个用户是否属于该球队
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        List<TournamentPlayer> toInsert = new ArrayList<>();

        // 查询已存在的报名记录（用于判重）
        List<TournamentPlayer> existingRegistrations = tournamentPlayerMapper
                .selectByTournamentIdAndUserIds(tournamentId, distinctUserIds);
        Set<Long> alreadyRegisteredUserIds = existingRegistrations.stream()
                .map(TournamentPlayer::getUserId)
                .collect(Collectors.toSet());

        // 获取球队所有球员信息（用于核验所属球队）
        List<com.marler.teammap.dto.response.PlayerInfoVO> teamPlayers =
                playerMapper.selectPlayerInfoByTeamId(Math.toIntExact(teamId));
        Set<Long> teamUserIds = teamPlayers.stream()
                .map(p -> p.getUserId().longValue())
                .collect(Collectors.toSet());

        for (Long userId : distinctUserIds) {
            try {
                // 8a. 校验用户是否是该球队的球员
                if (!teamUserIds.contains(userId)) {
                    failList.add("用户ID(" + userId + ")不属于该球队");
                    continue;
                }

                // 8b. 检查是否已经报名过该赛事
                if (alreadyRegisteredUserIds.contains(userId)) {
                    failList.add("用户ID(" + userId + ")已报名过该赛事");
                    continue;
                }

                // 8c. 添加到待插入列表，status = 0（审核中）
                TournamentPlayer tp = new TournamentPlayer();
                tp.setTournamentId(tournamentId);
                tp.setUserId(userId);
                tp.setStatus(0);
                toInsert.add(tp);
                successList.add("用户ID(" + userId + ")");

            } catch (Exception e) {
                log.warn("批量报名 - 用户报名失败 userId: {}, error: {}", userId, e.getMessage());
                failList.add("用户ID(" + userId + "): " + e.getMessage());
            }
        }

        // 9. 批量插入
        if (!toInsert.isEmpty()) {
            tournamentPlayerMapper.insertBatch(toInsert);
            log.info("批量报名球员成功 - tournamentId: {}, teamId: {}, 成功报名 {} 人",
                    tournamentId, teamId, toInsert.size());
        }

        // 10. 构建结果信息
        StringBuilder resultMsg = new StringBuilder();
        if (!successList.isEmpty()) {
            resultMsg.append("成功报名 ").append(successList.size()).append(" 名球员：")
                    .append(String.join("、", successList)).append("；");
        }
        if (!failList.isEmpty()) {
            resultMsg.append("失败 ").append(failList.size()).append(" 名：")
                    .append(String.join("；", failList));
        }
        if (resultMsg.isEmpty()) {
            resultMsg.append("未处理任何球员");
        }

        String result = resultMsg.toString();
        log.info("球队批量报名球员完成 - tournamentId: {}, teamId: {}, 结果: {}",
                tournamentId, teamId, result);
        return result;
    }

    @Override
    @Transactional
    public TournamentPlayer register(Long tournamentId, Long userId, Long operatorUserId) {
        log.info("球员单独报名参赛 - tournamentId: {}, userId: {}, operatorUserId: {}",
                tournamentId, userId, operatorUserId);

        // 1. 查询赛事是否存在
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            log.warn("报名失败：赛事不存在 - tournamentId: {}", tournamentId);
            throw new RuntimeException("赛事不存在");
        }

        // 2. 校验赛事状态：只能报名 status = 1（筹办中）的赛事
        if (tournament.getStatus() != 1) {
            log.warn("报名失败：赛事状态不允许报名 - tournamentId: {}, status: {}",
                    tournamentId, tournament.getStatus());
            throw new RuntimeException("当前赛事状态不允许报名，仅可报名筹办中的赛事");
        }

        // 3. 检查是否已经报名过该赛事
        TournamentPlayer existing = tournamentPlayerMapper.selectByTournamentIdAndUserId(tournamentId, userId);
        if (existing != null) {
            log.warn("报名失败：该用户已报名过该赛事 - tournamentId: {}, userId: {}, currentStatus: {}",
                    tournamentId, userId, existing.getStatus());
            throw new RuntimeException("该用户已报名过该赛事");
        }

        // 4. 创建报名记录，status = 0（审核中）
        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.setTournamentId(tournamentId);
        tournamentPlayer.setUserId(userId);
        tournamentPlayer.setStatus(0);
        tournamentPlayerMapper.insert(tournamentPlayer);

        log.info("球员单独报名参赛成功 - id: {}, tournamentId: {}, userId: {}",
                tournamentPlayer.getId(), tournamentId, userId);
        return tournamentPlayer;
    }

    @Override
    @Transactional
    public void approve(Long id, Integer status, Long userId, Integer role) {
        log.info("审批球员准入 - id: {}, targetStatus: {}, userId: {}, role: {}", id, status, userId, role);

        // 1. 参数校验
        if (status != 1 && status != 3) {
            log.warn("审批失败：无效的目标状态 - status: {}", status);
            throw new RuntimeException("无效的审批状态，仅允许通过(1)或拒绝(3)");
        }

        // 2. 查询报名记录是否存在
        TournamentPlayer tournamentPlayer = tournamentPlayerMapper.selectById(id);
        if (tournamentPlayer == null) {
            log.warn("审批失败：报名记录不存在 - id: {}", id);
            throw new RuntimeException("报名记录不存在");
        }

        // 3. 校验当前状态是否为审核中
        if (tournamentPlayer.getStatus() != 0) {
            log.warn("审批失败：当前状态不是审核中 - id: {}, currentStatus: {}",
                    id, tournamentPlayer.getStatus());
            throw new RuntimeException("当前状态不允许审批，仅可审批状态为「审核中」的记录");
        }

        // 4. 查询赛事，校验权限
        Tournament tournament = tournamentMapper.selectById(tournamentPlayer.getTournamentId());
        if (tournament == null) {
            log.warn("审批失败：关联赛事不存在 - tournamentId: {}", tournamentPlayer.getTournamentId());
            throw new RuntimeException("关联赛事不存在");
        }
        if (role == null || role < 3) {
            log.warn("审批失败：权限不足 - userId: {}, role: {}", userId, role);
            throw new RuntimeException("权限不足，需要赛事管理员或系统管理员角色");
        }
        // role = 3（赛事管理员）只能审批自己创建的赛事
        if (role == 3 && !tournament.getCreatorId().equals(userId)) {
            log.warn("审批失败：赛事管理员只能审批自己创建的赛事 - userId: {}, creatorId: {}",
                    userId, tournament.getCreatorId());
            throw new RuntimeException("只能审批自己创建的赛事中的球员报名");
        }

        // 5. 更新状态
        tournamentPlayerMapper.updateStatus(id, status);
        String statusText = status == 1 ? "已通过" : "已拒绝";
        log.info("审批球员准入成功 - id: {}, status: {}({}), operatorId: {}",
                id, status, statusText, userId);
    }

    @Override
    public List<TournamentPlayerInfoVO> getPlayersByTournamentId(Long tournamentId) {
        log.info("查询赛事球员报名信息 - tournamentId: {}", tournamentId);

        if (tournamentId == null || tournamentId <= 0) {
            log.warn("查询失败：赛事ID无效");
            throw new RuntimeException("赛事ID无效");
        }

        return tournamentPlayerMapper.selectInfoByTournamentId(tournamentId);
    }

    @Override
    public List<TournamentPlayerInfoVO> getPlayersByTournamentIdAndTeamId(Long tournamentId, Long teamId) {
        log.info("按球队查询赛事球员报名信息 - tournamentId: {}, teamId: {}", tournamentId, teamId);

        if (tournamentId == null || tournamentId <= 0) {
            log.warn("查询失败：赛事ID无效");
            throw new RuntimeException("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("查询失败：球队ID无效");
            throw new RuntimeException("球队ID无效");
        }

        return tournamentPlayerMapper.selectInfoByTournamentIdAndTeamId(tournamentId, teamId);
    }
}
