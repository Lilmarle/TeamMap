package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户详细信息响应 DTO
 * 对应数据库视图 v_user_info_detail
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDetailResponse {
    private Long userId;           // 用户ID
    private String username;       // 用户名
    private Integer role;          // 角色编码：1-普通用户，2-球员，3-赛事管理员，4-系统管理员
    private String roleName;       // 角色名称
    private String nickname;       // 昵称
    private String avatar;         // 头像URL
    private String genderName;     // 性别名称
    private Integer age;           // 年龄
}
