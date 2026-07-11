package com.marler.teammap.mapper;

import com.marler.teammap.pojo.College;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollegeMapper {
    List<College> selectAll();
}
