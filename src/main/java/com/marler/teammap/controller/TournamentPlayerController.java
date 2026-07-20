package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.response.TournamentPlayerInfoVO;
import com.marler.teammap.pojo.TournamentPlayer;
import com.marler.teammap.service.TournamentPlayerService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tournament-players")
public class TournamentPlayerController {

    @Autowired
    private TournamentPlayerService tournamentPlayerService;

    /**
     * 球队批量报名球员参赛
     * <p>
     * POST /api/tournament-players/batch-register
     * 请求体：{ "tournamentId": 1, "teamId": 1, "userIds": [1, 2, 3, 4] }
     * 报名后所有球员状态为 0-审核中
     */
    @PostMapping("/batch-register")
    public Result<?> registerBatch(@RequestBody Map<String, Object> body,
                                   @RequestHeader("Authorization") String authHeader) {
        Long tournamentId = body.get("tournamentId") != null
                ? ((Number) body.get("tournamentId")).longValue() : null;
        Long teamId = body.get("teamId") != null
                ? ((Number) body.get("teamId")).longValue() : null;
        @SuppressWarnings("unchecked")
        List<Integer> userIdsRaw = (List<Integer>) body.get("userIds");
        List<Long> userIds = userIdsRaw != null
                ? userIdsRaw.stream().map(Long::valueOf).collect(Collectors.toList()) : null;

        log.info("球队批量报名球员参赛 - tournamentId: {}, teamId: {}, userIds数量: {}",
                tournamentId, teamId, userIds != null ? userIds.size() : 0);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("批量报名失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("批量报名失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long operatorUserId = Long.valueOf(claims.getSubject());

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("批量报名失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("批量报名失败：球队ID无效");
            return Result.error("球队ID无效");
        }
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量报名失败：用户ID列表为空");
            return Result.error("用户ID列表不能为空");
        }

        // 4. 调用业务逻辑
        try {
            String result = tournamentPlayerService.registerBatch(tournamentId, teamId, userIds, operatorUserId);
            log.info("球队批量报名球员参赛完成 - tournamentId: {}, teamId: {}, result: {}",
                    tournamentId, teamId, result);
            return Result.success(result);
        } catch (RuntimeException e) {
            log.warn("批量报名失败 - tournamentId: {}, teamId: {}, reason: {}",
                    tournamentId, teamId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 球员单独报名参赛
     * <p>
     * POST /api/tournament-players/register
     * 请求体：{ "tournamentId": 1 }
     * 报名后球员状态为 0-审核中，用户ID从Token中获取
     */
    @PostMapping("/register")
    public Result<TournamentPlayer> register(@RequestBody Map<String, Object> body,
                                             @RequestHeader("Authorization") String authHeader) {
        Long tournamentId = body.get("tournamentId") != null
                ? ((Number) body.get("tournamentId")).longValue() : null;

        log.info("球员单独报名参赛 - tournamentId: {}", tournamentId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("报名失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("报名失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("报名失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }

        // 4. 调用业务逻辑
        try {
            TournamentPlayer tournamentPlayer = tournamentPlayerService.register(tournamentId, userId, userId);
            log.info("球员单独报名参赛成功 - id: {}, tournamentId: {}, userId: {}",
                    tournamentPlayer.getId(), tournamentId, userId);
            return Result.success(tournamentPlayer);
        } catch (RuntimeException e) {
            log.warn("报名失败 - tournamentId: {}, reason: {}",
                    tournamentId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 单独审批球员准入
     * <p>
     * PATCH /api/tournament-players/{id}/status
     * 请求体：{ "status": 1 }  → 通过（可出战）
     *          { "status": 3 }  → 拒绝（退出）
     * 权限：role >= 3（赛事管理员可审批自己创建的赛事，系统管理员可审批所有）
     */
    @PatchMapping("/{id}/status")
    public Result<?> approve(@PathVariable Long id,
                             @RequestBody Map<String, Integer> body,
                             @RequestHeader("Authorization") String authHeader) {
        Integer status = body.get("status");
        log.info("审批球员准入 - id: {}, status: {}", id, status);

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
        if (id == null || id <= 0) {
            log.warn("审批失败：记录ID无效");
            return Result.error("记录ID无效");
        }
        if (status == null) {
            log.warn("审批失败：状态不能为空");
            return Result.error("状态不能为空");
        }

        // 4. 调用业务逻辑
        try {
            tournamentPlayerService.approve(id, status, userId, role);
            String statusText = status == 1 ? "通过" : "拒绝";
            log.info("审批球员准入成功 - id: {}, status: {}, userId: {}", id, status, userId);
            return Result.success("审批" + statusText + "成功");
        } catch (RuntimeException e) {
            log.warn("审批球员准入失败 - id: {}, reason: {}", id, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询赛事的所有球员报名信息
     * <p>
     * GET /api/tournament-players?tournamentId=1
     */
    @GetMapping
    public Result<List<TournamentPlayerInfoVO>> getPlayers(@RequestParam Long tournamentId) {
        log.info("查询赛事球员报名信息 - tournamentId: {}", tournamentId);

        if (tournamentId == null || tournamentId <= 0) {
            log.warn("查询失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }

        List<TournamentPlayerInfoVO> list = tournamentPlayerService.getPlayersByTournamentId(tournamentId);
        return Result.success(list);
    }

    /**
     * 按球队查询赛事的球员报名信息
     * <p>
     * GET /api/tournament-players/by-team?tournamentId=1&teamId=1
     */
    @GetMapping("/by-team")
    public Result<List<TournamentPlayerInfoVO>> getPlayersByTeam(@RequestParam Long tournamentId,
                                                                  @RequestParam Long teamId) {
        log.info("按球队查询赛事球员报名信息 - tournamentId: {}, teamId: {}", tournamentId, teamId);

        if (tournamentId == null || tournamentId <= 0) {
            log.warn("查询失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("查询失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        List<TournamentPlayerInfoVO> list = tournamentPlayerService.getPlayersByTournamentIdAndTeamId(tournamentId, teamId);
        return Result.success(list);
    }
}
