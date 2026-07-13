package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//CREATE TABLE `tournament_team` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
//        `tournament_id` INT UNSIGNED NOT NULL COMMENT '赛事ID，关联tournament表',
//        `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
//        `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-申请中，2-已通过，3-未通过',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（申请时间）',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（审核时间）',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_tournament_team` (`tournament_id`, `team_id`),
//KEY `idx_tournament_id` (`tournament_id`),
//KEY `idx_team_id` (`team_id`),
//KEY `idx_status` (`status`),
//CONSTRAINT `fk_tt_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_tt_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球队参赛表';
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentTeam {
    private Long id;
    private Long tournamentId;
    private Long teamId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
