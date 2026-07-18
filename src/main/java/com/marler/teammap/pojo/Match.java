package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//CREATE TABLE `match` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '比赛ID，主键',
//        `tournament_id` INT UNSIGNED NOT NULL COMMENT '关联赛事ID，关联tournament表',
//        `team1_id` INT UNSIGNED NOT NULL COMMENT '队伍1ID，关联team表',
//        `team2_id` INT UNSIGNED NOT NULL COMMENT '队伍2ID，关联team表',
//        `team1_score` TINYINT(3) UNSIGNED DEFAULT 0 COMMENT '队伍1得分',
//        `team2_score` TINYINT(3) UNSIGNED DEFAULT 0 COMMENT '队伍2得分',
//        `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-未开始，2-进行中，3-已结束，4-结算中',
//        `stage` TINYINT(1) UNSIGNED NOT NULL COMMENT '阶段：1-友谊赛，2-小组赛，3-附加赛 4-八分之一决赛，5-四分之一决赛，6-半决赛，7-三四名决赛，8-决赛',
//        `name` VARCHAR(200) DEFAULT NULL COMMENT '比赛名称（如：A组第1轮 文学院 vs 外国语学院）',
//        `match_time` DATETIME DEFAULT NULL COMMENT '比赛开始时间',
//        `location` VARCHAR(200) DEFAULT NULL COMMENT '比赛地点',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_tournament_teams` (`tournament_id`, `team1_id`, `team2_id`, `match_time`),
//KEY `idx_tournament_id` (`tournament_id`),
//KEY `idx_team1_id` (`team1_id`),
//KEY `idx_team2_id` (`team2_id`),
//KEY `idx_status` (`status`),
//KEY `idx_stage` (`stage`),
//KEY `idx_match_time` (`match_time`),
//CONSTRAINT `fk_match_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_match_team1` FOREIGN KEY (`team1_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
//CONSTRAINT `fk_match_team2` FOREIGN KEY (`team2_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛表';
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Long id;                 // 比赛ID，主键
    private Integer tournamentId;    // 关联赛事ID，关联tournament表
    private Integer groupStageId;    // 小组赛ID，关联group_stage表（仅小组赛阶段使用）
    private String name;             // 比赛名称（如：A组第1轮 文学院 vs 外国语学院）
    private Integer team1Id;         // 队伍1ID，关联team表
    private Integer team2Id;         // 队伍2ID，关联team表
    private Integer team1Score;      // 队伍1得分
    private Integer team2Score;      // 队伍2得分
    private Integer status;          // 状态：1-未开始，2-进行中，3-已结束，4-结算中
    private Integer stage;           // 阶段：1-友谊赛，2-小组赛，3-附加赛 4-八分之一决赛，5-四分之一决赛，6-半决赛，7-三四名决赛，8-决赛
    private String matchTime;        // 比赛开始时间
    private String location;         // 比赛地点
    private String createTime;       // 创建时间
    private String updateTime;       // 修改时间
}
