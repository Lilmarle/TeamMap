package com.marler.teammap.service.impl;

import com.marler.teammap.mapper.PlayerMapper;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerMapper playerMapper;

    @Override
    public void add(Player player) {
        playerMapper.insert(player);
    }
}
