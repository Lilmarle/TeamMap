package com.marler.teammap.mapper;

import com.marler.teammap.pojo.GroupStage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupStageMapper {

    void insert(GroupStage groupStage);

    void insertBatch(List<GroupStage> groupStages);

    GroupStage selectById(@Param("id") Integer id);

    List<GroupStage> selectByTournamentId(@Param("tournamentId") Integer tournamentId);
}
