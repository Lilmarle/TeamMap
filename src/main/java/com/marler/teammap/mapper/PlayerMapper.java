package com.marler.teammap.mapper;

import com.marler.teammap.pojo.Player;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlayerMapper {
    void insert(Player player);
}
