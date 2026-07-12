package com.marler.teammap.service.impl;

import com.marler.teammap.mapper.TeamRollegeRelMapper;
import com.marler.teammap.pojo.TeamRollegeRel;
import com.marler.teammap.service.TeamRollegeRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TeamRollegeRelServiceImpl implements TeamRollegeRelService {

    @Autowired
    private TeamRollegeRelMapper teamRollegeRelMapper;

    @Override
    @Transactional
    public void add(TeamRollegeRel teamRollegeRel) {
        teamRollegeRel.setCreateTime(LocalDateTime.now());
        teamRollegeRel.setUpdateTime(LocalDateTime.now());
        teamRollegeRelMapper.insert(teamRollegeRel);
    }

    @Override
    @Transactional
    public void updateByTeamId(TeamRollegeRel teamRollegeRel) {
        teamRollegeRel.setUpdateTime(LocalDateTime.now());
        teamRollegeRelMapper.updateByTeamId(teamRollegeRel);
    }

    @Override
    @Transactional
    public void deleteByTeamId(Long teamId) {
        teamRollegeRelMapper.deleteByTeamId(teamId);
    }
}
