package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTeamRequest;
import com.marler.teammap.dto.request.UpdateTeamRequest;
import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
     * 添加球队
     */
    @PostMapping("/add")
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
    @PostMapping("/update")
    public Result<?> update(@RequestBody UpdateTeamRequest request) {
        log.info("修改球队信息请求 - teamId: {}", request.getTeamId());

        // 参数校验
        if (request.getTeamId() == null) {
            log.warn("修改球队信息失败：球队ID为空");
            return Result.error("球队ID不能为空");
        }

        teamService.update(request);
        log.info("修改球队信息成功 - teamId: {}", request.getTeamId());
        return Result.success("修改球队信息成功");
    }

    /**
     * 查询所有球队信息
     */
    @GetMapping("/list")
    public Result<List<TeamInfoVO>> list() {
        log.info("查询所有球队信息");
        List<TeamInfoVO> list = teamService.getAllTeamInfo();
        return Result.success(list);
    }
}
