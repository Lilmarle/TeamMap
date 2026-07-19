package com.marler.teammap.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;           // 邮箱
    private Integer role;           // 角色：1-普通用户，2-球员，3-赛事管理员，4-系统管理员
    private String inviteCode;      // 邀请码（仅注册时使用，不持久化到数据库）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
