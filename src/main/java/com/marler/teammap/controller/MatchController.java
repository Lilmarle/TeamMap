package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddMatchRequest;
import com.marler.teammap.dto.request.UpdateMatchRequest;
import com.marler.teammap.dto.response.MatchDetailVO;
import com.marler.teammap.pojo.Match;
import com.marler.teammap.service.MatchService;

import java.util.List;
import com.marler.teammap.service.TeamService;
import com.marler.teammap.service.TournamentService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private com.marler.teammap.mapper.MatchMapper matchMapper;

    /**
     * 添加一场比赛
     * POST /api/match
     * 请求体示例：
     * {
     *   "tournamentId": 1,
     *   "team1Id": 1,
     *   "team2Id": 2,
     *   "stage": 2,
     *   "matchTime": "2026-07-15 14:00:00",
     *   "location": "体育馆A"
     * }
     * 说明：
     * - team1Score、team2Score 默认0
     * - status 默认1（未开始）
     * - stage 没填默认0（未指定）
     * - matchTime、location 不填默认为空
     */
    @PostMapping
    public Result<?> add(@RequestBody AddMatchRequest request,
                         @RequestHeader("Authorization") String authHeader) {
        log.info("添加比赛请求 - tournamentId: {}, team1Id: {}, team2Id: {}",
                request.getTournamentId(), request.getTeam1Id(), request.getTeam2Id());

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("添加比赛失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("添加比赛失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能操作
        if (role == null || role < 3) {
            log.warn("添加比赛失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 参数校验
        if (request.getTournamentId() == null) {
            log.warn("添加比赛失败：赛事ID为空");
            return Result.error("赛事ID不能为空");
        }
        if (request.getTeam1Id() == null) {
            log.warn("添加比赛失败：队伍1ID为空");
            return Result.error("队伍1ID不能为空");
        }
        if (request.getTeam2Id() == null) {
            log.warn("添加比赛失败：队伍2ID为空");
            return Result.error("队伍2ID不能为空");
        }
        if (request.getTeam1Id().equals(request.getTeam2Id())) {
            log.warn("添加比赛失败：队伍1和队伍2不能相同");
            return Result.error("队伍1和队伍2不能相同");
        }

        // 5. 校验赛事是否存在
        if (tournamentService.getById(request.getTournamentId().longValue()) == null) {
            log.warn("添加比赛失败：赛事不存在 - tournamentId: {}", request.getTournamentId());
            return Result.error("赛事不存在");
        }

        // 6. 校验球队是否存在
        if (teamService.getById(request.getTeam1Id().longValue()) == null) {
            log.warn("添加比赛失败：队伍1不存在 - team1Id: {}", request.getTeam1Id());
            return Result.error("队伍1不存在");
        }
        if (teamService.getById(request.getTeam2Id().longValue()) == null) {
            log.warn("添加比赛失败：队伍2不存在 - team2Id: {}", request.getTeam2Id());
            return Result.error("队伍2不存在");
        }

        // 7. 调用 Service 添加比赛
        Match match = matchService.add(request);
        log.info("添加比赛成功 - id: {}", match.getId());

        return Result.success(match);
    }

    /**
     * 修改比赛信息（如比赛状态、双方得分等）
     * PUT /api/match/{id}
     * 请求体示例：
     * {
     *   "team1Score": 3,
     *   "team2Score": 1,
     *   "status": 3,
     *   "stage": 2,
     *   "matchTime": "2026-07-15 16:00:00",
     *   "location": "体育馆B"
     * }
     * 说明：只需传入需要修改的字段，未传的字段保持不变
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id,
                            @RequestBody UpdateMatchRequest request,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("修改比赛信息请求 - id: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("修改比赛信息失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("修改比赛信息失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能操作
        if (role == null || role < 3) {
            log.warn("修改比赛信息失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 执行修改
        try {
            Match match = matchService.update(id, request);
            log.info("修改比赛信息成功 - id: {}", id);
            return Result.success(match);
        } catch (RuntimeException e) {
            log.warn("修改比赛信息失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询比赛详情（含双方球队信息）
     * GET /api/match/{id}
     */
    @GetMapping("/{id}")
    public Result<MatchDetailVO> detail(@PathVariable Long id) {
        log.info("查询比赛详情 - id: {}", id);
        MatchDetailVO vo = matchService.getDetail(id);
        if (vo == null) {
            return Result.error("比赛不存在");
        }
        return Result.success(vo);
    }

    /**
     * 查询某赛事的所有比赛（含双方球队信息）
     * GET /api/match/tournament/{tournamentId}
     */
    @GetMapping("/tournament/{tournamentId}")
    public Result<List<MatchDetailVO>> listByTournament(@PathVariable Integer tournamentId) {
        log.info("查询赛事比赛列表 - tournamentId: {}", tournamentId);
        List<MatchDetailVO> list = matchService.listByTournament(tournamentId);
        return Result.success(list);
    }

    /**
     * 查询某只球队参与的所有比赛（含双方球队信息）
     * GET /api/match/team/{teamId}
     */
    @GetMapping("/team/{teamId}")
    public Result<List<MatchDetailVO>> listByTeam(@PathVariable Integer teamId) {
        log.info("查询球队比赛列表 - teamId: {}", teamId);
        List<MatchDetailVO> list = matchService.listByTeam(teamId);
        return Result.success(list);
    }

    /**
     * 查询某小组的所有比赛（含双方球队信息）
     * GET /api/match/group-stage/{groupStageId}
     */
    @GetMapping("/group-stage/{groupStageId}")
    public Result<List<MatchDetailVO>> listByGroupStage(@PathVariable Integer groupStageId) {
        log.info("查询小组比赛列表 - groupStageId: {}", groupStageId);
        List<MatchDetailVO> list = matchService.listByGroupStage(groupStageId);
        return Result.success(list);
    }

    /**
     * 删除比赛
     * DELETE /api/match/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除比赛请求 - id: {}", id);
        matchMapper.deleteById(id);
        log.info("比赛删除成功 - id: {}", id);
        return Result.success("比赛已删除");
    }
}