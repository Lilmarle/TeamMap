package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddMatchEventRequest;
import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.pojo.MatchEvent;
import com.marler.teammap.service.MatchEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-events")
public class MatchEventController {

    @Autowired
    private MatchEventService matchEventService;

    /**
     * 添加比赛事件
     * POST /api/match-events
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<MatchEvent> add(@RequestBody AddMatchEventRequest request) {
        MatchEvent event = matchEventService.add(request);
        return Result.success(event);
    }

    /**
     * 根据比赛ID查询所有事件
     * GET /api/match-events/{matchId}
     */
    @GetMapping("/{matchId}")
    public Result<List<MatchEvent>> getByMatchId(@PathVariable Integer matchId) {
        List<MatchEvent> events = matchEventService.getByMatchId(matchId);
        return Result.success(events);
    }
    /**
     * 查询比赛事件统计（按队伍分组）
     * GET /api/match-events/{matchId}/stats
     */
    @GetMapping("/{matchId}/stats")
    public Result<List<MatchEventStatsVO>> getEventStats(@PathVariable Integer matchId) {
        List<MatchEventStatsVO> stats = matchEventService.getEventStatsByMatchId(matchId);
        return Result.success(stats);
    }
}