package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.service.PlayerService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private com.marler.teammap.service.TeamMemberService teamMemberService;

    /**
     * 查询所有球员信息
     * <p>
     * GET /api/players
     */
    @GetMapping
    public Result<List<PlayerInfoVO>> getAllPlayers() {
        log.info("查询所有球员信息");
        List<PlayerInfoVO> list = playerService.getAllPlayers();
        return Result.success(list);
    }

    /**
     * 按用户ID查询球员信息
     * <p>
     * GET /api/players/by-user?userId=1
     */
    @GetMapping("/by-user")
    public Result<PlayerInfoVO> getPlayerByUserId(@RequestParam Integer userId) {
        log.info("按用户ID查询球员信息 - userId: {}", userId);

        if (userId == null || userId <= 0) {
            log.warn("查询失败：用户ID无效");
            return Result.error("用户ID无效");
        }

        PlayerInfoVO playerInfo = playerService.getPlayerInfoByUserId(userId);
        return Result.success(playerInfo);
    }

    /**
     * 按球队查询球员信息
     * <p>
     * GET /api/players/by-team?teamId=1
     */
    @GetMapping("/by-team")
    public Result<List<PlayerInfoVO>> getPlayersByTeam(@RequestParam Integer teamId) {
        log.info("按球队查询球员信息 - teamId: {}", teamId);

        if (teamId == null || teamId <= 0) {
            log.warn("查询失败：球队ID无效");
            return Result.error("球队ID无效");
        }

        List<PlayerInfoVO> list = playerService.getPlayersByTeamId(teamId);
        return Result.success(list);
    }

    /**
     * 修改球员信息
     * <p>
     * PUT /api/players/{id}
     * <p>
     * 权限控制：
     * - 系统管理员（role=4）可以修改所有球员信息
     * - 球员本人（player.userId = 当前用户ID）可以修改自己的信息
     */
    @PutMapping("/{id}")
    public Result<?> updatePlayer(@PathVariable Integer id,
                                  @RequestBody Player player,
                                  @RequestHeader("Authorization") String authHeader) {
        log.info("修改球员信息请求 - playerId: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("修改球员信息失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("修改球员信息失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验
        // 3.1 查询要修改的球员信息
        Player existingPlayer = playerService.getById(id);
        if (existingPlayer == null) {
            log.warn("修改球员信息失败：球员不存在 - playerId: {}", id);
            return Result.error("球员不存在");
        }

        // 3.2 系统管理员（role=4）可以修改所有球员信息
        if (role == 4) {
            log.info("系统管理员修改球员信息 - adminUserId: {}, targetPlayerId: {}", userId, id);
        } else {
            // 3.3 球员本人可以修改自己的信息
            if (!existingPlayer.getUserId().equals(userId.intValue())) {
                log.warn("修改球员信息失败：权限不足 - userId: {}, role: {}, targetPlayerUserId: {}",
                        userId, role, existingPlayer.getUserId());
                return Result.error("权限不足，只能修改自己的球员信息");
            }
            log.info("球员修改自己的信息 - userId: {}, playerId: {}", userId, id);
        }

        // 4. 设置球员ID并执行更新
        player.setId(id);
        // 保持原有的userId和tmId不变（不允许通过此接口修改关联关系）
        player.setUserId(existingPlayer.getUserId());
        player.setTmId(existingPlayer.getTmId());

        playerService.update(player);
        log.info("球员信息修改成功 - playerId: {}", id);
        return Result.success("修改成功");
    }

    /**
     * 注册球员（创建球员记录）
     * <p>
     * POST /api/players
     * <p>
     * 为当前登录用户在选中的球队中创建球员记录。
     * 需要当前用户已是该球队的成员（有 team_member 记录）。
     */
    @PostMapping
    public Result<?> addPlayer(@RequestBody Player player,
                               @RequestHeader("Authorization") String authHeader) {
        log.info("注册球员请求");

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("注册球员失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("注册球员失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer currentUserId = Integer.valueOf(claims.getSubject());

        // 3. 参数校验
        if (player.getTmId() == null) {
            log.warn("注册球员失败：缺少队伍成员关联ID");
            return Result.error("缺少队伍成员信息");
        }

        // 4. 校验该 team_member 记录是否属于当前用户
        com.marler.teammap.pojo.TeamMember member = 
            teamMemberService.getById(player.getTmId().longValue());
        if (member == null) {
            log.warn("注册球员失败：队伍成员记录不存在 - tmId: {}", player.getTmId());
            return Result.error("队伍成员记录不存在");
        }
        if (!member.getUserId().equals(currentUserId.longValue())) {
            log.warn("注册球员失败：权限不足 - userId: {}, tmId: {}", currentUserId, player.getTmId());
            return Result.error("权限不足，只能为自己注册球员信息");
        }

        // 5. 检查是否已存在球员记录
        PlayerInfoVO existing = playerService.getPlayerInfoByUserId(currentUserId);
        if (existing != null) {
            log.warn("注册球员失败：已存在球员记录 - userId: {}", currentUserId);
            return Result.error("已存在球员记录，请使用修改功能");
        }

        // 6. 创建球员记录
        player.setUserId(currentUserId);
        player.setStatus(1); // 默认可出战
        playerService.add(player);
        log.info("球员注册成功 - userId: {}, tmId: {}, jerseyName: {}",
                currentUserId, player.getTmId(), player.getJerseyName());
        return Result.success("注册成功");
    }

    /**
     * 删除球员
     * <p>
     * DELETE /api/players/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePlayer(@PathVariable Integer id) {
        log.info("删除球员请求 - playerId: {}", id);

        Player existingPlayer = playerService.getById(id);
        if (existingPlayer == null) {
            log.warn("删除球员失败：球员不存在 - playerId: {}", id);
            return Result.error("球员不存在");
        }

        playerService.deleteById(id);
        log.info("球员删除成功 - playerId: {}", id);
        return Result.success("删除成功");
    }
}
