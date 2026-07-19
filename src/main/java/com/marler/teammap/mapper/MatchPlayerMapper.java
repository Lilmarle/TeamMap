package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.MatchPlayerPerformanceVO;
import com.marler.teammap.dto.response.PlayerTournamentStatsVO;
import com.marler.teammap.pojo.MatchPlayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchPlayerMapper {

    List<MatchPlayerPerformanceVO> selectPerformanceByMatchId(@Param("matchId") Integer matchId);

    List<PlayerTournamentStatsVO> selectTournamentStats(
            @Param("tournamentId") Integer tournamentId,
            @Param("teamId") Integer teamId);

    List<MatchPlayer> selectByMatchId(@Param("matchId") Integer matchId);

    MatchPlayer selectById(@Param("id") Long id);

    int insert(MatchPlayer matchPlayer);

    int updateById(MatchPlayer matchPlayer);

    int deleteById(@Param("id") Long id);
}
