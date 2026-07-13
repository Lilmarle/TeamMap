package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddTeamRequest;
import com.marler.teammap.dto.request.UpdateTeamRequest;
import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.pojo.Team;

import java.util.List;

public interface TeamService {
    void add(AddTeamRequest request);

    void update(UpdateTeamRequest request);

    void delete(Long teamId);

    List<TeamInfoVO> getAllTeamInfo();

    /**
     * 根据运动类型查询球队列表
     *
     * @param type 运动类型：1-足球，2-篮球，3-排球
     * @return 球队列表
     */
    List<Team> getTeamsByType(Integer type);
}
