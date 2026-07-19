package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `player` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '球员ID，主键',
//        `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID，关联user表',
//        `tm_id` INT UNSIGNED NOT NULL COMMENT '队伍成员关联ID，关联team_member表',
//        `jersey_name` VARCHAR(50) DEFAULT NULL COMMENT '球衣名称（印名）',
//        `jersey_number` TINYINT(2) UNSIGNED DEFAULT NULL COMMENT '球衣号码（1-99）',
//        `position` VARCHAR(50) DEFAULT NULL COMMENT '位置（如：前锋、后卫、守门员）',
//        `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-可出战，2-禁赛中',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_tm_id` (`tm_id`),
//UNIQUE KEY `uk_user_team` (`user_id`, `tm_id`),
//UNIQUE KEY `uk_team_number` (`tm_id`, `jersey_number`),
//KEY `idx_user_id` (`user_id`),
//KEY `idx_status` (`status`),
//KEY `idx_position` (`position`),
//CONSTRAINT `fk_player_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_player_team_member` FOREIGN KEY (`tm_id`) REFERENCES `team_member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球员表';

public class Player {
    private Integer id;            // 球员ID，主键
    private Integer userId;        // 用户ID，关联user表
    private Integer tmId;          // 队伍成员关联ID，关联team_member表
    private String jerseyName;     // 球衣名称（印名）
    private Integer jerseyNumber;  // 球衣号码（1-99）
    private String position;       // 位置
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
