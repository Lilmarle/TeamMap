package com.marler.teammap.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改成员信息请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private Long userId;       // 用户ID
    private String nickname;   // 昵称
    private String avatar;     // 头像URL
    private Integer gender;    // 性别：0-未知，1-男，2-女
    private Integer age;       // 年龄
}
