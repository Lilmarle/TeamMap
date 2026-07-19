package com.marler.teammap.service;

import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.dto.response.MatchPlayerPerformanceVO;
import com.marler.teammap.dto.response.PlayerTournamentStatsVO;
import com.marler.teammap.pojo.MatchPlayer;

import java.util.List;

public interface MatchPlayerService {

    /**
     * 根据比赛ID查询球员表现
     */
    List<MatchPlayerPerformanceVO> getPerformanceByMatchId(Integer matchId);

    /**
     * 查询球员赛事统计
     */
    List<PlayerTournamentStatsVO> getTournamentStats(Integer tournamentId, Integer teamId);

    /**
     * 根据球员ID查询比赛事件
     */
    List<MatchEventVO> getEventsByPlayerId(Integer playerId);

    /**
     * 根据比赛ID查询事件统计（按队伍分组）
     */
    List<MatchEventStatsVO> getEventStatsByMatchId(Integer matchId);

    /**
     * 根据比赛ID获取球员阵容列表
     */
    List<MatchPlayer> getByMatchId(Integer matchId);

    /**
     * 根据ID获取球员阵容记录
     */
    MatchPlayer getById(Long id);

    /**
     * 添加球员阵容
     */
    MatchPlayer add(MatchPlayer matchPlayer);

    /**
     * 更新球员阵容
     */
    MatchPlayer update(MatchPlayer matchPlayer);

    /**
     * 删除球员阵容记录
     */
    void delete(Long id);
}
