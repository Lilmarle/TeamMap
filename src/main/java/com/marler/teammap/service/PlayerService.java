package com.marler.teammap.service;

import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.pojo.Player;

import java.util.List;

public interface PlayerService {
    void add(Player player);

    Player getById(Integer id);

    PlayerInfoVO getPlayerInfoById(Integer id);

    List<PlayerInfoVO> getAllPlayers();

    List<PlayerInfoVO> getPlayersByTeamId(Integer teamId);
}
