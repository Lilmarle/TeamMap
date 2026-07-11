package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddTeamRequest;
import com.marler.teammap.dto.request.UpdateTeamRequest;
import com.marler.teammap.dto.response.TeamInfoVO;

import java.util.List;

public interface TeamService {
    void add(AddTeamRequest request);

    void update(UpdateTeamRequest request);

    List<TeamInfoVO> getAllTeamInfo();
}
