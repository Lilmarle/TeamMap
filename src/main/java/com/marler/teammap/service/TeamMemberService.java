package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddPlayerForCoachRequest;
import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.dto.request.UpdateTeamMemberRequest;
import com.marler.teammap.pojo.TeamMember;

import java.util.List;

public interface TeamMemberService {
    void add(AddTeamMemberRequest request);

    /**
     * 为教练/领队（role >= 3）添加球员记录
     * <p>
     * 教练、领队等角色也可以上场踢球，此方法为指定成员创建对应的球员记录。
     *
     * @param memberId 队伍成员ID
     * @param request  球员信息（球衣名、号码、位置）
     * @param operatorId 操作人用户ID
     * @param operatorRole 操作人角色
     * @throws RuntimeException 当校验失败时抛出异常
     */
    void addPlayerForCoach(Long memberId, AddPlayerForCoachRequest request,
                           Long operatorId, Integer operatorRole);

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

    /**
     * 更新队伍成员信息（含球员信息的联动更新）
     * <p>
     * 权限控制：
     * - 成员本人可以修改
     * - user.role >= 2（球员及以上）可以修改任意成员
     * <p>
     * 内容控制：
     * - 若有球员记录：更新 team_member（定妆照）+ player（球衣名、号码、位置）
     * - 若无球员记录：仅更新 team_member（定妆照）
     *
     * @param id          成员ID
     * @param request     更新内容（定妆照 + 可选球员信息）
     * @param operatorId  操作人用户ID
     * @param operatorRole 操作人角色
     * @throws RuntimeException 当校验失败时抛出异常
     */
    void updateMember(Long id, UpdateTeamMemberRequest request,
                      Long operatorId, Integer operatorRole);
}
