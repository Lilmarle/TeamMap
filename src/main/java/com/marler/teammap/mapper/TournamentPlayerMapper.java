package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.TournamentPlayerInfoVO;
import com.marler.teammap.pojo.TournamentPlayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TournamentPlayerMapper {
    void insert(TournamentPlayer tournamentPlayer);

    void insertBatch(@Param("list") List<TournamentPlayer> list);

    TournamentPlayer selectById(@Param("id") Long id);

    TournamentPlayer selectByTournamentIdAndUserId(@Param("tournamentId") Long tournamentId,
                                                    @Param("userId") Long userId);

    List<TournamentPlayer> selectByTournamentId(@Param("tournamentId") Long tournamentId);

    List<TournamentPlayer> selectByTournamentIdAndUserIds(@Param("tournamentId") Long tournamentId,
                                                           @Param("userIds") List<Long> userIds);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void updateStatusByTournamentIdAndTeamId(@Param("tournamentId") Long tournamentId,
                                              @Param("teamId") Long teamId,
                                              @Param("status") Integer status);

    // ====== 视图查询 ======

    List<TournamentPlayerInfoVO> selectInfoByTournamentId(@Param("tournamentId") Long tournamentId);

    List<TournamentPlayerInfoVO> selectInfoByTournamentIdAndTeamId(@Param("tournamentId") Long tournamentId,
                                                                    @Param("teamId") Long teamId);
}
