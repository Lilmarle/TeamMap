package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTournamentRequest;
import com.marler.teammap.pojo.Tournament;
import com.marler.teammap.service.TournamentService;
import com.marler.teammap.service.UserService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    /**
     * 创建赛事（权限：user.role >= 3）
     */
    @PostMapping
    public Result<?> add(@RequestBody AddTournamentRequest request,
                         @RequestHeader("Authorization") String authHeader) {
        log.info("创建赛事请求 - name: {}", request.getName());

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("创建赛事失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("创建赛事失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能创建赛事
        if (role == null || role < 3) {
            log.warn("创建赛事失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 参数校验
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("创建赛事失败：赛事名称为空");
            return Result.error("赛事名称不能为空");
        }
        if (request.getType() == null) {
            log.warn("创建赛事失败：运动类型为空");
            return Result.error("运动类型不能为空");
        }
        if (request.getStartTime() == null) {
            log.warn("创建赛事失败：开始时间为空");
            return Result.error("开始时间不能为空");
        }
        if (request.getEndTime() == null) {
            log.warn("创建赛事失败：结束时间为空");
            return Result.error("结束时间不能为空");
        }

        // 5. 构建 Tournament 对象
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setCreatorId(userId);
        tournament.setType(request.getType());
        tournament.setStatus(1); // 默认状态：1-筹办中
        tournament.setStartTime(request.getStartTime());
        tournament.setEndTime(request.getEndTime());

        tournamentService.add(tournament);
        log.info("创建赛事成功 - id: {}, name: {}, creatorId: {}", tournament.getId(), request.getName(), userId);
        return Result.success("创建赛事成功");
    }

    /**
     * 查看所有赛事
     */
    @GetMapping
    public Result<List<Tournament>> list() {
        log.info("查询所有赛事");
        List<Tournament> list = tournamentService.getAll();
        return Result.success(list);
    }

    /**
     * 查看自己创建的赛事（需登录）
     */
    @GetMapping("/my")
    public Result<List<Tournament>> my(@RequestHeader("Authorization") String authHeader) {
        log.info("查询自己创建的赛事");

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("查询失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户ID
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("查询失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        log.info("当前用户ID: {}", userId);

        // 3. 查询该用户创建的赛事
        List<Tournament> list = tournamentService.getByCreatorId(userId);
        return Result.success(list);
    }

    /**
     * 删除赛事
     * 权限：role = 4（系统管理员）可删除所有赛事
     *       role = 3（赛事管理员）只能删除自己创建的赛事
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("删除赛事请求 - id: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("删除赛事失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token 获取用户信息
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("删除赛事失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验
        if (role == null || role < 3) {
            log.warn("删除赛事失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. role = 3（赛事管理员）只能删除自己创建的赛事
        if (role == 3) {
            Tournament tournament = tournamentService.getById(id);
            if (tournament == null) {
                log.warn("删除赛事失败：赛事不存在 - id: {}", id);
                return Result.error("赛事不存在");
            }
            if (!tournament.getCreatorId().equals(userId)) {
                log.warn("删除赛事失败：赛事管理员只能删除自己创建的赛事 - userId: {}, creatorId: {}",
                        userId, tournament.getCreatorId());
                return Result.error("只能删除自己创建的赛事");
            }
        }

        tournamentService.deleteById(id);
        log.info("删除赛事成功 - id: {}, operatorId: {}, role: {}", id, userId, role);
        return Result.success("删除赛事成功");
    }
}
