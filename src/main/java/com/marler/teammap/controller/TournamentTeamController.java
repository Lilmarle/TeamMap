package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.response.TournamentTeamInfoVO;
import com.marler.teammap.dto.response.TournamentTeamSimpleVO;
import com.marler.teammap.service.TournamentTeamService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tournament-team")
public class TournamentTeamController {

    @Autowired
    private TournamentTeamService tournamentTeamService;

    /**
     * 球队申请加入赛事
     * 校验：
     * 1. Team 和 Tournament 的 type（运动类型）必须一致
     * 2. Tournament 的 status 必须为 1（筹办中）
     */
    @PostMapping("/apply/{tournamentId}/{teamId}")
    public Result<?> apply(@PathVariable Long tournamentId,
                           @PathVariable Long teamId,
                           @RequestHeader("Authorization") String authHeader) {
        log.info("球队申请加入赛事 - tournamentId: {}, teamId: {}", tournamentId, teamId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("申请失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("申请失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("申请失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("申请失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        // 4. 调用业务逻辑
        try {
            tournamentTeamService.apply(tournamentId, teamId, userId);
            log.info("球队申请加入赛事成功 - tournamentId: {}, teamId: {}, userId: {}",
                    tournamentId, teamId, userId);
            return Result.success("申请成功，等待审核");
        } catch (RuntimeException e) {
            log.warn("球队申请加入赛事失败 - tournamentId: {}, teamId: {}, reason: {}",
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
    @PutMapping("/approve/{relId}")
    public Result<?> approve(@PathVariable Long relId,
                             @RequestParam Integer status,
                             @RequestHeader("Authorization") String authHeader) {
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
     * 主办方邀请单个球队加入赛事
     * 权限：role >= 3（赛事管理员可邀请自己创建的赛事，系统管理员可邀请所有）
     * 逻辑：直接将球队加入赛事（状态设为已通过）
     */
    @PostMapping("/invite/{tournamentId}/{teamId}")
    public Result<?> invite(@PathVariable Long tournamentId,
                            @PathVariable Long teamId,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("主办方邀请球队加入赛事 - tournamentId: {}, teamId: {}", tournamentId, teamId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("邀请失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("邀请失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("邀请失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }
        if (teamId == null || teamId <= 0) {
            log.warn("邀请失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        // 4. 调用业务逻辑
        try {
            tournamentTeamService.invite(tournamentId, teamId, userId, role);
            log.info("主办方邀请球队加入赛事成功 - tournamentId: {}, teamId: {}, userId: {}",
                    tournamentId, teamId, userId);
            return Result.success("邀请成功，球队已加入赛事");
        } catch (RuntimeException e) {
            log.warn("邀请球队失败 - tournamentId: {}, teamId: {}, reason: {}",
                    tournamentId, teamId, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 主办方批量邀请球队加入赛事
     * 权限：role >= 3（赛事管理员可邀请自己创建的赛事，系统管理员可邀请所有）
     * 逻辑：将选中的多个球队直接加入赛事
     */
    @PostMapping("/invite/batch/{tournamentId}")
    public Result<?> inviteBatch(@PathVariable Long tournamentId,
                                 @RequestBody List<Long> teamIds,
                                 @RequestHeader("Authorization") String authHeader) {
        log.info("主办方批量邀请球队加入赛事 - tournamentId: {}, teamIds: {}", tournamentId, teamIds);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("批量邀请失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("批量邀请失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);

        // 3. 参数校验
        if (tournamentId == null || tournamentId <= 0) {
            log.warn("批量邀请失败：赛事ID无效");
            return Result.error("赛事ID无效");
        }

        // 4. 调用业务逻辑
        try {
            String result = tournamentTeamService.inviteBatch(tournamentId, teamIds, userId, role);
            log.info("主办方批量邀请球队完成 - tournamentId: {}, result: {}", tournamentId, result);
            return Result.success(result);
        } catch (RuntimeException e) {
            log.warn("批量邀请球队失败 - tournamentId: {}, reason: {}",
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
