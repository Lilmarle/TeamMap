package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

//CREATE TABLE `team_member` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成员ID，主键',
//        `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
//        `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID，关联user表',
//        `role` TINYINT(1) UNSIGNED NOT NULL COMMENT '角色：1-队员，2-队长，3-教练，4-领队',
//        `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_team_user` (`team_id`, `user_id`),
//KEY `idx_team_id` (`team_id`),
//KEY `idx_user_id` (`user_id`),
//KEY `idx_role` (`role`),
//KEY `idx_join_time` (`join_time`),
//CONSTRAINT `fk_tm_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_tm_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队伍成员表';
//-- 在 team_member 表添加定妆照字段
//ALTER TABLE `team_member`
//ADD COLUMN `portrait_photo` VARCHAR(500) DEFAULT NULL COMMENT '定妆照URL地址' AFTER `role`;
public class TeamMember {
    private Long id;                  // 成员ID，主键
    private Long teamId;              // 队伍ID，关联team表
    private Long userId;              // 用户ID，关联user表
    private String portraitPhoto;     // 定妆照URL地址
    private Integer role;             // 角色：1-队员，2-队长，3-教练，4-领队
    private Integer status;           // 状态：1-申请中，2-已加入，3-已退出
    private LocalDateTime joinTime;   // 加入时间
    private LocalDateTime updateTime; // 修改时间
}
