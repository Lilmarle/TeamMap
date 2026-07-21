package com.marler.teammap.service;

import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.pojo.Player;

import java.util.List;

public interface PlayerService {
    void add(Player player);

    void update(Player player);

    void deleteById(Integer id);

    Player getById(Integer id);

    PlayerInfoVO getPlayerInfoById(Integer id);

    PlayerInfoVO getPlayerInfoByUserId(Integer userId);

    List<PlayerInfoVO> getAllPlayers();

    List<PlayerInfoVO> getPlayersByTeamId(Integer teamId);
}
