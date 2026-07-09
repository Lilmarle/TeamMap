package com.marler.teammap.dto.response;

import com.marler.teammap.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应 DTO
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
}
