package com.marler.teammap.service;

import com.marler.teammap.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    User findById(Long id);

    int register(User user);

    int updateUser(User user);

    int deleteUser(Long id);
}
