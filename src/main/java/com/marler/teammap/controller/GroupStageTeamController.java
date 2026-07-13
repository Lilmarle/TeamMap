package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddGroupStageTeamRequest;
import com.marler.teammap.dto.request.BatchAddGroupStageTeamRequest;
import com.marler.teammap.dto.request.TournamentGroupTeamRequest;
import com.marler.teammap.pojo.GroupStageTeam;
import com.marler.teammap.service.GroupStageTeamService;
import com.marler.teammap.service.TournamentService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/group-stage-team")
public class GroupStageTeamController {

    @Autowired
    private GroupStageTeamService groupStageTeamService;

    @Autowired
    private TournamentService tournamentService;

    /**
     * 为一个小组添加一支球队
     * POST /api/group-stage-team
     * 请求体：{ "groupStageId": 1, "teamId": 1 }
     */
    @PostMapping
    public Result<?> addTeam(@RequestBody AddGroupStageTeamRequest request,
                             @RequestHeader("Authorization") String authHeader) {
        log.info("为小组添加一支球队 - groupStageId: {}, teamId: {}", request.getGroupStageId(), request.getTeamId());

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            return Result.error("token无效或已过期");
        }
        Integer role = claims.get("role", Integer.class);
        if (role == null || role < 3) {
            return Result.error("权限不足");
        }

        // 2. 参数校验
        if (request.getGroupStageId() == null) {
            return Result.error("小组ID不能为空");
        }
        if (request.getTeamId() == null) {
            return Result.error("队伍ID不能为空");
        }

        // 3. 添加球队
        groupStageTeamService.addTeamToGroup(request.getGroupStageId(), request.getTeamId());
        log.info("为小组[{}]添加球队[{}]成功", request.getGroupStageId(), request.getTeamId());
        return Result.success("添加球队成功");
    }

    /**
     * 为一个小组批量添加多支球队
     * POST /api/group-stage-team/batch
     * 请求体：{ "groupStageId": 1, "teamIds": [1, 2, 3, 4] }
     */
    @PostMapping("/batch")
    public Result<?> addTeams(@RequestBody BatchAddGroupStageTeamRequest request,
                              @RequestHeader("Authorization") String authHeader) {
        log.info("为小组批量添加球队 - groupStageId: {}, teamIds数量: {}",
                request.getGroupStageId(),
                request.getTeamIds() != null ? request.getTeamIds().size() : 0);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            return Result.error("token无效或已过期");
        }
        Integer role = claims.get("role", Integer.class);
        if (role == null || role < 3) {
            return Result.error("权限不足");
        }

        // 2. 参数校验
        if (request.getGroupStageId() == null) {
            return Result.error("小组ID不能为空");
        }
        if (request.getTeamIds() == null || request.getTeamIds().isEmpty()) {
            return Result.error("队伍ID列表不能为空");
        }

        // 3. 批量添加球队
        groupStageTeamService.addTeamsToGroup(request.getGroupStageId(), request.getTeamIds());
        log.info("为小组[{}]批量添加 {} 支球队成功", request.getGroupStageId(), request.getTeamIds().size());
        return Result.success("批量添加球队成功");
    }

    /**
     * 为整个赛事的所有小组添加球队
     * POST /api/group-stage-team/tournament
     * 请求体：
     * {
     *   "tournamentId": 1,
     *   "assignments": [
     *     { "groupStageId": 1, "teamIds": [1, 2, 3, 4] },
     *     { "groupStageId": 2, "teamIds": [5, 6, 7, 8] }
     *   ]
     * }
     */
    @PostMapping("/tournament")
    public Result<?> addTeamsForTournament(@RequestBody TournamentGroupTeamRequest request,
                                            @RequestHeader("Authorization") String authHeader) {
        log.info("为赛事所有小组添加球队 - tournamentId: {}, assignments数量: {}",
                request.getTournamentId(),
                request.getAssignments() != null ? request.getAssignments().size() : 0);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            return Result.error("token无效或已过期");
        }
        Integer role = claims.get("role", Integer.class);
        if (role == null || role < 3) {
            return Result.error("权限不足");
        }

        // 2. 参数校验
        if (request.getTournamentId() == null) {
            return Result.error("赛事ID不能为空");
        }
        if (request.getAssignments() == null || request.getAssignments().isEmpty()) {
            return Result.error("小组分配列表不能为空");
        }

        // 3. 校验赛事是否存在
        if (tournamentService.getById(request.getTournamentId().longValue()) == null) {
            return Result.error("赛事不存在");
        }

        // 4. 为所有小组添加球队
        groupStageTeamService.addTeamsToTournamentGroups(request.getTournamentId(), request.getAssignments());
        log.info("为赛事[{}]的所有小组添加球队成功", request.getTournamentId());
        return Result.success("为赛事所有小组添加球队成功");
    }

    /**
     * 查看某小组的所有球队
     * GET /api/group-stage-team/group/{groupStageId}
     */
    @GetMapping("/group/{groupStageId}")
    public Result<List<GroupStageTeam>> listByGroup(@PathVariable Integer groupStageId) {
        log.info("查询小组球队列表 - groupStageId: {}", groupStageId);
        List<GroupStageTeam> list = groupStageTeamService.getByGroupStageId(groupStageId);
        return Result.success(list);
    }
}
