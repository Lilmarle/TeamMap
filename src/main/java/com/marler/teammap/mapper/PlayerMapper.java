package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.pojo.Player;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerMapper {
    void insert(Player player);

    void update(Player player);

    void deleteById(@Param("id") Integer id);

    Player selectById(@Param("id") Integer id);

    PlayerInfoVO selectPlayerInfoById(@Param("playerId") Integer playerId);

    PlayerInfoVO selectPlayerInfoByUserId(@Param("userId") Integer userId);

    List<PlayerInfoVO> selectPlayerInfoByTeamId(@Param("teamId") Integer teamId);

    List<PlayerInfoVO> selectAllPlayerInfo();
}
