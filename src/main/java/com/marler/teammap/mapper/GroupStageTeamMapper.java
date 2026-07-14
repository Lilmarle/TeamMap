package com.marler.teammap.mapper;

import com.marler.teammap.pojo.GroupStageTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupStageTeamMapper {

    void insert(GroupStageTeam groupStageTeam);

    void insertBatch(List<GroupStageTeam> groupStageTeams);

    List<GroupStageTeam> selectByGroupStageId(@Param("groupStageId") Integer groupStageId);

    void deleteByGroupStageId(@Param("groupStageId") Integer groupStageId);
}
