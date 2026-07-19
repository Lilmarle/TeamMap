package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `match_event` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '事件ID，主键',
//        `match_id` INT UNSIGNED NOT NULL COMMENT '比赛ID，关联match表',
//        `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
//        `player_id` INT UNSIGNED DEFAULT NULL COMMENT '球员ID（进球者/犯规者/吃牌者/换上球员）',
//        `related_player_id` INT UNSIGNED DEFAULT NULL COMMENT '关联球员ID（助攻球员/被换下球员/被犯规球员）',
//        `type` TINYINT(1) UNSIGNED NOT NULL COMMENT '事件类型：1-进球，2-红牌，3-黄牌，4-犯规，5-换人',
//        `description` VARCHAR(500) DEFAULT NULL COMMENT '事件描述',
//        `event_time` INT UNSIGNED NOT NULL COMMENT '事件发生时间（分钟）',
//        `extra_info` VARCHAR(200) DEFAULT NULL COMMENT '额外信息',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//KEY `idx_match_id` (`match_id`),
//KEY `idx_team_id` (`team_id`),
//KEY `idx_player_id` (`player_id`),
//KEY `idx_related_player_id` (`related_player_id`),
//KEY `idx_type` (`type`),
//KEY `idx_event_time` (`event_time`),
//CONSTRAINT `fk_me_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_me_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_me_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
//CONSTRAINT `fk_me_related_player` FOREIGN KEY (`related_player_id`) REFERENCES `player` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛事件表';
//LTER TABLE `match_event`
//ADD COLUMN `sport_type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球' AFTER `match_id`,
//ADD KEY `idx_sport_type` (`sport_type`);

public class MatchEvent {
    private Long id;                   // 事件ID，主键
    private Integer matchId;           // 比赛ID，关联match表
    private Integer sportType;         // 运动类型：1-足球，2-篮球，3-排球
    private Integer teamId;            // 队伍ID，关联team表
    private Integer playerId;          // 球员ID（进球者/犯规者/吃牌者/换上球员）
    private Integer relatedPlayerId;   // 关联球员ID（助攻球员/被换下球员/被犯规球员）
    private Integer type;              // 事件类型：1-进球，2-红牌，3-黄牌，4-犯规，5-换人
    private String description;        // 事件描述
    private Integer eventTime;         // 事件发生时间（分钟）
    private String extraInfo;          // 额外信息
    private LocalDateTime createTime;         // 创建时间
    private LocalDateTime updateTime;         // 修改时间
}