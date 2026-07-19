package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.dto.response.MatchPlayerPerformanceVO;
import com.marler.teammap.dto.response.PlayerTournamentStatsVO;
import com.marler.teammap.mapper.MatchEventMapper;
import com.marler.teammap.mapper.MatchPlayerMapper;
import com.marler.teammap.pojo.MatchPlayer;
import com.marler.teammap.service.MatchPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchPlayerServiceImpl implements MatchPlayerService {

    @Autowired
    private MatchPlayerMapper matchPlayerMapper;

    @Autowired
    private MatchEventMapper matchEventMapper;

    @Override
    public List<MatchPlayerPerformanceVO> getPerformanceByMatchId(Integer matchId) {
        return matchPlayerMapper.selectPerformanceByMatchId(matchId);
    }

    @Override
    public List<PlayerTournamentStatsVO> getTournamentStats(Integer tournamentId, Integer teamId) {
        return matchPlayerMapper.selectTournamentStats(tournamentId, teamId);
    }

    @Override
    public List<MatchPlayer> getByMatchId(Integer matchId) {
        return matchPlayerMapper.selectByMatchId(matchId);
    }

    @Override
    public MatchPlayer getById(Long id) {
        return matchPlayerMapper.selectById(id);
    }

    @Override
    public MatchPlayer add(MatchPlayer matchPlayer) {
        matchPlayerMapper.insert(matchPlayer);
        return matchPlayer;
    }

    @Override
    public MatchPlayer update(MatchPlayer matchPlayer) {
        matchPlayerMapper.updateById(matchPlayer);
        return matchPlayer;
    }

    @Override
    public void delete(Long id) {
        matchPlayerMapper.deleteById(id);
    }

    @Override
    public List<MatchEventVO> getEventsByPlayerId(Integer playerId) {
        return matchEventMapper.selectEventVOByPlayerId(playerId);
    }

    @Override
    public List<MatchEventStatsVO> getEventStatsByMatchId(Integer matchId) {
        return matchEventMapper.selectEventStatsByMatchId(matchId);
    }
}
