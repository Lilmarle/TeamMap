package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.UserInfoDetailResponse;
import com.marler.teammap.mapper.UserProfileMapper;
import com.marler.teammap.pojo.UserProfile;
import com.marler.teammap.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public int insertProfile(UserProfile userProfile) {
        return userProfileMapper.insert(userProfile);
    }

    @Override
    public int updateProfile(UserProfile userProfile) {
        return userProfileMapper.updateByUserId(userProfile);
    }

    @Override
    public UserInfoDetailResponse getUserInfoDetailById(Long userId) {
        return userProfileMapper.selectUserInfoDetailById(userId);
    }
}
