package com.marler.teammap.mapper;

import com.marler.teammap.pojo.TeamMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMemberMapper {
    void insert(TeamMember teamMember);
}
