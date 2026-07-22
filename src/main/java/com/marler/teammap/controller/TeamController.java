package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTeamRequest;
import com.marler.teammap.dto.request.UpdateTeamRequest;
import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.service.TeamService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * 根据运动类型查询球队列表（用于主办方邀请时选择球队）
     */
    @GetMapping("/type/{type}")
    public Result<List<Team>> getByType(@PathVariable Integer type) {
        log.info("根据运动类型查询球队列表 - type: {}", type);

        if (type == null || type < 1 || type > 3) {
            log.warn("查询失败：无效的运动类型 - type: {}", type);
            return Result.error("无效的运动类型，仅支持：1-足球，2-篮球，3-排球");
        }

        List<Team> list = teamService.getTeamsByType(type);
        return Result.success(list);
    }

    /**
     * 添加球队
     * <p>
     * 权限控制：
     * - 系统管理员（role=4）可以添加球队
     * - 赛事管理员（role=3）可以添加球队
     */
    @PostMapping
    public Result<?> add(@RequestBody AddTeamRequest request,
                         @RequestHeader("Authorization") String authHeader) {
        log.info("添加球队请求 - name: {}", request.getTeam() != null ? request.getTeam().getName() : null);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("添加球队失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("添加球队失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        Team team = request.getTeam();

        // 3. 参数校验
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            log.warn("添加球队失败：队伍名称为空");
            return Result.error("队伍名称不能为空");
        }
        if (team.getType() == null) {
            log.warn("添加球队失败：运动类型为空");
            return Result.error("运动类型不能为空");
        }
        if (team.getGender() == null) {
            log.warn("添加球队失败：性别类型为空");
            return Result.error("性别类型不能为空");
        }
        if (request.getRank() == null) {
            log.warn("添加球队失败：队伍级别为空");
            return Result.error("队伍级别不能为空");
        }

        // 4. 权限校验：根据角色限制可创建的队伍级别
        Integer rank = request.getRank();
        // rank: 1-院队，2-校队，3-班队
        // role: 1-普通用户，2-球员，3-赛事管理员，4-系统管理员
        if (role == null) {
            log.warn("添加球队失败：权限不足 - userId: {}, role: null", userId);
            return Result.error("权限不足");
        }

        boolean allowed = false;
        switch (role) {
            case 1: // 普通用户 - 只能创建班队（rank=3）
                if (rank == 3) allowed = true;
                break;
            case 2: // 球员 - 可以创建班队（rank=3）和院队（rank=1）
                if (rank == 3 || rank == 1) allowed = true;
                break;
            case 3: // 赛事管理员
            case 4: // 系统管理员 - 可以创建所有级别
                if (rank == 1 || rank == 2 || rank == 3) allowed = true;
                break;
        }

        if (!allowed) {
            log.warn("添加球队失败：权限不足 - userId: {}, role: {}, rank: {}", userId, role, rank);
            String allowedDesc;
            if (role == 1) allowedDesc = "仅允许创建班队";
            else if (role == 2) allowedDesc = "仅允许创建班队和院队";
            else allowedDesc = "不允许创建该级别的队伍";
            return Result.error("权限不足，" + allowedDesc);
        }

        teamService.add(request, userId);
        log.info("添加球队成功 - name: {}, id: {}, creatorId: {}", team.getName(), team.getId(), userId);
        return Result.success("添加球队成功");
    }

    /**
     * 修改球队信息
     * <p>
     * 权限控制：
     * - 系统管理员（role=4）可以修改所有球队
     * - 赛事管理员（role=3）可以修改球队
     */
    @PutMapping("/{teamId}")
    public Result<?> update(@PathVariable Long teamId,
                            @RequestBody UpdateTeamRequest request,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("修改球队信息请求 - teamId: {}", teamId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("修改球队失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("修改球队失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能修改球队
        if (role == null || role < 3) {
            log.warn("修改球队失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        request.setTeamId(teamId);
        teamService.update(request);
        log.info("修改球队信息成功 - teamId: {}", teamId);
        return Result.success("修改球队信息成功");
    }

    /**
     * 删除球队
     * <p>
     * 权限控制：
     * - 系统管理员（role=4）可以删除球队
     * - 赛事管理员（role=3）可以删除球队
     */
    @DeleteMapping("/{teamId}")
    public Result<?> delete(@PathVariable Long teamId,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("删除球队请求 - teamId: {}", teamId);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("删除球队失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("删除球队失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能删除球队
        if (role == null || role < 3) {
            log.warn("删除球队失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        teamService.delete(teamId);
        log.info("删除球队成功 - teamId: {}", teamId);
        return Result.success("删除球队成功");
    }

    /**
     * 查询所有球队信息
     */
    @GetMapping
    public Result<List<TeamInfoVO>> list() {
        log.info("查询所有球队信息");
        List<TeamInfoVO> list = teamService.getAllTeamInfo();
        return Result.success(list);
    }
}
