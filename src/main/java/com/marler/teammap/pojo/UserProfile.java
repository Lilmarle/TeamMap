package com.marler.teammap.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `user_profile` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '档案ID，主键',
//        `user_id` INT UNSIGNED NOT NULL COMMENT '关联user表的用户ID',
//        `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
//        `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL地址',
//        `gender` TINYINT(1) UNSIGNED DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
//        `age` TINYINT(3) UNSIGNED DEFAULT NULL COMMENT '年龄（0-150）',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_user_id` (`user_id`),
//KEY `idx_nickname` (`nickname`)
//        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息扩展表';
public class UserProfile {
    private Long id;                   // 档案ID，主键
    private Long userId;               // 关联user表
    private String nickname;           // 昵称
    private String avatar;             // 头像URL地址
    private Integer gender;            // 性别：0-未知，1-男，2-女
    private Integer age;               // 年龄（0-150）
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 修改时间
}
