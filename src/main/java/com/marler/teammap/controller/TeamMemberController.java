package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddPlayerForCoachRequest;
import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.dto.request.UpdateTeamMemberRequest;
import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.pojo.TeamMember;
import com.marler.teammap.service.PlayerService;
import com.marler.teammap.service.TeamMemberService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/team-members")
public class TeamMemberController {

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private PlayerService playerService;

    /**
     * 获取当前用户在指定队伍中的 team_member 角色
     *
     * @param userId 当前用户ID
     * @param teamId 队伍ID
     * @return team_member.role，如果不在该队伍中返回 null
     */
    private Integer getOperatorTeamRole(Long userId, Long teamId) {
        List<TeamMember> memberships = teamMemberService.getActiveByUserId(userId);
        return memberships.stream()
                .filter(m -> m.getTeamId().equals(teamId))
                .findFirst()
                .map(TeamMember::getRole)
                .orElse(null);
    }

    /**
     * 获取当前用户在指定成员所在队伍中的 team_member 角色
     *
     * @param userId   当前用户ID
     * @param memberId 队伍成员ID
     * @return team_member.role，如果不在该队伍中返回 null
     */
    private Integer getOperatorTeamRoleByMemberId(Long userId, Long memberId) {
        TeamMember targetMember = teamMemberService.getById(memberId);
        if (targetMember == null) {
            return null;
        }
        return getOperatorTeamRole(userId, targetMember.getTeamId());
    }

    /**
     * 添加队伍成员（同时创建球员记录）
     * <p>
     * 权限控制（基于 team_member.role）：
     * - 队长(role=2)、教练(role=3)、领队(role=4)可以添加任意角色的成员
     * - 队员(role=1)只能添加队员(role=1)
     */
    @PostMapping
    public Result<?> add(@RequestBody AddTeamMemberRequest request,
                         @RequestHeader("Authorization") String authHeader) {
        log.info("添加队伍成员请求 - teamId: {}, userId: {}",
                request.getTeamId(), request.getUserId());

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("添加队伍成员失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("添加队伍成员失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());

        // 3. 参数校验
        if (request.getTeamId() == null) {
            log.warn("添加队伍成员失败：队伍ID为空");
            return Result.error("队伍ID不能为空");
        }
        if (request.getUserId() == null) {
            log.warn("添加队伍成员失败：用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 4. 权限校验：基于 team_member.role
        Integer targetRole = request.getRole();
        Integer operatorTeamRole = getOperatorTeamRole(userId, request.getTeamId());

        if (operatorTeamRole == null) {
            log.warn("添加队伍成员失败：操作人不是该队伍成员 - userId: {}, teamId: {}", userId, request.getTeamId());
            return Result.error("只有队伍成员才能添加新成员");
        }

        if (operatorTeamRole == 1) {
            // 队员 - 只能添加队员（role=1）
            if (targetRole == null || targetRole != 1) {
                log.warn("添加队伍成员失败：权限不足 - userId: {}, teamRole: {}, targetRole: {}",
                        userId, operatorTeamRole, targetRole);
                return Result.error("权限不足，队员只能添加新队员角色");
            }
        }
        // team_member.role >= 2（队长/教练/领队）可以添加任意角色
        // 校验 targetRole 必须在有效范围内
        if (targetRole != null && (targetRole < 1 || targetRole > 4)) {
            log.warn("添加队伍成员失败：角色值无效 - targetRole: {}", targetRole);
            return Result.error("角色值无效，仅支持：1-队员，2-队长，3-教练，4-领队");
        }

        teamMemberService.add(request);
        log.info("添加队伍成员成功 - teamId: {}, userId: {}, role: {}",
                request.getTeamId(), request.getUserId(), targetRole);
        return Result.success("添加队伍成员成功");
    }

    /**
     * 查询当前用户的队伍成员信息（用于前端判断是否已加入队伍，决定显示哪个组件）
     * <p>
     * 返回：当前用户活跃的 TeamMember 列表
     * - 空数组 = 用户未加入任何队伍，需要显示创建/加入队伍的组件
     * - 非空 = 用户已加入队伍，显示队伍管理组件
     * <p>
     * GET /api/team-members/current
     */
    @GetMapping("/current")
    public Result<List<TeamMember>> getCurrentUserMembership(
            @RequestHeader("Authorization") String authHeader) {
        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("查询当前用户队伍信息失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("查询当前用户队伍信息失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        log.info("查询当前用户队伍信息 - userId: {}", userId);

        List<TeamMember> memberships = teamMemberService.getActiveByUserId(userId);
        return Result.success(memberships);
    }

    /**
     * 查询指定球队的成员列表
     * <p>
     * GET /api/team-members/by-team?teamId=1
     */
    @GetMapping("/by-team")
    public Result<List<?>> getMembersByTeam(@RequestParam Long teamId) {
        log.info("查询球队成员列表 - teamId: {}", teamId);

        if (teamId == null || teamId <= 0) {
            log.warn("查询失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        // 1. 获取所有队伍成员
        List<TeamMember> members = teamMemberService.getMembersByTeamId(teamId);

        // 2. 获取所有球员信息（有球员记录的成员）
        List<PlayerInfoVO> players = playerService.getPlayersByTeamId(teamId.intValue());
        Map<Long, PlayerInfoVO> playerMap = players.stream()
                .collect(Collectors.toMap(PlayerInfoVO::getMemberId, p -> p, (a, b) -> a));

        // 3. 合并：有球员记录的返回 PlayerInfoVO（含球衣名、号码等），否则返回 TeamMember
        List<Object> result = new ArrayList<>(members.size());
        for (TeamMember member : members) {
            PlayerInfoVO playerInfo = playerMap.get(member.getId());
            if (playerInfo != null) {
                result.add(playerInfo);
            } else {
                result.add(member);
            }
        }

        return Result.success(result);
    }

    /**
     * 更新队伍成员信息（含球员信息的联动更新）
     * <p>
     * PUT /api/team-members/{id}/portrait
     * <p>
     * 权限控制（基于 team_member.role）：
     * - 成员本人可以修改
     * - 队长(role=2)、教练(role=3)、领队(role=4)可以修改任意成员
     * <p>
     * 内容控制：
     * - 若有球员记录：更新 team_member（定妆照）+ player（球衣名、号码、位置）
     * - 若无球员记录：仅更新 team_member（定妆照）
     */
    @PutMapping("/{id}/portrait")
    public Result<?> updateMember(@PathVariable Long id,
                                   @RequestBody UpdateTeamMemberRequest request,
                                   @RequestHeader("Authorization") String authHeader) {
        log.info("更新队伍成员信息请求 - memberId: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("更新队伍成员信息失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("更新队伍成员信息失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long currentUserId = Long.valueOf(claims.getSubject());

        // 3. 参数校验
        if (id == null || id <= 0) {
            log.warn("更新队伍成员信息失败：成员ID无效");
            return Result.error("成员ID无效");
        }

        // 4. 获取操作人在该队伍中的 team_member.role 作为权限依据
        Integer operatorTeamRole = getOperatorTeamRoleByMemberId(currentUserId, id);

        // 5. 调用 Service 执行
        try {
            teamMemberService.updateMember(id, request, currentUserId, operatorTeamRole);
            log.info("更新队伍成员信息成功 - memberId: {}", id);
            return Result.success("更新成功");
        } catch (RuntimeException e) {
            log.warn("更新队伍成员信息失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出队伍（将状态改为 3-已退出）
     * <p>
     * 权限控制（基于 team_member.role）：
     * - 成员本人可以退出
     * - 队长(role=2)、教练(role=3)、领队(role=4)可以操作任意成员退出
     */
    @PutMapping("/{id}/quit")
    public Result<?> quit(@PathVariable Long id,
                          @RequestHeader("Authorization") String authHeader) {
        log.info("退出队伍请求 - memberId: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("退出队伍失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("退出队伍失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long currentUserId = Long.valueOf(claims.getSubject());

        // 3. 查询要退出的成员信息
        TeamMember member = teamMemberService.getById(id);
        if (member == null) {
            log.warn("退出队伍失败：成员不存在 - memberId: {}", id);
            return Result.error("成员不存在");
        }

        // 4. 权限校验（基于 team_member.role）
        boolean isSelf = member.getUserId().equals(currentUserId);
        Integer operatorTeamRole = getOperatorTeamRole(currentUserId, member.getTeamId());
        boolean hasPermission = operatorTeamRole != null && operatorTeamRole >= 2;

        if (!isSelf && !hasPermission) {
            log.warn("退出队伍失败：权限不足 - currentUserId: {}, teamRole: {}, memberUserId: {}",
                    currentUserId, operatorTeamRole, member.getUserId());
            return Result.error("权限不足，只有成员本人或管理员可以操作退出");
        }

        // 5. 执行退出（状态改为 3-已退出）
        teamMemberService.quit(id);
        log.info("退出队伍成功 - memberId: {}, operator: {}", id, isSelf ? "本人" : "管理员");
        return Result.success("退出队伍成功");
    }

    /**
     * 为教练/领队（role >= 3）添加球员记录
     * <p>
     * 教练、领队等角色也可以上场踢球，此接口为指定成员创建对应的球员记录。
     * <p>
     * POST /api/team-members/{memberId}/player-registration
     * <p>
     * 权限控制（基于 team_member.role）：
     * - 成员本人可以操作
     * - 队长(role=2)、教练(role=3)、领队(role=4)可以操作任意成员
     */
    @PostMapping("/{memberId}/player-registration")
    public Result<?> addPlayerForCoach(@PathVariable Long memberId,
                                        @RequestBody AddPlayerForCoachRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        log.info("为教练/领队添加球员记录请求 - memberId: {}", memberId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("为教练/领队添加球员记录失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("为教练/领队添加球员记录失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long currentUserId = Long.valueOf(claims.getSubject());

        // 3. 参数校验
        if (memberId == null || memberId <= 0) {
            log.warn("为教练/领队添加球员记录失败：成员ID无效");
            return Result.error("成员ID无效");
        }

        // 4. 获取操作人在该队伍中的 team_member.role 作为权限依据
        Integer operatorTeamRole = getOperatorTeamRoleByMemberId(currentUserId, memberId);

        // 5. 调用 Service 执行
        try {
            teamMemberService.addPlayerForCoach(memberId, request, currentUserId, operatorTeamRole);
            log.info("为教练/领队添加球员记录成功 - memberId: {}", memberId);
            return Result.success("球员记录添加成功");
        } catch (RuntimeException e) {
            log.warn("为教练/领队添加球员记录失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}