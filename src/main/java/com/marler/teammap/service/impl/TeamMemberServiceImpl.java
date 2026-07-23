package com.marler.teammap.service.impl;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddPlayerForCoachRequest;
import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.dto.request.UpdateTeamMemberRequest;
import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.mapper.TeamMemberMapper;
import com.marler.teammap.mapper.TeamRollegeRelMapper;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.pojo.TeamMember;
import com.marler.teammap.service.PlayerService;
import com.marler.teammap.service.TeamMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TeamRollegeRelMapper teamRollegeRelMapper;

    @Autowired
    private PlayerService playerService;

    @Override
    public TeamMember getById(Long id) {
        return teamMemberMapper.selectById(id);
    }

    @Override
    public List<TeamMember> getMembersByTeamId(Long teamId) {
        return teamMemberMapper.selectByTeamId(teamId);
    }

    @Override
    public List<TeamMember> getActiveByUserId(Long userId) {
        return teamMemberMapper.selectActiveByUserId(userId);
    }

    @Override
    @Transactional
    public void quit(Long memberId) {
        teamMemberMapper.updateStatus(memberId, 3);
        log.info("队伍成员退出成功 - memberId: {}", memberId);
    }

    @Override
    @Transactional
    public void updateMember(Long id, UpdateTeamMemberRequest request,
                             Long operatorId, Integer operatorRole) {
        // 1. 查询成员信息
        TeamMember member = teamMemberMapper.selectById(id);
        if (member == null) {
            log.warn("更新队伍成员失败：成员不存在 - memberId: {}", id);
            throw new RuntimeException("成员不存在");
        }

        // 2. 权限校验：成员本人 或 user.role >= 2 可以操作
        boolean isSelf = member.getUserId().equals(operatorId);
        boolean hasPermission = operatorRole != null && operatorRole >= 2;
        if (!isSelf && !hasPermission) {
            log.warn("更新队伍成员失败：权限不足 - operatorId: {}, role: {}, memberUserId: {}",
                    operatorId, operatorRole, member.getUserId());
            throw new RuntimeException("权限不足，只有成员本人或管理员可以操作");
        }

        // 3. 更新 team_member 定妆照
        TeamMember updateMember = new TeamMember();
        updateMember.setId(id);
        updateMember.setPortraitPhoto(request.getPortraitPhoto());
        teamMemberMapper.update(updateMember);
        log.info("队伍成员定妆照更新成功 - memberId: {}", id);

        // 4. 检查是否存在球员记录，若有则联动更新 player 信息
        PlayerInfoVO existingPlayer = playerService.getPlayerInfoByUserId(member.getUserId().intValue());
        if (existingPlayer != null) {
            // 只更新前端传了值的字段
            Player playerUpdate = new Player();
            playerUpdate.setId(existingPlayer.getPlayerId());
            if (request.getJerseyName() != null) {
                playerUpdate.setJerseyName(request.getJerseyName());
            }
            if (request.getJerseyNumber() != null) {
                playerUpdate.setJerseyNumber(request.getJerseyNumber());
            }
            if (request.getPosition() != null) {
                playerUpdate.setPosition(request.getPosition());
            }
            // 保留原有 userId、tmId 和 status（避免 status 被设为 null）
            Player existing = playerService.getById(existingPlayer.getPlayerId());
            if (existing != null) {
                playerUpdate.setUserId(existing.getUserId());
                playerUpdate.setTmId(existing.getTmId());
                playerUpdate.setStatus(existing.getStatus());
                playerService.update(playerUpdate);
                log.info("球员信息联动更新成功 - playerId: {}, memberId: {}",
                        existingPlayer.getPlayerId(), id);
            }
        } else {
            log.info("该成员无球员记录，仅更新定妆照 - memberId: {}", id);
        }
    }

    /**
     * 校验用户是否已加入同级别的球队
     *
     * @return 如果已加入同级别球队，返回错误信息；否则返回 null
     */
    public String validateSameRankDuplicate(Long userId, Long targetTeamId) {
        // 1. 获取目标球队的级别
        Integer targetRank = teamRollegeRelMapper.selectRankByTeamId(targetTeamId);
        if (targetRank == null) {
            log.warn("球队级别不存在 - teamId: {}", targetTeamId);
            return "球队级别信息不存在";
        }

        // 2. 查询用户活跃的成员记录
        List<TeamMember> activeMembers = teamMemberMapper.selectActiveByUserId(userId);
        if (activeMembers == null || activeMembers.isEmpty()) {
            return null; // 未加入任何球队，可以加入
        }

        // 3. 检查是否有同级别的球队
        for (TeamMember member : activeMembers) {
            Integer existingRank = teamRollegeRelMapper.selectRankByTeamId(member.getTeamId());
            if (existingRank != null && existingRank.equals(targetRank)) {
                String rankName;
                if (targetRank == 1) rankName = "院队";
                else if (targetRank == 2) rankName = "校队";
                else rankName = "班队";
                log.warn("用户已加入同级别球队 - userId: {}, rank: {}, existingTeamId: {}, targetTeamId: {}",
                        userId, targetRank, member.getTeamId(), targetTeamId);
                return "已加入" + rankName + "，请先退出该队伍再加入";
            }
        }

        return null; // 没有同级别的球队，可以加入
    }

    @Override
    @Transactional
    public void add(AddTeamMemberRequest request) {
        // 校验同级别球队重复问题
        String validationError = validateSameRankDuplicate(request.getUserId(), request.getTeamId());
        if (validationError != null) {
            throw new RuntimeException(validationError);
        }

        Integer role = request.getRole() != null ? request.getRole() : 1;

        // 1. 插入队伍成员
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(request.getTeamId());
        teamMember.setUserId(request.getUserId());
        teamMember.setPortraitPhoto(request.getPortraitPhoto());
        teamMember.setRole(role);
        teamMemberMapper.insert(teamMember);
        log.info("队伍成员插入成功 - id: {}, teamId: {}, userId: {}, role: {}",
                teamMember.getId(), request.getTeamId(), request.getUserId(), role);

        // 2. 仅当角色为队员（role=1）时，才插入球员记录
        if (role == 1) {
            Player player = new Player();
            player.setUserId(request.getUserId().intValue());
            player.setTmId(teamMember.getId().intValue());
            player.setJerseyName(request.getJerseyName());
            player.setJerseyNumber(request.getJerseyNumber());
            player.setPosition(request.getPosition());
            playerService.add(player);
            log.info("球员记录插入成功 - tmId: {}, jerseyName: {}",
                    player.getTmId(), request.getJerseyName());
        }
    }

    @Override
    @Transactional
    public void addPlayerForCoach(Long memberId, AddPlayerForCoachRequest request,
                                  Long operatorId, Integer operatorRole) {
        // 1. 查询成员信息
        TeamMember member = teamMemberMapper.selectById(memberId);
        if (member == null) {
            log.warn("为教练添加球员记录失败：成员不存在 - memberId: {}", memberId);
            throw new RuntimeException("成员不存在");
        }

        // 2. 校验角色：仅允许为角色 >= 3（教练、领队）的成员添加球员记录
        if (member.getRole() == null || member.getRole() < 3) {
            log.warn("为教练添加球员记录失败：角色不符合要求 - memberId: {}, role: {}",
                    memberId, member.getRole());
            throw new RuntimeException("仅允许为教练（role=3）或领队（role=4）添加球员记录");
        }

        // 3. 权限校验：操作人需为成员本人，或具有管理权限（role >= 2）
        boolean isSelf = member.getUserId().equals(operatorId);
        boolean hasPermission = operatorRole != null && operatorRole >= 2;
        if (!isSelf && !hasPermission) {
            log.warn("为教练添加球员记录失败：权限不足 - operatorId: {}, role: {}, memberUserId: {}",
                    operatorId, operatorRole, member.getUserId());
            throw new RuntimeException("权限不足，只有成员本人或管理员可以操作");
        }

        // 4. 检查是否已存在球员记录（tm_id 唯一约束）
        PlayerInfoVO existingPlayer = playerService.getPlayerInfoByUserId(member.getUserId().intValue());
        if (existingPlayer != null) {
            log.warn("为教练添加球员记录失败：已存在球员记录 - userId: {}, memberId: {}",
                    member.getUserId(), memberId);
            throw new RuntimeException("该成员已存在球员记录，请使用修改功能");
        }

        // 5. 创建球员记录
        Player player = new Player();
        player.setUserId(member.getUserId().intValue());
        player.setTmId(member.getId().intValue());
        player.setJerseyName(request.getJerseyName());
        player.setJerseyNumber(request.getJerseyNumber());
        player.setPosition(request.getPosition());
        player.setStatus(1); // 默认可出战
        playerService.add(player);
        log.info("为教练/领队添加球员记录成功 - memberId: {}, userId: {}, jerseyName: {}",
                memberId, member.getUserId(), request.getJerseyName());
    }
}
