package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddMatchEventRequest;
import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.pojo.MatchEvent;

import java.util.List;

public interface MatchEventService {

    /**
     * 添加比赛事件
     */
    MatchEvent add(AddMatchEventRequest request);

    /**
     * 根据比赛ID查询所有事件
     */
    List<MatchEvent> getByMatchId(Integer matchId);

    /**
     * 根据比赛ID查询事件列表（含球员名称、球衣号码等）
     */
    List<MatchEventVO> getEventVOByMatchId(Integer matchId);

    /**
     * 根据比赛ID查询事件统计
     */
    List<MatchEventStatsVO> getEventStatsByMatchId(Integer matchId);
}
