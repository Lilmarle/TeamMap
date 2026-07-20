package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.pojo.MatchEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchEventMapper {

    void insert(MatchEvent matchEvent);

    List<MatchEvent> selectByMatchId(@Param("matchId") Integer matchId);

    List<MatchEventStatsVO> selectEventStatsByMatchId(@Param("matchId") Integer matchId);

    List<MatchEventVO> selectEventVOByPlayerId(@Param("playerId") Integer playerId);

    List<MatchEventVO> selectEventVOByMatchId(@Param("matchId") Integer matchId);
}
