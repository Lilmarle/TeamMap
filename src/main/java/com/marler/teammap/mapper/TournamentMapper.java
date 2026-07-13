package com.marler.teammap.mapper;

import com.marler.teammap.pojo.Tournament;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TournamentMapper {
    void insert(Tournament tournament);

    List<Tournament> selectAll();

    Tournament selectById(@Param("id") Long id);

    List<Tournament> selectByCreatorId(@Param("creatorId") Long creatorId);

    void deleteById(@Param("id") Long id);
}
