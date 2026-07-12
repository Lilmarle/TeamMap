package com.marler.teammap.service;

import com.marler.teammap.pojo.TeamRollegeRel;

public interface TeamRollegeRelService {
    void add(TeamRollegeRel teamRollegeRel);

    void updateByTeamId(TeamRollegeRel teamRollegeRel);

    void deleteByTeamId(Long teamId);
}
