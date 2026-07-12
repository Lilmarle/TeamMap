package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.service.TeamMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/team-member")
public class TeamMemberController {

    @Autowired
    private TeamMemberService teamMemberService;

    /**
     * 添加队伍成员（同时创建球员记录，角色固定为 1-队员）
     */
    @PostMapping
    public Result<?> add(@RequestBody AddTeamMemberRequest request) {
        log.info("添加队伍成员请求 - teamId: {}, userId: {}",
                request.getTeamId(), request.getUserId());

        // 参数校验
        if (request.getTeamId() == null) {
            log.warn("添加队伍成员失败：队伍ID为空");
            return Result.error("队伍ID不能为空");
        }
        if (request.getUserId() == null) {
            log.warn("添加队伍成员失败：用户ID为空");
            return Result.error("用户ID不能为空");
        }

        teamMemberService.add(request);
        log.info("添加队伍成员成功 - teamId: {}, userId: {}", request.getTeamId(), request.getUserId());
        return Result.success("添加队伍成员成功");
    }
}
