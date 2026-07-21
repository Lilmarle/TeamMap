package com.marler.teammap.service.impl;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTeamMemberRequest;
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
    @Transactional
    public void quit(Long memberId) {
        teamMemberMapper.updateStatus(memberId, 3);
        log.info("队伍成员退出成功 - memberId: {}", memberId);
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
}
