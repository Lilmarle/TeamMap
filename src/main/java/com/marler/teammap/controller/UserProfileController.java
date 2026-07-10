package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.UpdateProfileRequest;
import com.marler.teammap.dto.response.UserInfoDetailResponse;
import com.marler.teammap.pojo.UserProfile;
import com.marler.teammap.service.UserProfileService;
import com.marler.teammap.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url}")
    private String uploadUrl;

    /**
     * 修改成员信息
     */
    @PutMapping("/update")
    public Result<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        log.info("修改成员信息请求 - userId: {}", request.getUserId());

        // 参数校验
        if (request.getUserId() == null) {
            log.warn("修改成员信息失败：用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 将请求 DTO 转换为实体对象
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(request.getUserId());
        userProfile.setNickname(request.getNickname());
        userProfile.setAvatar(request.getAvatar());
        userProfile.setGender(request.getGender());
        userProfile.setAge(request.getAge());

        int rows = userProfileService.updateProfile(userProfile);
        if (rows <= 0) {
            log.warn("修改成员信息失败：未找到对应的用户档案 - userId: {}", request.getUserId());
            return Result.error("修改失败，用户档案不存在");
        }

        log.info("修改成员信息成功 - userId: {}", request.getUserId());
        return Result.success("修改成功");
    }

    /**
     * 更新个人资料（支持头像上传）
     * 通过 JWT Token 获取用户 ID，支持上传头像文件
     */
    @PostMapping("/profile")
    public Result<?> updateProfileWithAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) MultipartFile avatar) {
        try {
            // 解析 JWT 令牌获取用户 ID
            Claims claims = JwtUtil.parseToken(token.replace("Bearer ", ""));
            Long userId = Long.valueOf(claims.getSubject());

            log.info("更新个人资料请求 - userId: {}", userId);

            // 处理头像上传
            String avatarUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                // 验证文件类型
                String fileName = avatar.getOriginalFilename();
                String extName = "";
                if (fileName != null && fileName.contains(".")) {
                    extName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                }
                if (!extName.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                    return Result.error("只支持上传图片文件（jpg、jpeg、png、gif、bmp、webp）");
                }

                // 确保上传目录存在
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 生成唯一文件名
                String newFileName = UUID.randomUUID().toString() + extName;

                // 保存文件
                avatar.transferTo(new File(uploadPath, newFileName));

                // 生成可访问的头像URL
                avatarUrl = uploadUrl + "/" + newFileName;

                log.info("头像上传成功 - URL: {}", avatarUrl);
            }

            // 构造 UserProfile 对象
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setNickname(nickname);
            profile.setGender(gender);
            profile.setAge(age);
            profile.setAvatar(avatarUrl);

            // 调用 Service 保存或更新用户档案
            int rows = userProfileService.updateProfile(profile);
            if (rows <= 0) {
                // 如果更新行数为0，说明档案不存在，则插入新档案
                userProfileService.insertProfile(profile);
            }

            log.info("更新个人资料成功 - userId: {}", userId);
            return Result.success("用户信息完善成功");
        } catch (Exception e) {
            log.error("更新个人资料失败", e);
            return Result.error("更新个人资料失败：" + e.getMessage());
        }
    }

    /**
     * 根据 userId 查询用户详细信息（联表查询 user + user_profile）
     */
    @GetMapping("/detail/{userId}")
    public Result<UserInfoDetailResponse> getUserInfoDetail(@PathVariable Long userId) {
        log.info("查询用户详细信息请求 - userId: {}", userId);

        if (userId == null) {
            log.warn("查询用户详细信息失败：用户ID为空");
            return Result.error("用户ID不能为空");
        }

        UserInfoDetailResponse userInfoDetail = userProfileService.getUserInfoDetailById(userId);
        if (userInfoDetail == null) {
            log.warn("查询用户详细信息失败：用户不存在 - userId: {}", userId);
            return Result.error("用户不存在");
        }

        log.info("查询用户详细信息成功 - userId: {}, username: {}", userId, userInfoDetail.getUsername());
        return Result.success(userInfoDetail);
    }
}
