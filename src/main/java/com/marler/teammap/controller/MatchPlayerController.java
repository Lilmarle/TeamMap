package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.dto.response.MatchPlayerPerformanceVO;
import com.marler.teammap.dto.response.PlayerTournamentStatsVO;
import com.marler.teammap.pojo.MatchPlayer;
import com.marler.teammap.service.MatchPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-players")
public class MatchPlayerController {

    @Autowired
    private MatchPlayerService matchPlayerService;

    /**
     * 查询比赛球员表现（按比赛ID）
     * GET /api/match-players/performance/{matchId}
     */
    @GetMapping("/performance/{matchId}")
    public Result<List<MatchPlayerPerformanceVO>> getPerformanceByMatchId(@PathVariable Integer matchId) {
        List<MatchPlayerPerformanceVO> list = matchPlayerService.getPerformanceByMatchId(matchId);
        return Result.success(list);
    }

    /**
     * 查询球员赛事统计
     * GET /api/match-players/tournament-stats?tournamentId=&teamId=
     */
    @GetMapping("/tournament-stats")
    public Result<List<PlayerTournamentStatsVO>> getTournamentStats(
            @RequestParam(required = false) Integer tournamentId,
            @RequestParam(required = false) Integer teamId) {
        List<PlayerTournamentStatsVO> list = matchPlayerService.getTournamentStats(tournamentId, teamId);
        return Result.success(list);
    }

    /**
     * 查询球员比赛事件（按球员ID）
     * GET /api/match-players/{playerId}/events
     */
    @GetMapping("/{playerId}/events")
    public Result<List<MatchEventVO>> getEventsByPlayerId(@PathVariable Integer playerId) {
        List<MatchEventVO> list = matchPlayerService.getEventsByPlayerId(playerId);
        return Result.success(list);
    }

    /**
     * 查询比赛事件统计（按比赛ID，按队伍分组）
     * GET /api/match-players/{matchId}/event-stats
     */
    @GetMapping("/{matchId}/event-stats")
    public Result<List<MatchEventStatsVO>> getEventStats(@PathVariable Integer matchId) {
        List<MatchEventStatsVO> stats = matchPlayerService.getEventStatsByMatchId(matchId);
        return Result.success(stats);
    }

    /**
     * 根据比赛ID获取球员阵容列表
     * GET /api/match-players/match/{matchId}
     */
    @GetMapping("/match/{matchId}")
    public Result<List<MatchPlayer>> getByMatchId(@PathVariable Integer matchId) {
        List<MatchPlayer> list = matchPlayerService.getByMatchId(matchId);
        return Result.success(list);
    }

    /**
     * 根据ID获取球员阵容记录
     * GET /api/match-players/{id}
     */
    @GetMapping("/{id}")
    public Result<MatchPlayer> getById(@PathVariable Long id) {
        MatchPlayer matchPlayer = matchPlayerService.getById(id);
        return Result.success(matchPlayer);
    }

    /**
     * 添加球员阵容
     * POST /api/match-players
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<MatchPlayer> add(@RequestBody MatchPlayer matchPlayer) {
        MatchPlayer result = matchPlayerService.add(matchPlayer);
        return Result.success(result);
    }

    /**
     * 更新球员阵容
     * PUT /api/match-players/{id}
     */
    @PutMapping("/{id}")
    public Result<MatchPlayer> update(@PathVariable Long id, @RequestBody MatchPlayer matchPlayer) {
        matchPlayer.setId(id);
        MatchPlayer result = matchPlayerService.update(matchPlayer);
        return Result.success(result);
    }

    /**
     * 删除球员阵容记录
     * DELETE /api/match-players/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        matchPlayerService.delete(id);
        return Result.success();
    }
}
