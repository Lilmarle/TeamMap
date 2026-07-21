package com.marler.teammap.mapper;

import com.marler.teammap.pojo.TeamRollegeRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeamRollegeRelMapper {
    int insert(TeamRollegeRel teamRollegeRel);

    int updateByTeamId(TeamRollegeRel teamRollegeRel);

    int deleteByTeamId(Long teamId);

    Integer selectRankByTeamId(@Param("teamId") Long teamId);
}
