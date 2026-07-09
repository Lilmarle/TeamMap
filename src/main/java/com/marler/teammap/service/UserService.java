package com.marler.teammap.service;

import com.marler.teammap.dto.response.LoginResponse;
import com.marler.teammap.pojo.User;

/**
 * 用户服务接口
 */
public interface UserService {

    User findById(Long id);

    User findByUsername(String username);

    int register(User user);

    LoginResponse login(String username, String password);

    void changePassword(Long userId, String oldPassword, String newPassword);

    int updateUser(User user);

    int deleteUser(Long id);
}
