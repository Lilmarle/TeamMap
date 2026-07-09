package com.marler.teammap.mapper;

import com.marler.teammap.pojo.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper {

    int insert(UserProfile userProfile);
}
