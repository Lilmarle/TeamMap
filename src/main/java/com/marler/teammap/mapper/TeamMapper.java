package com.marler.teammap.mapper;

import com.marler.teammap.dto.response.TeamInfoVO;
import com.marler.teammap.pojo.Team;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeamMapper {
    int insert(Team team);

    int updateById(Team team);

    int deleteById(Long id);

    List<TeamInfoVO> selectAllFromView();
}
