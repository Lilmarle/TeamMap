package com.marler.teammap.service;

import com.marler.teammap.pojo.Tournament;

import java.util.List;

public interface TournamentService {
    void add(Tournament tournament);

    List<Tournament> getAll();

    List<Tournament> getByCreatorId(Long creatorId);

    Tournament getById(Long id);

    void deleteById(Long id);
}
