package com.marler.teammap.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `group_stage` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '小组赛ID，主键',
//        `tournament_id` INT UNSIGNED NOT NULL COMMENT '赛事ID，关联tournament表',
//        `name` VARCHAR(100) NOT NULL COMMENT '小组赛名称（如：2026年足球校联赛小组赛）',
//        `team_count` TINYINT(1) UNSIGNED NOT NULL COMMENT '球队总数',
//        `direct_advance` TINYINT(1) UNSIGNED NOT NULL COMMENT '直接出线数（小组排名直接晋级）',
//        `indirect_advance` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '间接出线数（通过附加赛晋级）',
//        `round_type` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '循环数：1-单循环，2-双循环',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//PRIMARY KEY (`id`),
//KEY `idx_tournament_id` (`tournament_id`),
//KEY `idx_create_time` (`create_time`),
//CONSTRAINT `fk_gs_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组赛表';

public class GroupStage {
    private Integer id;              // 小组赛ID，主键
    private Integer tournamentId;    // 赛事ID，关联tournament表
    private String name;             // 小组赛名称（如：2026年足球校联赛小组赛）
    private Integer teamCount;       // 球队总数
    private Integer directAdvance;   // 直接出线数（小组排名直接晋级）
    private Integer indirectAdvance; // 间接出线数（通过附加赛晋级）
    private Integer roundType;       // 循环数：1-单循环，2-双循环
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;       // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;       // 修改时间
}
