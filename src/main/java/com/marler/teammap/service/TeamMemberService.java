package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.pojo.TeamMember;

import java.util.List;

public interface TeamMemberService {
    void add(AddTeamMemberRequest request);

    void quit(Long memberId);

    TeamMember getById(Long id);

    List<TeamMember> getMembersByTeamId(Long teamId);
}
