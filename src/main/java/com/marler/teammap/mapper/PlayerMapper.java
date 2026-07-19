package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.pojo.Player;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerMapper {
    void insert(Player player);

    Player selectById(@Param("id") Integer id);

    PlayerInfoVO selectPlayerInfoById(@Param("playerId") Integer playerId);

    List<PlayerInfoVO> selectPlayerInfoByTeamId(@Param("teamId") Integer teamId);
}
