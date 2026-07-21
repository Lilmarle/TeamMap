package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//CREATE TABLE `team` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '队伍ID，主键',
//        `name` VARCHAR(100) NOT NULL COMMENT '队伍名称',
//        `logo` VARCHAR(500) DEFAULT NULL COMMENT '队伍Logo URL地址',
//        `description` VARCHAR(500) DEFAULT NULL COMMENT '队伍描述',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_name` (`name`),
//KEY `idx_create_time` (`create_time`)
//        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队伍表';
//        -- 添加字段 + 索引
//ALTER TABLE `team`
//ADD COLUMN `type` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '运动类型：1-足球，2-篮球，3-排球' AFTER `description`,
//ADD KEY `idx_type` (`type`);
//ALTER TABLE `team`
//ADD COLUMN `gender` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '性别：1-男，2-女，3-混合' AFTER `type`;
//ALTER TABLE `team`
//ADD COLUMN `short_name` VARCHAR(50) DEFAULT NULL COMMENT '队伍简称' AFTER `name`;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private Integer type;            // 运动类型：1-足球，2-篮球，3-排球
    private Integer gender;          // 性别：1-男，2-女
    private String shortName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
