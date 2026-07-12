package com.marler.teammap.mapper;

import com.marler.teammap.pojo.TeamRollegeRel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamRollegeRelMapper {
    int insert(TeamRollegeRel teamRollegeRel);

    int updateByTeamId(TeamRollegeRel teamRollegeRel);

    int deleteByTeamId(Long teamId);
}
