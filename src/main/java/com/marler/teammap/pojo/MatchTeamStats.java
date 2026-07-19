package com.marler.teammap.pojo;

import java.time.LocalDateTime;

//CREATE TABLE `match_team_stats` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
//        `match_id` INT UNSIGNED NOT NULL COMMENT '比赛ID，关联match表',
//        `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
//        `goals` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '进球数',
//        `goals_against` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '丢球数',
//        `red_cards` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红牌数',
//        `yellow_cards` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '黄牌数',
//        `fouls` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '犯规数',
//        `substitutions` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '换人次数',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_match_team` (`match_id`, `team_id`),
//KEY `idx_match_id` (`match_id`),
//KEY `idx_team_id` (`team_id`),
//CONSTRAINT `fk_mts_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_mts_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛球队数据统计表';
public class MatchTeamStats {
    private Integer id;
    private Integer matchId;
    private Integer teamId;
    private Integer goals;
    private Integer goalsAgainst;
    private Integer redCards;
    private Integer yellowCards;
    private Integer fouls;
    private Integer substitutions;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
