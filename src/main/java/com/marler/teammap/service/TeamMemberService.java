package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.pojo.TeamMember;

import java.util.List;

public interface TeamMemberService {
    void add(AddTeamMemberRequest request);

    void quit(Long memberId);

    TeamMember getById(Long id);

    List<TeamMember> getMembersByTeamId(Long teamId);

    /**
     * 查询用户的活跃队伍成员记录
     *
     * @param userId 用户ID
     * @return 活跃的成员记录列表
     */
    List<TeamMember> getActiveByUserId(Long userId);
}
