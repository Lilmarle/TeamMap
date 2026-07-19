package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `group_stage_team` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
//        `group_stage_id` INT UNSIGNED NOT NULL COMMENT '小组赛ID，关联group_stage表',
//        `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
//        `win` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '胜场数',
//        `draw` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '平场数',
//        `loss` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '负场数',
//        `points` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '积分',
//        `goals_for` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '进球数（得分）',
//        `goals_against` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '失球数（失分）',
//        `goal_difference` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '净胜球（进球-失球）',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_group_team` (`group_stage_id`, `team_id`),
//KEY `idx_group_stage_id` (`group_stage_id`),
//KEY `idx_team_id` (`team_id`),
//KEY `idx_points` (`points`),
//KEY `idx_goal_difference` (`goal_difference`),
//CONSTRAINT `fk_gst_group_stage` FOREIGN KEY (`group_stage_id`) REFERENCES `group_stage` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_gst_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组赛球队成绩表';
public class GroupStageTeam {
    private Integer id;               // 关联ID，主键
    private Integer groupStageId;     // 小组赛ID，关联group_stage表
    private Integer teamId;           // 队伍ID，关联team表
    private Integer win;              // 胜场数
    private Integer draw;             // 平场数
    private Integer loss;             // 负场数
    private Integer points;           // 积分
    private Integer goalsFor;         // 进球数（得分）
    private Integer goalsAgainst;     // 失球数（失分）
    private Integer goalDifference;   // 净胜球（进球-失球）
    private LocalDateTime createTime;        // 创建时间
    private LocalDateTime updateTime;        // 修改时间
}
