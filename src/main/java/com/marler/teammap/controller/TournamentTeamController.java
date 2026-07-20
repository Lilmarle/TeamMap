package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTournamentTeamRequest;
import com.marler.teammap.dto.response.TournamentTeamInfoVO;
import com.marler.teammap.dto.response.TournamentTeamSimpleVO;
import com.marler.teammap.service.TournamentTeamService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tournament-teams")
public class TournamentTeamController {

    @Autowired
    private TournamentTeamService tournamentTeamService;

    /**
     * 创建赛事-球队关联（球队申请加入 / 主办方邀请）
     *
     * - 如果调用者是管理员（role >= 3），则直接创建已通过的关系（邀请）
     * - 如果调用者是普通用户，则创建待审批的关系（申请）
     *
     * POST /api/tournament-teams
     * 请求体：{ "tournamentId": 1, "teamId": 1 }
     */
    @PostMapping
    public Result<?> create(@RequestBody AddTournamentTeamRequest request,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("创建赛事-球队关联 - tournamentId: {}, teamId: {}", request.getTournamentId(), request.getTeamId());

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("操作失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("操作失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);

        // 3. 参数校验
        Long tournamentId = request.getTournamentId();
        Long teamId = request.getTeamId();
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("操作失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("操作失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        // 4. 根据角色决定行为：管理员邀请 vs 普通用户申请
        try {
            if (role != null && role >= 3) {
                tournamentTeamService.invite(tournamentId, teamId, userId, role);
                log.info("主办方邀请球队加入赛事成功 - tournamentId: {}, teamId: {}, userId: {}",
                        tournamentId, teamId, userId);
                return Result.success("球队已加入赛事");
            } else {
                tournamentTeamService.apply(tournamentId, teamId, userId);
                log.info("球队申请加入赛事成功 - tournamentId: {}, teamId: {}, userId: {}",
                        tournamentId, teamId, userId);
                return Result.success("球队已加入赛事");
            }
        } catch (RuntimeException e) {
            log.warn("创建赛事-球队关联失败 - tournamentId: {}, teamId: {}, reason: {}",
                    tournamentId, teamId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询与赛事关联的球队列表
     */
    @GetMapping("/{tournamentId}/teams")
    public Result<List<TournamentTeamInfoVO>> getTeams(@PathVariable Long tournamentId) {
        log.info("查询与赛事关联的球队列表 - tournamentId: {}", tournamentId);

        if (tournamentId == null || tournamentId <= 0) {
            log.warn("查询失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }

        List<TournamentTeamInfoVO> list = tournamentTeamService.getTeamsByTournamentId(tournamentId);
        return Result.success(list);
    }

    /**
     * 查询某赛事的球队（简化版，仅返回 teamId + 队名 + logo）
     */
    @GetMapping("/{tournamentId}/teams/simple")
    public Result<List<TournamentTeamSimpleVO>> getSimpleTeams(@PathVariable Long tournamentId) {
        log.info("查询某赛事的球队简化信息 - tournamentId: {}", tournamentId);

        if (tournamentId == null || tournamentId <= 0) {
            log.warn("查询失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }

        List<TournamentTeamSimpleVO> list = tournamentTeamService.getSimpleTeamsByTournamentId(tournamentId);
        return Result.success(list);
    }

    /**
     * 审批球队申请（通过/驳回）
     * 权限：role >= 3（赛事管理员可审批自己创建的赛事，系统管理员可审批所有）
     */
    @PatchMapping("/{relId}/status")
    public Result<?> approve(@PathVariable Long relId,
                             @RequestBody Map<String, Integer> body,
                             @RequestHeader("Authorization") String authHeader) {
        Integer status = body.get("status");
        log.info("审批球队申请 - relId: {}, status: {}", relId, status);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("审批失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("审批失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);

        // 3. 参数校验
        if (relId == null || relId <= 0) {
            log.warn("审批失败：申请记录ID无效");
            return Result.error("申请记录ID无效");
        }
        if (status == null) {
            log.warn("审批失败：状态不能为空");
            return Result.error("状态不能为空");
        }

        // 4. 调用业务逻辑
        try {
            tournamentTeamService.approve(relId, status, userId, role);
            String statusText = status == 2 ? "通过" : "驳回";
            log.info("审批球队申请成功 - relId: {}, status: {}, userId: {}", relId, status, userId);
            return Result.success("审批" + statusText + "成功");
        } catch (RuntimeException e) {
            log.warn("审批球队申请失败 - relId: {}, reason: {}", relId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量创建赛事-球队关联（主办方批量邀请）
     * 权限：role >= 3（赛事管理员可邀请自己创建的赛事，系统管理员可邀请所有）
     *
     * POST /api/tournament-teams/batch
     * 请求体：{ "tournamentId": 1, "teamIds": [1, 2, 3, 4] }
     */
    @PostMapping("/batch")
    public Result<?> createBatch(@RequestBody Map<String, Object> body,
                                  @RequestHeader("Authorization") String authHeader) {
        Long tournamentId = body.get("tournamentId") != null
                ? ((Number) body.get("tournamentId")).longValue() : null;
        @SuppressWarnings("unchecked")
        List<Integer> teamIdsRaw = (List<Integer>) body.get("teamIds");
        List<Long> teamIds = teamIdsRaw != null
                ? teamIdsRaw.stream().map(Long::valueOf).collect(Collectors.toList()) : null;

        log.info("批量创建赛事-球队关联 - tournamentId: {}, teamIds数量: {}",
                tournamentId, teamIds != null ? teamIds.size() : 0);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("批量操作失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("批量操作失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("批量操作失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamIds == null || teamIds.isEmpty()) {
            log.warn("批量操作失败：球队ID列表为空");
            return Result.error("球队ID列表不能为空");
        }

        // 4. 调用业务逻辑
        try {
            String result = tournamentTeamService.inviteBatch(tournamentId, teamIds, userId, role);
            log.info("批量创建赛事-球队关联完成 - tournamentId: {}, result: {}", tournamentId, result);
            return Result.success(result);
        } catch (RuntimeException e) {
            log.warn("批量操作失败 - tournamentId: {}, reason: {}",
                    tournamentId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 主办方从赛事中移除球队
     * 权限：role >= 3（赛事管理员可移除自己创建的赛事中的球队，系统管理员可移除所有）
     */
    @DeleteMapping("/{tournamentId}/{teamId}")
    public Result<?> removeTeam(@PathVariable Long tournamentId,
                                @PathVariable Long teamId,
                                @RequestHeader("Authorization") String authHeader) {
        log.info("主办方从赛事中移除球队 - tournamentId: {}, teamId: {}", tournamentId, teamId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("移除失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("移除失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("移除失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("移除失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        // 4. 调用业务逻辑
        try {
            tournamentTeamService.removeTeam(tournamentId, teamId, userId, role);
            log.info("主办方从赛事中移除球队成功 - tournamentId: {}, teamId: {}, userId: {}",
                    tournamentId, teamId, userId);
            return Result.success("球队已从赛事中移除");
        } catch (RuntimeException e) {
            log.warn("从赛事中移除球队失败 - tournamentId: {}, teamId: {}, reason: {}",
                    tournamentId, teamId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
