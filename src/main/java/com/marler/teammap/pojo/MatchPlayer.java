package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `match_player` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
//        `match_id` INT UNSIGNED NOT NULL COMMENT '比赛ID，关联match表',
//        `sport_type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球',
//        `player_id` INT UNSIGNED NOT NULL COMMENT '球员ID，关联player表',
//        `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0-未出场，1-首发，2-替补',
//        `play_time` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '出场时间（分钟），0表示打满全场',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_match_player` (`match_id`, `player_id`),
//KEY `idx_match_id` (`match_id`),
//KEY `idx_player_id` (`player_id`),
//KEY `idx_status` (`status`),
//CONSTRAINT `fk_mp_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_mp_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛球员阵容表';
public class MatchPlayer {
    private Long id;
    private Long matchId;
    private Integer sportType;
    private Long playerId;
    private Integer status;
    private Integer playTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
