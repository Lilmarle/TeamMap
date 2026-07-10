package com.marler.teammap.service;

import com.marler.teammap.dto.response.UserInfoDetailResponse;
import com.marler.teammap.pojo.UserProfile;

public interface UserProfileService {

    int insertProfile(UserProfile userProfile);

    int updateProfile(UserProfile userProfile);

    UserInfoDetailResponse getUserInfoDetailById(Long userId);
}
