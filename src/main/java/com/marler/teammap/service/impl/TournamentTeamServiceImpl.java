package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.TournamentTeamInfoVO;
import com.marler.teammap.mapper.TeamMapper;
import com.marler.teammap.mapper.TournamentMapper;
import com.marler.teammap.mapper.TournamentTeamMapper;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.pojo.Tournament;
import com.marler.teammap.pojo.TournamentTeam;
import com.marler.teammap.service.TournamentTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TournamentTeamServiceImpl implements TournamentTeamService {

    @Autowired
    private TournamentTeamMapper tournamentTeamMapper;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Override
    @Transactional
    public void apply(Long tournamentId, Long teamId, Long userId) {
        log.info("球队申请加入赛事 - tournamentId: {}, teamId: {}, userId: {}", tournamentId, teamId, userId);

        // 1. 查询赛事是否存在
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            log.warn("申请失败：赛事不存在 - tournamentId: {}", tournamentId);
            throw new RuntimeException("赛事不存在");
        }

        // 2. 校验赛事状态：只能加入 status = 1（筹办中）的赛事
        if (tournament.getStatus() != 1) {
            log.warn("申请失败：赛事状态不允许加入 - tournamentId: {}, status: {}", tournamentId, tournament.getStatus());
            throw new RuntimeException("当前赛事状态不允许加入，仅可加入筹办中的赛事");
        }

        // 3. 查询球队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            log.warn("申请失败：球队不存在 - teamId: {}", teamId);
            throw new RuntimeException("球队不存在");
        }

        // 4. 校验运动类型是否一致
        if (!team.getType().equals(tournament.getType())) {
            log.warn("申请失败：运动类型不匹配 - teamType: {}, tournamentType: {}", team.getType(), tournament.getType());
            throw new RuntimeException("球队与赛事的运动类型不一致");
        }

        // 5. 检查是否已经申请过该赛事
        TournamentTeam existing = tournamentTeamMapper.selectByTournamentIdAndTeamId(tournamentId, teamId);
        if (existing != null) {
            if (existing.getStatus() == 1) {
                log.warn("申请失败：该球队已申请过此赛事，等待审核 - tournamentId: {}, teamId: {}",
                        tournamentId, teamId);
                throw new RuntimeException("该球队已申请过此赛事，请勿重复申请");
            }
            if (existing.getStatus() == 2) {
                log.warn("申请失败：该球队已通过审核 - tournamentId: {}, teamId: {}",
                        tournamentId, teamId);
                throw new RuntimeException("该球队已通过该赛事的审核，无需重复申请");
            }
            // status == 3（未通过/驳回）允许重新申请，将状态更新为 1（申请中）
            tournamentTeamMapper.updateStatus(existing.getId(), 1);
            log.info("球队重新申请赛事成功 - id: {}, tournamentId: {}, teamId: {}",
                    existing.getId(), tournamentId, teamId);
            return;
        }

        // 6. 创建申请记录，status 默认为 1（申请中）
        TournamentTeam tournamentTeam = new TournamentTeam();
        tournamentTeam.setTournamentId(tournamentId);
        tournamentTeam.setTeamId(teamId);
        tournamentTeam.setStatus(1);
        tournamentTeamMapper.insert(tournamentTeam);

        log.info("球队申请加入赛事成功 - id: {}, tournamentId: {}, teamId: {}",
                tournamentTeam.getId(), tournamentId, teamId);
    }

    @Override
    public List<TournamentTeamInfoVO> getTeamsByTournamentId(Long tournamentId) {
        log.info("查询与赛事关联的球队列表 - tournamentId: {}", tournamentId);
        return tournamentTeamMapper.selectTeamsByTournamentId(tournamentId);
    }

    @Override
    @Transactional
    public void approve(Long relId, Integer status, Long userId, Integer role) {
        log.info("审批球队申请 - relId: {}, targetStatus: {}, userId: {}, role: {}", relId, status, userId, role);

        // 1. 参数校验
        if (status != 2 && status != 3) {
            log.warn("审批失败：无效的目标状态 - status: {}", status);
            throw new RuntimeException("无效的审批状态，仅允许通过(2)或驳回(3)");
        }

        // 2. 查询关联记录是否存在
        TournamentTeam tournamentTeam = tournamentTeamMapper.selectById(relId);
        if (tournamentTeam == null) {
            log.warn("审批失败：申请记录不存在 - relId: {}", relId);
            throw new RuntimeException("申请记录不存在");
        }

        // 3. 校验当前状态是否为申请中
        if (tournamentTeam.getStatus() != 1) {
            log.warn("审批失败：申请记录状态不是申请中 - relId: {}, currentStatus: {}", relId, tournamentTeam.getStatus());
            throw new RuntimeException("当前申请状态不允许审批，仅可审批状态为「申请中」的记录");
        }

        // 4. 查询赛事，校验权限
        Tournament tournament = tournamentMapper.selectById(tournamentTeam.getTournamentId());
        if (tournament == null) {
            log.warn("审批失败：关联赛事不存在 - tournamentId: {}", tournamentTeam.getTournamentId());
            throw new RuntimeException("关联赛事不存在");
        }
        // 5. 权限校验：role >= 3 且有权限
        if (role == null || role < 3) {
            log.warn("审批失败：权限不足 - userId: {}, role: {}", userId, role);
            throw new RuntimeException("权限不足，需要赛事管理员或系统管理员角色");
        }
        // role = 3（赛事管理员）只能审批自己创建的赛事
        if (role == 3 && !tournament.getCreatorId().equals(userId)) {
            log.warn("审批失败：赛事管理员只能审批自己创建的赛事 - userId: {}, creatorId: {}",
                    userId, tournament.getCreatorId());
            throw new RuntimeException("只能审批自己创建的赛事中的申请");
        }
        // 6. 更新状态
        tournamentTeamMapper.updateStatus(relId, status);
        String statusText = status == 2 ? "已通过" : "未通过";
        log.info("审批球队申请成功 - relId: {}, status: {}({}), operatorId: {}",
                relId, status, statusText, userId);
    }

    /**
     * 校验主办方是否有权限操作该赛事
     */
    private void validateOrganizerPermission(Tournament tournament, Long userId, Integer role, String operation) {
        // 权限校验：role >= 3
        if (role == null || role < 3) {
            log.warn("{}失败：权限不足 - userId: {}, role: {}", operation, userId, role);
            throw new RuntimeException("权限不足，需要赛事管理员或系统管理员角色");
        }
        // role = 3（赛事管理员）只能操作自己创建的赛事
        if (role == 3 && !tournament.getCreatorId().equals(userId)) {
            log.warn("{}失败：赛事管理员只能操作自己创建的赛事 - userId: {}, creatorId: {}",
                    operation, userId, tournament.getCreatorId());
            throw new RuntimeException("只能操作自己创建的赛事中的申请");
        }
    }

    @Override
    @Transactional
    public void invite(Long tournamentId, Long teamId, Long userId, Integer role) {
        log.info("主办方邀请球队加入赛事 - tournamentId: {}, teamId: {}, userId: {}, role: {}",
                tournamentId, teamId, userId, role);

        // 1. 查询赛事是否存在
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            log.warn("邀请失败：赛事不存在 - tournamentId: {}", tournamentId);
            throw new RuntimeException("赛事不存在");
        }

        // 2. 校验赛事状态：只能邀请加入 status = 1（筹办中）的赛事
        if (tournament.getStatus() != 1) {
            log.warn("邀请失败：赛事状态不允许邀请 - tournamentId: {}, status: {}",
                    tournamentId, tournament.getStatus());
            throw new RuntimeException("当前赛事状态不允许邀请球队，仅可邀请加入筹办中的赛事");
        }

        // 3. 权限校验：只有赛事创建者或系统管理员可以邀请
        validateOrganizerPermission(tournament, userId, role, "邀请");

        // 4. 查询球队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            log.warn("邀请失败：球队不存在 - teamId: {}", teamId);
            throw new RuntimeException("球队不存在");
        }

        // 5. 校验运动类型是否一致
        if (!team.getType().equals(tournament.getType())) {
            log.warn("邀请失败：运动类型不匹配 - teamType: {}, tournamentType: {}",
                    team.getType(), tournament.getType());
            throw new RuntimeException("球队与赛事的运动类型不一致");
        }

        // 6. 检查是否已经存在关联记录
        TournamentTeam existing = tournamentTeamMapper.selectByTournamentIdAndTeamId(tournamentId, teamId);
        if (existing != null) {
            if (existing.getStatus() == 2) {
                log.warn("邀请失败：该球队已通过审核，已在赛事中 - tournamentId: {}, teamId: {}",
                        tournamentId, teamId);
                throw new RuntimeException("该球队已在赛事中，请勿重复邀请");
            }
            if (existing.getStatus() == 1) {
                log.warn("邀请失败：该球队已被邀请，等待确认 - tournamentId: {}, teamId: {}",
                        tournamentId, teamId);
                throw new RuntimeException("该球队已被邀请，请勿重复邀请");
            }
            // status == 3（未通过/驳回），允许重新邀请，直接更新为已通过
            tournamentTeamMapper.updateStatus(existing.getId(), 2);
            log.info("主办方重新邀请球队成功 - id: {}, tournamentId: {}, teamId: {}",
                    existing.getId(), tournamentId, teamId);
            return;
        }

        // 7. 创建邀请记录，直接设为已通过(2)
        TournamentTeam tournamentTeam = new TournamentTeam();
        tournamentTeam.setTournamentId(tournamentId);
        tournamentTeam.setTeamId(teamId);
        tournamentTeam.setStatus(2);
        tournamentTeamMapper.insert(tournamentTeam);

        log.info("主办方邀请球队加入赛事成功 - id: {}, tournamentId: {}, teamId: {}",
                tournamentTeam.getId(), tournamentId, teamId);
    }

    @Override
    @Transactional
    public String inviteBatch(Long tournamentId, List<Long> teamIds, Long userId, Integer role) {
        log.info("主办方批量邀请球队加入赛事 - tournamentId: {}, teamIds: {}, userId: {}, role: {}",
                tournamentId, teamIds, userId, role);

        // 1. 查询赛事是否存在
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            log.warn("批量邀请失败：赛事不存在 - tournamentId: {}", tournamentId);
            throw new RuntimeException("赛事不存在");
        }

        // 2. 校验赛事状态
        if (tournament.getStatus() != 1) {
            log.warn("批量邀请失败：赛事状态不允许邀请 - tournamentId: {}, status: {}",
                    tournamentId, tournament.getStatus());
            throw new RuntimeException("当前赛事状态不允许邀请球队，仅可邀请加入筹办中的赛事");
        }

        // 3. 权限校验
        validateOrganizerPermission(tournament, userId, role, "批量邀请");

        // 4. 参数校验
        if (teamIds == null || teamIds.isEmpty()) {
            log.warn("批量邀请失败：球队ID列表为空 - tournamentId: {}", tournamentId);
            throw new RuntimeException("请选择要邀请的球队");
        }

        // 5. 遍历邀请每支球队
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();

        for (Long teamId : teamIds) {
            try {
                // 检查球队是否存在
                Team team = teamMapper.selectById(teamId);
                if (team == null) {
                    failList.add("球队ID(" + teamId + ")不存在");
                    continue;
                }

                // 校验运动类型是否一致
                if (!team.getType().equals(tournament.getType())) {
                    failList.add(team.getName() + "(" + team.getShortName() + ")运动类型不匹配");
                    continue;
                }

                // 检查是否已经存在关联记录
                TournamentTeam existing = tournamentTeamMapper.selectByTournamentIdAndTeamId(tournamentId, teamId);
                if (existing != null) {
                    if (existing.getStatus() == 2) {
                        failList.add(team.getName() + "(" + team.getShortName() + ")已在赛事中");
                        continue;
                    }
                    if (existing.getStatus() == 1) {
                        failList.add(team.getName() + "(" + team.getShortName() + ")已被邀请，请勿重复邀请");
                        continue;
                    }
                    // status == 3，重新邀请
                    tournamentTeamMapper.updateStatus(existing.getId(), 2);
                    successList.add(team.getName() + "(" + team.getShortName() + ")");
                    continue;
                }

                // 创建邀请记录
                TournamentTeam tournamentTeam = new TournamentTeam();
                tournamentTeam.setTournamentId(tournamentId);
                tournamentTeam.setTeamId(teamId);
                tournamentTeam.setStatus(2);
                tournamentTeamMapper.insert(tournamentTeam);
                successList.add(team.getName() + "(" + team.getShortName() + ")");

            } catch (Exception e) {
                log.warn("批量邀请 - 邀请球队失败 teamId: {}, error: {}", teamId, e.getMessage());
                failList.add("球队ID(" + teamId + "): " + e.getMessage());
            }
        }

        // 6. 构建结果信息
        StringBuilder resultMsg = new StringBuilder();
        if (!successList.isEmpty()) {
            resultMsg.append("成功邀请 ").append(successList.size()).append(" 支球队：")
                    .append(String.join("、", successList)).append("；");
        }
        if (!failList.isEmpty()) {
            resultMsg.append("失败 ").append(failList.size()).append(" 支：")
                    .append(String.join("；", failList));
        }
        if (resultMsg.isEmpty()) {
            resultMsg.append("未处理任何球队");
        }

        String result = resultMsg.toString();
        log.info("主办方批量邀请球队完成 - tournamentId: {}, 结果: {}", tournamentId, result);
        return result;
    }

    @Override
    public boolean isTeamInTournament(Long tournamentId, Long teamId) {
        log.debug("判断球队是否在赛事中 - tournamentId: {}, teamId: {}", tournamentId, teamId);

        if (tournamentId == null || teamId == null) {
            return false;
        }

        TournamentTeam existing = tournamentTeamMapper.selectByTournamentIdAndTeamId(tournamentId, teamId);
        return existing != null && existing.getStatus() == 2;
    }

    @Override
    @Transactional
    public void removeTeam(Long tournamentId, Long teamId, Long userId, Integer role) {
        log.info("主办方从赛事中移除球队 - tournamentId: {}, teamId: {}, userId: {}, role: {}",
                tournamentId, teamId, userId, role);

        // 1. 查询关联记录是否存在
        TournamentTeam tournamentTeam = tournamentTeamMapper.selectByTournamentIdAndTeamId(tournamentId, teamId);
        if (tournamentTeam == null) {
            log.warn("移除失败：该球队未参与此赛事 - tournamentId: {}, teamId: {}", tournamentId, teamId);
            throw new RuntimeException("该球队未参与此赛事");
        }

        // 2. 查询赛事，校验权限
        Tournament tournament = tournamentMapper.selectById(tournamentId);
        if (tournament == null) {
            log.warn("移除失败：赛事不存在 - tournamentId: {}", tournamentId);
            throw new RuntimeException("赛事不存在");
        }

        validateOrganizerPermission(tournament, userId, role, "移除球队");

        // 3. 物理删除关联记录
        tournamentTeamMapper.deleteByTournamentIdAndTeamId(tournamentId, teamId);
        log.info("主办方从赛事中移除球队成功 - tournamentId: {}, teamId: {}, operatorId: {}",
                tournamentId, teamId, userId);
    }
}