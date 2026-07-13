package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.TournamentTeamInfoVO;
import com.marler.teammap.pojo.TournamentTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TournamentTeamMapper {
    void insert(TournamentTeam tournamentTeam);

    TournamentTeam selectById(@Param("id") Long id);

    TournamentTeam selectByTournamentIdAndTeamId(@Param("tournamentId") Long tournamentId,
                                                  @Param("teamId") Long teamId);

    List<TournamentTeamInfoVO> selectTeamsByTournamentId(@Param("tournamentId") Long tournamentId);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void deleteByTournamentIdAndTeamId(@Param("tournamentId") Long tournamentId,
                                        @Param("teamId") Long teamId);
}
