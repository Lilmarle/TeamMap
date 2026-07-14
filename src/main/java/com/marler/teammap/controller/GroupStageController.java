package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddGroupStageRequest;
import com.marler.teammap.dto.request.BatchAddGroupStageRequest;
import com.marler.teammap.dto.response.GroupStageDetailVO;
import com.marler.teammap.pojo.GroupStage;
import com.marler.teammap.pojo.Tournament;
import com.marler.teammap.service.GroupStageService;
import com.marler.teammap.service.TournamentService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/group-stage")
public class GroupStageController {

    @Autowired
    private GroupStageService groupStageService;

    @Autowired
    private TournamentService tournamentService;

    /**
     * 添加单个小组
     * POST /api/group-stage
     * 请求体示例：
     * {
     *   "tournamentId": 1,
     *   "name": "A组",
     *   "teamCount": 4,
     *   "directAdvance": 1,
     *   "indirectAdvance": 0,
     *   "roundType": 1
     * }
     */
    @PostMapping
    public Result<?> add(@RequestBody AddGroupStageRequest request,
                         @RequestHeader("Authorization") String authHeader) {
        log.info("添加单个小组请求 - tournamentId: {}, name: {}", request.getTournamentId(), request.getName());

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("添加小组失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("添加小组失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能操作
        if (role == null || role < 3) {
            log.warn("添加小组失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 参数校验
        if (request.getTournamentId() == null) {
            log.warn("添加小组失败：赛事ID为空");
            return Result.error("赛事ID不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("添加小组失败：小组名称为空");
            return Result.error("小组名称不能为空");
        }

        // 5. 校验赛事是否存在
        Tournament tournament = tournamentService.getById(request.getTournamentId().longValue());
        if (tournament == null) {
            log.warn("添加小组失败：赛事不存在 - tournamentId: {}", request.getTournamentId());
            return Result.error("赛事不存在");
        }

        // 6. 构建 GroupStage 对象
        GroupStage groupStage = new GroupStage();
        groupStage.setTournamentId(request.getTournamentId());
        groupStage.setName(request.getName());
        groupStage.setTeamCount(request.getTeamCount() != null ? request.getTeamCount() : 0);
        groupStage.setDirectAdvance(request.getDirectAdvance() != null ? request.getDirectAdvance() : 1);
        groupStage.setIndirectAdvance(request.getIndirectAdvance() != null ? request.getIndirectAdvance() : 0);
        groupStage.setRoundType(request.getRoundType() != null ? request.getRoundType() : 1);

        // 7. 添加小组（仅创建小组，不关联队伍）
        groupStageService.add(groupStage);
        log.info("添加小组成功 - id: {}, name: {}, tournamentId: {}", groupStage.getId(), request.getName(), request.getTournamentId());

        return Result.success(groupStage);
    }

    /**
     * 批量添加小组
     * POST /api/group-stage/batch
     * 请求体示例：
     * {
     *   "tournamentId": 1,
     *   "groups": [
     *     {
     *       "name": "A组",
     *       "teamCount": 4,
     *       "directAdvance": 1,
     *       "indirectAdvance": 0,
     *       "roundType": 1,
     *       "teamIds": [1, 2, 3, 4]
     *     },
     *     {
     *       "name": "B组",
     *       "teamCount": 4,
     *       "directAdvance": 1,
     *       "indirectAdvance": 0,
     *       "roundType": 1,
     *       "teamIds": [5, 6, 7, 8]
     *     }
     *   ]
     * }
     */
    @PostMapping("/batch")
    public Result<?> addBatch(@RequestBody BatchAddGroupStageRequest request,
                              @RequestHeader("Authorization") String authHeader) {
        log.info("批量添加小组请求 - tournamentId: {}, groups数量: {}",
                request.getTournamentId(),
                request.getGroups() != null ? request.getGroups().size() : 0);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("批量添加小组失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("批量添加小组失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验
        if (role == null || role < 3) {
            log.warn("批量添加小组失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 参数校验
        if (request.getTournamentId() == null) {
            log.warn("批量添加小组失败：赛事ID为空");
            return Result.error("赛事ID不能为空");
        }
        if (request.getGroups() == null || request.getGroups().isEmpty()) {
            log.warn("批量添加小组失败：小组列表为空");
            return Result.error("小组列表不能为空");
        }

        // 5. 校验赛事是否存在
        Tournament tournament = tournamentService.getById(request.getTournamentId().longValue());
        if (tournament == null) {
            log.warn("批量添加小组失败：赛事不存在 - tournamentId: {}", request.getTournamentId());
            return Result.error("赛事不存在");
        }

        // 6. 构建 GroupStage 列表
        List<GroupStage> groupStages = new ArrayList<>();
        for (BatchAddGroupStageRequest.GroupItem item : request.getGroups()) {
            if (item.getName() == null || item.getName().trim().isEmpty()) {
                log.warn("批量添加小组失败：存在名称为空的小组");
                return Result.error("每个小组的名称不能为空");
            }

            GroupStage groupStage = new GroupStage();
            groupStage.setTournamentId(request.getTournamentId());
            groupStage.setName(item.getName());
            groupStage.setTeamCount(item.getTeamCount() != null ? item.getTeamCount() : 0);
            groupStage.setDirectAdvance(item.getDirectAdvance() != null ? item.getDirectAdvance() : 1);
            groupStage.setIndirectAdvance(item.getIndirectAdvance() != null ? item.getIndirectAdvance() : 0);
            groupStage.setRoundType(item.getRoundType() != null ? item.getRoundType() : 1);
            groupStages.add(groupStage);
        }

        // 7. 调用 Service 批量添加
        groupStageService.addBatch(groupStages);
        log.info("批量添加小组成功 - tournamentId: {}, 共添加 {} 个小组",
                request.getTournamentId(), groupStages.size());

        return Result.success(groupStages);
    }

    /**
     * 查看某赛事的所有小组
     * GET /api/group-stage/tournament/{tournamentId}
     */
    @GetMapping("/tournament/{tournamentId}")
    public Result<List<GroupStage>> listByTournament(@PathVariable Integer tournamentId) {
        log.info("查询赛事小组列表 - tournamentId: {}", tournamentId);
        List<GroupStage> list = groupStageService.getByTournamentId(tournamentId);
        return Result.success(list);
    }

    /**
     * 查看小组详情（含球队成绩）
     * GET /api/group-stage/{id}/detail
     */
    @GetMapping("/{id}/detail")
    public Result<GroupStageDetailVO> detail(@PathVariable Integer id) {
        log.info("查询小组详情 - id: {}", id);
        GroupStageDetailVO vo = groupStageService.getDetail(id);
        if (vo == null) {
            return Result.error("小组不存在");
        }
        return Result.success(vo);
    }

    /**
     * 删除小组（同时删除已分配的球队关联）
     * DELETE /api/group-stage/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id,
                            @RequestHeader("Authorization") String authHeader) {
        log.info("删除小组请求 - id: {}", id);

        // 1. Token 校验
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("删除小组失败：未登录");
            return Result.error("未登录");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            String token = authHeader.replace("Bearer ", "");
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("删除小组失败：Token无效 - {}", e.getMessage());
            return Result.error("token无效或已过期");
        }

        Integer role = claims.get("role", Integer.class);
        Long userId = Long.valueOf(claims.getSubject());

        // 3. 权限校验：role >= 3 才能操作
        if (role == null || role < 3) {
            log.warn("删除小组失败：权限不足 - userId: {}, role: {}", userId, role);
            return Result.error("权限不足，需要赛事管理员或系统管理员角色");
        }

        // 4. 执行删除
        try {
            groupStageService.deleteGroup(id);
            log.info("删除小组成功 - id: {}", id);
            return Result.success("小组已取消，已分配的球队已移除");
        } catch (RuntimeException e) {
            log.warn("删除小组失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
