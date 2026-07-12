package com.marler.teammap.service.impl;

import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.mapper.TeamMemberMapper;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.pojo.TeamMember;
import com.marler.teammap.service.PlayerService;
import com.marler.teammap.service.TeamMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private PlayerService playerService;

    @Override
    @Transactional
    public void add(AddTeamMemberRequest request) {
        // 1. 插入队伍成员（角色固定为 1-队员）
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(request.getTeamId());
        teamMember.setUserId(request.getUserId());
        teamMember.setPortraitPhoto(request.getPortraitPhoto());
        teamMember.setRole(1);
        teamMemberMapper.insert(teamMember);
        log.info("队伍成员插入成功 - id: {}, teamId: {}, userId: {}",
                teamMember.getId(), request.getTeamId(), request.getUserId());

        // 2. 插入球员记录
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
