package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

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
}
