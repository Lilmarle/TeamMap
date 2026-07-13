package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTeamRequest;
import com.marler.teammap.dto.request.UpdateTeamRequest;
import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/team")
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
     */
    @PostMapping
    public Result<?> add(@RequestBody AddTeamRequest request) {
        Team team = request.getTeam();
        log.info("添加球队请求 - name: {}, rank: {}", team.getName(), request.getRank());

        // 参数校验
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

        teamService.add(request);
        log.info("添加球队成功 - name: {}, id: {}", team.getName(), team.getId());
        return Result.success("添加球队成功");
    }

    /**
     * 修改球队信息
     */
    @PutMapping("/{teamId}")
    public Result<?> update(@PathVariable Long teamId, @RequestBody UpdateTeamRequest request) {
        log.info("修改球队信息请求 - teamId: {}", teamId);
        request.setTeamId(teamId);

        teamService.update(request);
        log.info("修改球队信息成功 - teamId: {}", teamId);
        return Result.success("修改球队信息成功");
    }

    /**
     * 删除球队
     */
    @DeleteMapping("/{teamId}")
    public Result<?> delete(@PathVariable Long teamId) {
        log.info("删除球队请求 - teamId: {}", teamId);
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
