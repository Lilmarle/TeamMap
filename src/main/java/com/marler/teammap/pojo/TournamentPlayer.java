package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//CREATE TABLE `tournament_player` (
//        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
//        `tournament_id` INT UNSIGNED NOT NULL COMMENT '赛事ID，关联tournament表',
//        `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID，关联user表',
//        `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0-审核中，1-可出战，2-禁赛中，3-退出',
//        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（报名时间）',
//        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_tournament_user` (`tournament_id`, `user_id`),
//KEY `idx_tournament_id` (`tournament_id`),
//KEY `idx_user_id` (`user_id`),
//KEY `idx_status` (`status`),
//CONSTRAINT `fk_tp_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_tp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球员参赛表';
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentPlayer {
    private Long id;
    private Long tournamentId;
    private Long userId;             // 用户ID，关联user表
    private Integer status;          // 状态：0-审核中 1-可出战，2-禁赛中 3.退出
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
