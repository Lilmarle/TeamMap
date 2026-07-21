package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.pojo.TeamMember;
import com.marler.teammap.service.TeamMemberService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/team-members")
public class TeamMemberController {

    @Autowired
    private TeamMemberService teamMemberService;

    /**
     * 添加队伍成员（同时创建球员记录）
     * <p>
     * 权限控制：
     * - user.role = 1（普通用户）：只能添加角色为队员（role=1）的成员
     * - user.role >= 2（球员及以上）：可以添加教练（role=3）或领队（role=4）
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

        Integer currentUserRole = claims.get("role", Integer.class);
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

        // 4. 权限校验：根据当前用户角色限制可设置的角色
        Integer targetRole = request.getRole();
        // TeamMember.role: 1-队员，2-队长，3-教练，4-领队
        // User.role: 1-普通用户，2-球员，3-赛事管理员，4-系统管理员

        if (currentUserRole == null) {
            log.warn("添加队伍成员失败：权限不足 - userId: {}, role: null", userId);
            return Result.error("权限不足");
        }

        if (currentUserRole == 1) {
            // 普通用户 - 只能添加队员（role=1）
            if (targetRole == null || targetRole != 1) {
                log.warn("添加队伍成员失败：权限不足 - userId: {}, role: {}, targetRole: {}",
                        userId, currentUserRole, targetRole);
                return Result.error("权限不足，普通用户只能添加队员角色");
            }
        }
        // role >= 2 的用户的权限由前端控制，后端不限制
        // 但校验 targetRole 必须在有效范围内
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
     * 查询指定球队的成员列表
     * <p>
     * GET /api/team-members/by-team?teamId=1
     */
    @GetMapping("/by-team")
    public Result<List<TeamMember>> getMembersByTeam(@RequestParam Long teamId) {
        log.info("查询球队成员列表 - teamId: {}", teamId);

        if (teamId == null || teamId <= 0) {
            log.warn("查询失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        List<TeamMember> members = teamMemberService.getMembersByTeamId(teamId);
        return Result.success(members);
    }

    /**
     * 退出队伍（将状态改为 3-已退出）
     * <p>
     * 权限控制：
     * - 成员本人可以退出
     * - user.role >= 2（球员及以上）可以操作任意成员退出
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

        Integer currentUserRole = claims.get("role", Integer.class);
        Long currentUserId = Long.valueOf(claims.getSubject());

        // 3. 查询要退出的成员信息
        TeamMember member = teamMemberService.getById(id);
        if (member == null) {
            log.warn("退出队伍失败：成员不存在 - memberId: {}", id);
            return Result.error("成员不存在");
        }

        // 4. 权限校验
        // 成员本人可以退出 或 user.role >= 2 可以操作
        boolean isSelf = member.getUserId().equals(currentUserId);
        boolean hasPermission = currentUserRole != null && currentUserRole >= 2;

        if (!isSelf && !hasPermission) {
            log.warn("退出队伍失败：权限不足 - currentUserId: {}, role: {}, memberUserId: {}",
                    currentUserId, currentUserRole, member.getUserId());
            return Result.error("权限不足，只有成员本人或管理员可以操作退出");
        }

        // 5. 执行退出（状态改为 3-已退出）
        teamMemberService.quit(id);
        log.info("退出队伍成功 - memberId: {}, operator: {}", id, isSelf ? "本人" : "管理员");
        return Result.success("退出队伍成功");
    }
}
