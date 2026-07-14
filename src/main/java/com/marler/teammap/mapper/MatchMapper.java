package com.marler.teammap.mapper;

import com.marler.teammap.pojo.Match;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchMapper {

    void insert(Match match);

    void insertBatch(List<Match> matches);

    void updateById(Match match);

    Match selectById(@Param("id") Long id);

    List<Match> selectByTournamentId(@Param("tournamentId") Integer tournamentId);

    List<Match> selectByTeamId(@Param("teamId") Integer teamId);

    List<Match> selectByGroupStageId(@Param("groupStageId") Integer groupStageId);

    void deleteByGroupStageId(@Param("groupStageId") Integer groupStageId);
}
