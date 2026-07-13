package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.pojo.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamMapper {
    int insert(Team team);

    int updateById(Team team);

    int deleteById(Long id);

    Team selectById(@Param("id") Long id);

    List<TeamInfoVO> selectAllFromView();

    List<Team> selectByType(@Param("type") Integer type);
}
