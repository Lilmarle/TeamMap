package com.marler.teammap.mapper;

import com.marler.teammap.pojo.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamMemberMapper {
    void insert(TeamMember teamMember);

    TeamMember selectById(@Param("id") Long id);

    List<TeamMember> selectActiveByUserId(@Param("userId") Long userId);

    List<TeamMember> selectByTeamId(@Param("teamId") Long teamId);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void updatePortraitPhoto(@Param("id") Long id, @Param("portraitPhoto") String portraitPhoto);

    void update(TeamMember teamMember);
}
