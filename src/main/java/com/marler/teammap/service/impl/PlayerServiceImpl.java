package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.PlayerInfoVO;
import com.marler.teammap.mapper.PlayerMapper;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerMapper playerMapper;

    @Override
    public void add(Player player) {
        playerMapper.insert(player);
    }

    @Override
    public Player getById(Integer id) {
        return playerMapper.selectById(id);
    }

    @Override
    public PlayerInfoVO getPlayerInfoById(Integer id) {
        log.debug("查询球员信息 - playerId: {}", id);
        return playerMapper.selectPlayerInfoById(id);
    }

    @Override
    public List<PlayerInfoVO> getAllPlayers() {
        log.debug("查询所有球员信息");
        return playerMapper.selectAllPlayerInfo();
    }

    @Override
    public List<PlayerInfoVO> getPlayersByTeamId(Integer teamId) {
        log.debug("按球队查询球员信息 - teamId: {}", teamId);
        return playerMapper.selectPlayerInfoByTeamId(teamId);
    }
}
