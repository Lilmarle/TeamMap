package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `tournament` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '赛事ID，主键',
//        `name` VARCHAR(100) NOT NULL COMMENT '赛事名称',
//        `creator_id` INT UNSIGNED NOT NULL COMMENT '创办者ID，关联user表',
//        `type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球',
//        `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-筹办中，2-进行中，3-已结束',
//        `start_time` DATETIME NOT NULL COMMENT '开始时间',
//        `end_time` DATETIME NOT NULL COMMENT '结束时间',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_name` (`name`),
//KEY `idx_creator_id` (`creator_id`),
//KEY `idx_type` (`type`),
//KEY `idx_status` (`status`),
//KEY `idx_start_time` (`start_time`),
//KEY `idx_end_time` (`end_time`),
//KEY `idx_time_range` (`start_time`, `end_time`),
//CONSTRAINT `fk_tournament_creator` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='赛事表';
public class Tournament {
    private Long id;
    private String name;
    private Long creatorId;
    private Integer type;          // 运动类型：1-足球，2-篮球，3-排球
    private Integer status;        // 状态：1-筹办中，2-进行中，3-已结束
    private String startTime;
    private String endTime;
    private String createTime;
    private String updateTime;
}
