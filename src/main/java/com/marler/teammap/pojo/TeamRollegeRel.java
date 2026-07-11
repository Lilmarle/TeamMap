package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

//CREATE TABLE `team_college_rel` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
//        `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
//        `rank` TINYINT(1) UNSIGNED NOT NULL COMMENT '队伍级别：1-院队，2-校队 3-班队',
//        `college_id` INT UNSIGNED DEFAULT NULL COMMENT '学院ID，关联college表（校队可为空）',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//KEY `idx_team_id` (`team_id`),
//KEY `idx_college_id` (`college_id`),
//KEY `idx_rank` (`rank`),
//KEY `idx_team_college` (`team_id`, `college_id`),
//CONSTRAINT `fk_tcr_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_tcr_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球队学院关联表';
public class TeamRollegeRel {
    private Long id;                  // 关联ID，主键
    private Long teamId;              // 队伍ID，关联team表
    private Integer rank;             // 队伍级别：1-院队，2-校队 3-班队
    private Long collegeId;           // 学院ID，关联college表（校队可为空）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
}
