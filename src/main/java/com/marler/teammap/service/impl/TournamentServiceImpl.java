package com.marler.teammap.service.impl;

import com.marler.teammap.mapper.TournamentMapper;
import com.marler.teammap.pojo.Tournament;
import com.marler.teammap.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentMapper tournamentMapper;

    @Override
    public void add(Tournament tournament) {
        tournamentMapper.insert(tournament);
        log.info("赛事创建成功 - id: {}, name: {}", tournament.getId(), tournament.getName());
    }

    @Override
    public List<Tournament> getAll() {
        log.info("查询所有赛事");
        return tournamentMapper.selectAll();
    }

    @Override
    public List<Tournament> getByCreatorId(Long creatorId) {
        log.info("查询用户创建的赛事 - creatorId: {}", creatorId);
        return tournamentMapper.selectByCreatorId(creatorId);
    }

    @Override
    public Tournament getById(Long id) {
        log.info("查询赛事 - id: {}", id);
        return tournamentMapper.selectById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("删除赛事 - id: {}", id);
        tournamentMapper.deleteById(id);
        log.info("赛事删除成功 - id: {}", id);
    }
}
