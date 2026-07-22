package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddMatchEventRequest;
import com.marler.teammap.dto.request.UpdateMatchEventRequest;
import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.pojo.MatchEvent;
import com.marler.teammap.service.MatchEventService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/match-events")
public class MatchEventController {

    @Autowired
    private MatchEventService matchEventService;

    /**
     * 添加比赛事件
     * <p>
     * 权限控制：
     * - 赛事管理员（role=3）可以添加比赛事件
     * - 系统管理员（role=4）可以添加比赛事件
     * POST /api/match-events
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<MatchEvent> add(@RequestBody AddMatchEventRequest request,
                                  @RequestHeader("Authorization") String authHeader) {
        log.info("添加比赛事件请求");

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("添加比赛事件失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("添加比赛事件失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能添加比赛事件
        if (role == null || role < 3) {
            log.warn("添加比赛事件失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        MatchEvent event = matchEventService.add(request);
        log.info("添加比赛事件成功 - eventId: {}", event.getId());
        return Result.success(event);
    }

    /**
     * 修改比赛事件
     * <p>
     * 权限控制：
     * - 赛事管理员（role=3）可以修改比赛事件
     * - 系统管理员（role=4）可以修改比赛事件
     * PUT /api/match-events/{id}
     */
    @PutMapping("/{id}")
    public Result<MatchEvent> update(@PathVariable Long id,
                                     @RequestBody UpdateMatchEventRequest request,
                                     @RequestHeader("Authorization") String authHeader) {
        log.info("修改比赛事件请求 - id: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("修改比赛事件失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("修改比赛事件失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3
        if (role == null || role < 3) {
            log.warn("修改比赛事件失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 执行修改
        try {
            MatchEvent event = matchEventService.update(id, request);
            log.info("修改比赛事件成功 - id: {}, operatorId: {}", id, userId);
            return Result.success(event);
        } catch (RuntimeException e) {
            log.warn("修改比赛事件失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除比赛事件
     * <p>
     * 权限控制：
     * - 赛事管理员（role=3）可以删除比赛事件
     * - 系统管理员（role=4）可以删除比赛事件
     * DELETE /api/match-events/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("删除比赛事件请求 - id: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("删除比赛事件失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("删除比赛事件失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3
        if (role == null || role < 3) {
            log.warn("删除比赛事件失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 执行删除
        try {
            matchEventService.delete(id);
            log.info("删除比赛事件成功 - id: {}, operatorId: {}", id, userId);
            return Result.success("比赛事件已删除");
        } catch (RuntimeException e) {
            log.warn("删除比赛事件失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据比赛ID查询所有事件（含球员名称、球衣号码等），按事件发生时间升序排列
     * GET /api/match-events/{matchId}
     */
    @GetMapping("/{matchId}")
    public Result<List<MatchEventVO>> getByMatchId(@PathVariable Integer matchId) {
        List<MatchEventVO> events = matchEventService.getEventVOByMatchId(matchId);
        return Result.success(events);
    }

    /**
     * 查询比赛事件统计（按队伍分组）
     * GET /api/match-events/{matchId}/stats
     */
    @GetMapping("/{matchId}/stats")
    public Result<List<MatchEventStatsVO>> getEventStats(@PathVariable Integer matchId) {
        List<MatchEventStatsVO> stats = matchEventService.getEventStatsByMatchId(matchId);
        return Result.success(stats);
    }
}