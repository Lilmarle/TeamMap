package com.marler.teammap.service.impl;

import com.marler.teammap.dto.request.AddTeamRequest;
import com.marler.teammap.dto.request.UpdateTeamRequest;
import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.mapper.TeamMapper;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.pojo.TeamRollegeRel;
import com.marler.teammap.service.TeamRollegeRelService;
import com.marler.teammap.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamRollegeRelService teamRollegeRelService;

    //  创建球队
    @Override
    @Transactional
    public void add(AddTeamRequest request) {
        Team team = request.getTeam();

        // 1. 插入球队
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());
        teamMapper.insert(team);
        log.info("球队插入成功 - id: {}, name: {}", team.getId(), team.getName());

        // 2. 插入球队-学院关联记录
        if (request.getRank() != null) {
            TeamRollegeRel rel = new TeamRollegeRel();
            rel.setTeamId(team.getId());
            rel.setRank(request.getRank());
            rel.setCollegeId(request.getCollegeId());
            teamRollegeRelService.add(rel);
            log.info("球队学院关联记录插入成功 - teamId: {}, rank: {}, collegeId: {}",
                    rel.getTeamId(), rel.getRank(), rel.getCollegeId());
        }
    }

    // 删除球队
    @Override
    @Transactional
    public void delete(Long teamId) {
        // 1. 删除关联记录
        teamRollegeRelService.deleteByTeamId(teamId);
        log.info("球队关联记录删除成功 - teamId: {}", teamId);

        // 2. 删除球队
        teamMapper.deleteById(teamId);
        log.info("球队删除成功 - id: {}", teamId);
    }

    // 查询所有球队信息
    @Override
    public List<TeamInfoVO> getAllTeamInfo() {
        log.info("查询所有球队信息（视图 v_team_info）");
        return teamMapper.selectAllFromView();
    }

    // 根据ID查询球队
    @Override
    public Team getById(Long id) {
        log.info("根据ID查询球队 - id: {}", id);
        return teamMapper.selectById(id);
    }

    // 根据运动类型查询球队
    @Override
    public List<Team> getTeamsByType(Integer type) {
        log.info("根据运动类型查询球队 - type: {}", type);
        return teamMapper.selectByType(type);
    }

    // 更新球队信息
    @Override
    @Transactional
    public void update(UpdateTeamRequest request) {
        // 1. 更新 team 表
        Team team = new Team();
        team.setId(request.getTeamId());
        team.setName(request.getTeamName());
        team.setShortName(request.getTeamShortName());
        team.setLogo(request.getTeamLogo());
        team.setDescription(request.getTeamDescription());
        team.setType(request.getSportType());
        team.setGender(request.getGender());
        teamMapper.updateById(team);
        log.info("球队基本信息更新成功 - id: {}", request.getTeamId());

        // 2. 更新 team_college_rel 表
        TeamRollegeRel rel = new TeamRollegeRel();
        rel.setTeamId(request.getTeamId());
        rel.setRank(request.getTeamRank());
        rel.setCollegeId(request.getCollegeId());
        teamRollegeRelService.updateByTeamId(rel);
        log.info("球队学院关联记录更新成功 - teamId: {}", request.getTeamId());
    }
}
