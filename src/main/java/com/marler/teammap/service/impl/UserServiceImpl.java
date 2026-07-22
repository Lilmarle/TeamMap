package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.LoginResponse;
import com.marler.teammap.pojo.User;
import com.marler.teammap.pojo.UserProfile;
import com.marler.teammap.mapper.UserMapper;
import com.marler.teammap.mapper.UserProfileMapper;
import com.marler.teammap.service.UserService;
import com.marler.teammap.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    @Transactional
    public int register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 处理邀请码：设置角色
        if (user.getInviteCode() != null && !user.getInviteCode().isEmpty()) {
            String roleName = resolveInviteCode(user.getInviteCode(), user);
            if (roleName == null) {
                throw new RuntimeException("邀请码无效");
            }
        } else {
            // 默认角色：普通用户(1)
            user.setRole(1);
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入用户（回填自增ID到user对象）
        int result = userMapper.insert(user);

        // 同步创建用户档案
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(user.getId());
        // 默认使用用户名作为昵称
        userProfile.setNickname(user.getUsername());
        userProfile.setGender(0);  // 默认未知
        userProfile.setCreateTime(LocalDateTime.now());
        userProfile.setUpdateTime(LocalDateTime.now());
        userProfileMapper.insert(userProfile);

        return result;
    }

    @Override
    public LoginResponse login(String username, String password) {
        // 根据用户名查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码（兼容 BCrypt 加密密码和明文密码）
        String storedPassword = user.getPassword();
        boolean passwordMatch;

        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            // BCrypt 加密密码
            passwordMatch = passwordEncoder.matches(password, storedPassword);
        } else {
            // 明文密码直接比较
            passwordMatch = password.equals(storedPassword);
        }

        if (!passwordMatch) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT Token（包含角色信息）
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 清除密码后返回
        user.setPassword(null);
        return new LoginResponse(token, user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 加密新密码并更新
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 解析邀请码，设置用户角色
     * @param inviteCode 邀请码
     * @param user 用户对象（会在方法内设置 role）
     * @return 角色名称（仅用于日志），如果邀请码无效返回 null
     */
    private String resolveInviteCode(String inviteCode, User user) {
        // 赛事管理员邀请码 -> role=3
        if ("dccshishabi".equals(inviteCode)) {
            user.setRole(3);
            return "赛事管理员";
        }
        // 系统管理员邀请码 -> role=4
        if ("hryshishabi".equals(inviteCode)) {
            user.setRole(4);
            return "系统管理员";
        }
        return null;
    }

    @Override
    public int updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user);
    }

    @Override
    public int deleteUser(Long id) {
        return userMapper.deleteById(id);
    }
}
