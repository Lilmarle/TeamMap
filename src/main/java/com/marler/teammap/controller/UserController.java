package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.ChangePasswordRequest;
import com.marler.teammap.dto.request.LoginRequest;
import com.marler.teammap.dto.response.LoginResponse;
import com.marler.teammap.pojo.User;
import com.marler.teammap.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        log.info("用户注册请求 - username: {}", user.getUsername());

        // 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            log.warn("注册失败：用户名为空");
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            log.warn("注册失败：密码长度不足6位 - username: {}", user.getUsername());
            return Result.error("密码不能少于6位");
        }

        userService.register(user);
        log.info("用户注册成功 - username: {}", user.getUsername());
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求 - username: {}", loginRequest.getUsername());

        // 参数校验
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            log.warn("登录失败：用户名为空");
            return Result.error("用户名不能为空");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            log.warn("登录失败：密码为空 - username: {}", loginRequest.getUsername());
            return Result.error("密码不能为空");
        }

        LoginResponse loginResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        log.info("用户登录成功 - username: {}", loginRequest.getUsername());
        return Result.success(loginResponse);
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<?> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("修改密码请求 - userId: {}", request.getUserId());

        // 参数校验
        if (request.getUserId() == null) {
            log.warn("修改密码失败：用户ID为空");
            return Result.error("用户ID不能为空");
        }
        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
            log.warn("修改密码失败：原密码为空 - userId: {}", request.getUserId());
            return Result.error("原密码不能为空");
        }
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            log.warn("修改密码失败：新密码长度不足6位 - userId: {}", request.getUserId());
            return Result.error("新密码不能少于6位");
        }

        userService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        log.info("修改密码成功 - userId: {}", request.getUserId());
        return Result.success("密码修改成功");
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        log.info("查询用户请求 - userId: {}", id);

        User user = userService.findById(id);
        if (user == null) {
            log.warn("查询用户失败：用户不存在 - userId: {}", id);
            return Result.error("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        log.info("查询用户成功 - userId: {}, username: {}", id, user.getUsername());
        return Result.success(user);
    }

    /**
     * 注册为赛事管理员（role=3）
     */
    @PostMapping("/register/admin-event")
    public Result<?> registerAsEventAdmin(@RequestBody User user) {
        log.info("赛事管理员注册请求 - username: {}", user.getUsername());

        // 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            log.warn("注册失败：用户名为空");
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            log.warn("注册失败：密码长度不足6位 - username: {}", user.getUsername());
            return Result.error("密码不能少于6位");
        }

        // 设置为赛事管理员角色
        user.setRole(3);
        userService.register(user);
        log.info("赛事管理员注册成功 - username: {}, role: 3", user.getUsername());
        return Result.success("赛事管理员注册成功");
    }

    /**
     * 注册为系统管理员（role=4）
     */
    @PostMapping("/register/admin-system")
    public Result<?> registerAsSystemAdmin(@RequestBody User user) {
        log.info("系统管理员注册请求 - username: {}", user.getUsername());

        // 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            log.warn("注册失败：用户名为空");
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            log.warn("注册失败：密码长度不足6位 - username: {}", user.getUsername());
            return Result.error("密码不能少于6位");
        }

        // 设置为系统管理员角色
        user.setRole(4);
        userService.register(user);
        log.info("系统管理员注册成功 - username: {}, role: 4", user.getUsername());
        return Result.success("系统管理员注册成功");
    }
}
