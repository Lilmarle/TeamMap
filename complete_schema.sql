-- =====================================================
-- 完整数据库脚本 - teammap_db
-- 包含所有表、触发器、视图
-- 修改说明: 允许教练/领队(role>=3)创建球员记录和自动报名赛事
-- =====================================================

-- =====================================================
-- 1. 队伍表 (team)
-- =====================================================
DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
                        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '队伍ID，主键',
                        `name` VARCHAR(100) NOT NULL COMMENT '队伍名称',
                        `short_name` VARCHAR(50) DEFAULT NULL COMMENT '队伍简称',
                        `logo` VARCHAR(500) DEFAULT NULL COMMENT '队伍Logo URL地址',
                        `description` VARCHAR(500) DEFAULT NULL COMMENT '队伍描述',
                        `type` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '运动类型：1-足球，2-篮球，3-排球',
                        `gender` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '性别：1-男，2-女，3-混合',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_name` (`name`),
                        KEY `idx_type` (`type`),
                        KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队伍表';


-- =====================================================
-- 2. 球队学院关联表 (team_college_rel)
-- =====================================================
DROP TABLE IF EXISTS `team_college_rel`;

CREATE TABLE `team_college_rel` (
                                    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
                                    `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
                                    `rank` TINYINT(1) UNSIGNED NOT NULL COMMENT '队伍级别：1-院队，2-校队，3-班队',
                                    `college_id` INT UNSIGNED DEFAULT NULL COMMENT '学院ID，关联college表（校队可为空）',
                                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_team_id` (`team_id`),
                                    KEY `idx_college_id` (`college_id`),
                                    KEY `idx_rank` (`rank`),
                                    KEY `idx_team_college` (`team_id`, `college_id`),
                                    CONSTRAINT `fk_tcr_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `fk_tcr_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球队学院关联表';


-- =====================================================
-- 3. 队伍成员表 (team_member)
-- =====================================================
DROP TABLE IF EXISTS `team_member`;

CREATE TABLE `team_member` (
                               `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成员ID，主键',
                               `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
                               `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID，关联user表',
                               `role` TINYINT(1) UNSIGNED NOT NULL COMMENT '角色：1-队员，2-队长，3-教练，4-领队',
                               `portrait_photo` VARCHAR(500) DEFAULT NULL COMMENT '定妆照URL地址',
                               `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_team_user` (`team_id`, `user_id`),
                               KEY `idx_team_id` (`team_id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_role` (`role`),
                               KEY `idx_join_time` (`join_time`),
                               CONSTRAINT `fk_tm_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_tm_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队伍成员表';
ALTER TABLE `team_member`
    ADD COLUMN `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-申请中，2-已加入，3-已退出' AFTER `role`,
    ADD KEY `idx_status` (`status`);

-- =====================================================
-- 4. 球员表 (player)
-- =====================================================
DROP TABLE IF EXISTS `player`;

CREATE TABLE `player` (
                          `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '球员ID，主键',
                          `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID，关联user表',
                          `tm_id` INT UNSIGNED NOT NULL COMMENT '队伍成员关联ID，关联team_member表',
                          `jersey_name` VARCHAR(50) DEFAULT NULL COMMENT '球衣名称（印名）',
                          `jersey_number` TINYINT(2) UNSIGNED DEFAULT NULL COMMENT '球衣号码（1-99）',
                          `position` VARCHAR(50) DEFAULT NULL COMMENT '位置（如：前锋、后卫、守门员）',
                          `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-可出战，2-禁赛中',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_tm_id` (`tm_id`),
                          UNIQUE KEY `uk_user_team` (`user_id`, `tm_id`),
                          UNIQUE KEY `uk_team_number` (`tm_id`, `jersey_number`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_status` (`status`),
                          KEY `idx_position` (`position`),
                          CONSTRAINT `fk_player_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT `fk_player_team_member` FOREIGN KEY (`tm_id`) REFERENCES `team_member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球员表';


-- =====================================================
-- 5. 赛事表 (tournament)
-- =====================================================
DROP TABLE IF EXISTS `tournament`;

CREATE TABLE `tournament` (
                              `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '赛事ID，主键',
                              `name` VARCHAR(100) NOT NULL COMMENT '赛事名称',
                              `creator_id` INT UNSIGNED NOT NULL COMMENT '创办者ID，关联user表',
                              `type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球',
                              `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-筹办中，2-进行中，3-已结束',
                              `start_time` DATETIME NOT NULL COMMENT '开始时间',
                              `end_time` DATETIME NOT NULL COMMENT '结束时间',
                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_name` (`name`),
                              KEY `idx_creator_id` (`creator_id`),
                              KEY `idx_type` (`type`),
                              KEY `idx_status` (`status`),
                              KEY `idx_start_time` (`start_time`),
                              KEY `idx_end_time` (`end_time`),
                              KEY `idx_time_range` (`start_time`, `end_time`),
                              CONSTRAINT `fk_tournament_creator` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='赛事表';


-- =====================================================
-- 6. 球队参赛表 (tournament_team)
-- =====================================================
DROP TABLE IF EXISTS `tournament_team`;

CREATE TABLE `tournament_team` (
                                   `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
                                   `tournament_id` INT UNSIGNED NOT NULL COMMENT '赛事ID，关联tournament表',
                                   `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
                                   `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-申请中，2-已通过，3-未通过',
                                   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（申请时间）',
                                   `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（审核时间）',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_tournament_team` (`tournament_id`, `team_id`),
                                   KEY `idx_tournament_id` (`tournament_id`),
                                   KEY `idx_team_id` (`team_id`),
                                   KEY `idx_status` (`status`),
                                   CONSTRAINT `fk_tt_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                   CONSTRAINT `fk_tt_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球队参赛表';


-- =====================================================
-- 7. 赛事球员表 (tournament_player)
-- =====================================================
DROP TABLE IF EXISTS `tournament_player`;

CREATE TABLE `tournament_player` (
                                     `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
                                     `tournament_id` INT UNSIGNED NOT NULL COMMENT '赛事ID，关联tournament表',
                                     `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID（球员），关联user表',
                                     `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-可出战，2-禁赛中',
                                     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（报名时间）',
                                     `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_tournament_user` (`tournament_id`, `user_id`),
                                     KEY `idx_tournament_id` (`tournament_id`),
                                     KEY `idx_user_id` (`user_id`),
                                     KEY `idx_status` (`status`),
                                     CONSTRAINT `ftp_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                     CONSTRAINT `ftp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='赛事球员表';


-- =====================================================
-- 8. 小组赛表 (group_stage)
-- =====================================================
DROP TABLE IF EXISTS `group_stage`;

CREATE TABLE `group_stage` (
                               `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '小组赛ID，主键',
                               `tournament_id` INT UNSIGNED NOT NULL COMMENT '赛事ID，关联tournament表',
                               `name` VARCHAR(100) NOT NULL COMMENT '小组赛名称',
                               `team_count` TINYINT(1) UNSIGNED NOT NULL COMMENT '球队总数',
                               `direct_advance` TINYINT(1) UNSIGNED NOT NULL COMMENT '直接出线数',
                               `indirect_advance` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '间接出线数',
                               `round_type` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '循环数：1-单循环，2-双循环',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_tournament_id` (`tournament_id`),
                               KEY `idx_create_time` (`create_time`),
                               CONSTRAINT `fk_gs_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组赛表';


-- =====================================================
-- 9. 小组赛球队成绩表 (group_stage_team)
-- =====================================================
DROP TABLE IF EXISTS `group_stage_team`;

CREATE TABLE `group_stage_team` (
                                    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
                                    `group_stage_id` INT UNSIGNED NOT NULL COMMENT '小组赛ID，关联group_stage表',
                                    `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
                                    `win` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '胜场数',
                                    `draw` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '平场数',
                                    `loss` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '负场数',
                                    `points` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '积分',
                                    `goals_for` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '进球数（得分）',
                                    `goals_against` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '失球数（失分）',
                                    `goal_difference` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '净胜球（进球-失球）',
                                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_group_team` (`group_stage_id`, `team_id`),
                                    KEY `idx_group_stage_id` (`group_stage_id`),
                                    KEY `idx_team_id` (`team_id`),
                                    KEY `idx_points` (`points`),
                                    KEY `idx_goal_difference` (`goal_difference`),
                                    CONSTRAINT `fk_gst_group_stage` FOREIGN KEY (`group_stage_id`) REFERENCES `group_stage` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `fk_gst_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组赛球队成绩表';


-- =====================================================
-- 10. 比赛表 (match)
-- =====================================================
DROP TABLE IF EXISTS `match`;

CREATE TABLE `match` (
                         `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '比赛ID，主键',
                         `tournament_id` INT UNSIGNED NOT NULL COMMENT '关联赛事ID，关联tournament表',
                         `sport_type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球',
                         `group_stage_id` INT UNSIGNED DEFAULT NULL COMMENT '小组赛ID，关联group_stage表',
                         `name` VARCHAR(200) DEFAULT NULL COMMENT '比赛名称',
                         `team1_id` INT UNSIGNED NOT NULL COMMENT '队伍1ID，关联team表',
                         `team2_id` INT UNSIGNED NOT NULL COMMENT '队伍2ID，关联team表',
                         `team1_score` TINYINT(3) UNSIGNED DEFAULT 0 COMMENT '队伍1得分',
                         `team2_score` TINYINT(3) UNSIGNED DEFAULT 0 COMMENT '队伍2得分',
                         `set_score` VARCHAR(50) DEFAULT NULL COMMENT '排球局分（如：25-23,25-20,22-25,25-18）',
                         `aggregate_score` VARCHAR(20) DEFAULT NULL COMMENT '两回合总比分（如：3-2）',
                         `winner_id` INT UNSIGNED DEFAULT NULL COMMENT '获胜队伍ID，关联team表',
                         `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-未开始，2-进行中，3-已结束，4-结算中',
                         `stage` TINYINT(1) UNSIGNED NOT NULL COMMENT '阶段：1-友谊赛，2-小组赛，3-附加赛，4-八分之一决赛，5-四分之一决赛，6-半决赛，7-三四名决赛，8-决赛',
                         `match_time` DATETIME DEFAULT NULL COMMENT '比赛开始时间',
                         `location` VARCHAR(200) DEFAULT NULL COMMENT '比赛地点',
                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_tournament_teams` (`tournament_id`, `team1_id`, `team2_id`, `match_time`),
                         KEY `idx_tournament_id` (`tournament_id`),
                         KEY `idx_sport_type` (`sport_type`),
                         KEY `idx_group_stage_id` (`group_stage_id`),
                         KEY `idx_team1_id` (`team1_id`),
                         KEY `idx_team2_id` (`team2_id`),
                         KEY `idx_winner_id` (`winner_id`),
                         KEY `idx_status` (`status`),
                         KEY `idx_stage` (`stage`),
                         KEY `idx_match_time` (`match_time`),
                         CONSTRAINT `fk_match_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT `fk_match_group_stage` FOREIGN KEY (`group_stage_id`) REFERENCES `group_stage` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
                         CONSTRAINT `fk_match_team1` FOREIGN KEY (`team1_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                         CONSTRAINT `fk_match_team2` FOREIGN KEY (`team2_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                         CONSTRAINT `fk_match_winner` FOREIGN KEY (`winner_id`) REFERENCES `team` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛表';


-- =====================================================
-- 11. 比赛球员阵容表 (match_player)
-- =====================================================
DROP TABLE IF EXISTS `match_player`;

CREATE TABLE `match_player` (
                                `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
                                `match_id` INT UNSIGNED NOT NULL COMMENT '比赛ID，关联match表',
                                `sport_type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球',
                                `player_id` INT UNSIGNED NOT NULL COMMENT '球员ID，关联player表',
                                `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0-未出场，1-首发，2-替补',
                                `play_time` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '出场时间（分钟），0表示打满全场',
                                `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_match_player` (`match_id`, `player_id`),
                                KEY `idx_match_id` (`match_id`),
                                KEY `idx_sport_type` (`sport_type`),
                                KEY `idx_player_id` (`player_id`),
                                KEY `idx_status` (`status`),
                                CONSTRAINT `fk_mp_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT `fk_mp_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛球员阵容表';


-- =====================================================
-- 12. 比赛事件表 (match_event) - 唯一真实数据源
-- =====================================================
DROP TABLE IF EXISTS `match_event`;

CREATE TABLE `match_event` (
                               `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '事件ID，主键',
                               `match_id` INT UNSIGNED NOT NULL COMMENT '比赛ID，关联match表',
                               `sport_type` TINYINT(1) UNSIGNED NOT NULL COMMENT '运动类型：1-足球，2-篮球，3-排球',
                               `team_id` INT UNSIGNED NOT NULL COMMENT '队伍ID，关联team表',
                               `player_id` INT UNSIGNED DEFAULT NULL COMMENT '球员ID（进球者/犯规者/吃牌者/换上球员）',
                               `related_player_id` INT UNSIGNED DEFAULT NULL COMMENT '关联球员ID（助攻球员/被换下球员/被犯规球员）',
                               `type` TINYINT(1) UNSIGNED NOT NULL COMMENT '事件类型：1-进球/得分，2-助攻，3-黄牌，4-红牌，5-犯规，6-换人',
                               `description` VARCHAR(500) DEFAULT NULL COMMENT '事件描述',
                               `event_time` INT UNSIGNED NOT NULL COMMENT '事件发生时间（分钟）',
                               `extra_info` VARCHAR(200) DEFAULT NULL COMMENT '额外信息',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_match_id` (`match_id`),
                               KEY `idx_sport_type` (`sport_type`),
                               KEY `idx_team_id` (`team_id`),
                               KEY `idx_player_id` (`player_id`),
                               KEY `idx_related_player_id` (`related_player_id`),
                               KEY `idx_type` (`type`),
                               KEY `idx_event_time` (`event_time`),
                               CONSTRAINT `fk_me_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_me_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_me_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
                               CONSTRAINT `fk_me_related_player` FOREIGN KEY (`related_player_id`) REFERENCES `player` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛事件表（唯一数据源）';


-- =====================================================
-- 13. 球员触发器 (before_insert_player)
-- 【已修改】移除了角色限制，允许教练(role=3)/领队(role=4)创建球员记录
-- =====================================================
DROP TRIGGER IF EXISTS `before_insert_player`;

DELIMITER $$

CREATE TRIGGER `before_insert_player`
    BEFORE INSERT ON `player`
    FOR EACH ROW
BEGIN
    DECLARE v_role TINYINT(1) UNSIGNED;
    DECLARE v_team_id INT UNSIGNED;
    DECLARE v_exists INT;

    -- 获取角色和队伍ID
    SELECT tm.`role`, tm.`team_id` INTO v_role, v_team_id
    FROM `team_member` tm
    WHERE tm.`id` = NEW.tm_id;

    -- 号码唯一性检查（对所有角色都适用）
    IF NEW.jersey_number IS NOT NULL THEN
        SELECT COUNT(*) INTO v_exists
        FROM `player` p
                 INNER JOIN `team_member` tm ON p.tm_id = tm.id
        WHERE tm.team_id = v_team_id AND p.jersey_number = NEW.jersey_number;

        IF v_exists > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '球衣号码已被占用，请使用其他号码';
        END IF;
    END IF;
END$$

DELIMITER ;


-- =====================================================
-- 14. 赛事报名触发器
-- =====================================================
DROP TRIGGER IF EXISTS `before_insert_tournament_team`;

DELIMITER $$

CREATE TRIGGER `before_insert_tournament_team`
    BEFORE INSERT ON `tournament_team`
    FOR EACH ROW
BEGIN
    DECLARE v_tournament_type TINYINT(1) UNSIGNED;
    DECLARE v_team_type TINYINT(1) UNSIGNED;

    SELECT `type` INTO v_tournament_type FROM `tournament` WHERE `id` = NEW.tournament_id;
    SELECT `type` INTO v_team_type FROM `team` WHERE `id` = NEW.team_id;

    IF v_tournament_type != v_team_type THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '赛事类型与队伍类型不匹配，无法报名';
    END IF;
END$$

DELIMITER ;


-- =====================================================
-- 15. 赛事审核通过后自动报名触发器
-- 【已修改】游标条件从 tm.role <= 2 改为 tm.role IN (1,2,3,4)
-- 允许教练/领队自动报名赛事
-- =====================================================
DROP TRIGGER IF EXISTS `after_update_tournament_team`;

DELIMITER $$

CREATE TRIGGER `after_update_tournament_team`
    AFTER UPDATE ON `tournament_team`
    FOR EACH ROW
BEGIN
    DECLARE v_done INT DEFAULT FALSE;
    DECLARE v_user_id INT UNSIGNED;
    DECLARE v_cursor CURSOR FOR
        SELECT DISTINCT tm.user_id
        FROM `team_member` tm
        WHERE tm.team_id = NEW.team_id AND tm.role IN (1, 2, 3, 4);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

    IF NEW.status = 2 AND OLD.status != 2 THEN
        OPEN v_cursor;
        read_loop: LOOP
            FETCH v_cursor INTO v_user_id;
            IF v_done THEN
                LEAVE read_loop;
            END IF;
            INSERT IGNORE INTO `tournament_player`
            (`tournament_id`, `user_id`, `status`, `create_time`, `update_time`)
            VALUES (NEW.tournament_id, v_user_id, 1, NOW(), NOW());
        END LOOP;
        CLOSE v_cursor;
    END IF;
END$$

DELIMITER ;


-- =====================================================
-- 视图部分
-- =====================================================


-- =====================================================
-- 视图1: 队伍信息 (v_team_info)
-- =====================================================
DROP VIEW IF EXISTS `v_team_info`;

CREATE VIEW `v_team_info` AS
SELECT
    t.id AS team_id,
    t.name AS team_name,
    t.short_name AS team_short_name,
    t.logo AS team_logo,
    t.description AS team_description,
    t.gender AS gender,
    CASE t.gender
        WHEN 1 THEN '男'
        WHEN 2 THEN '女'
        WHEN 3 THEN '混合'
        ELSE '未知'
        END AS gender_name,
    c.full_name AS college_full_name,
    c.short_name AS college_short_name,
    tcr.rank AS team_rank,
    CASE tcr.rank
        WHEN 1 THEN '院队'
        WHEN 2 THEN '校队'
        WHEN 3 THEN '班队'
        ELSE '未知'
        END AS rank_name,
    t.type AS sport_type,
    CASE t.type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    COUNT(DISTINCT tm.id) AS member_count,
    COUNT(CASE WHEN tm.role = 2 THEN 1 END) AS captain_count,
    COUNT(CASE WHEN tm.role = 1 THEN 1 END) AS player_count,
    COUNT(CASE WHEN tm.role = 3 THEN 1 END) AS coach_count,
    COUNT(CASE WHEN tm.role = 4 THEN 1 END) AS leader_count,
    t.create_time,
    t.update_time
FROM team t
         INNER JOIN team_college_rel tcr ON t.id = tcr.team_id
         LEFT JOIN college c ON tcr.college_id = c.id
         LEFT JOIN team_member tm ON t.id = tm.team_id
GROUP BY t.id, t.name, t.short_name, t.logo, t.description, t.gender, c.full_name, c.short_name, tcr.rank, t.type;


-- =====================================================
-- 视图2: 球员信息 (v_player_info)
-- =====================================================
DROP VIEW IF EXISTS `v_player_info`;

CREATE VIEW `v_player_info` AS
SELECT
    p.id AS player_id,
    p.user_id,
    t.id AS team_id,
    t.name AS team_name,
    t.short_name AS team_short_name,
    t.logo AS team_logo,
    t.type AS sport_type,
    CASE t.type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    t.gender AS team_gender,
    CASE t.gender
        WHEN 1 THEN '男'
        WHEN 2 THEN '女'
        WHEN 3 THEN '混合'
        ELSE '未知'
        END AS team_gender_name,
    t.description AS team_description,
    tm.id AS member_id,
    tm.role,
    CASE tm.role
        WHEN 1 THEN '队员'
        WHEN 2 THEN '队长'
        WHEN 3 THEN '教练'
        WHEN 4 THEN '领队'
        ELSE '未知'
        END AS role_name,
    tm.portrait_photo,
    tm.join_time,
    tm.update_time AS member_update_time,
    p.jersey_name,
    p.jersey_number,
    p.position,
    p.status,
    CASE p.status
        WHEN 1 THEN '可出战'
        WHEN 2 THEN '禁赛中'
        ELSE '未知'
        END AS status_name,
    p.create_time AS player_create_time,
    p.update_time AS player_update_time
FROM player p
         INNER JOIN team_member tm ON p.tm_id = tm.id
         INNER JOIN team t ON tm.team_id = t.id;


-- =====================================================
-- 视图3: 赛事参赛队伍 (v_tournament_team_info)
-- =====================================================
DROP VIEW IF EXISTS `v_tournament_team_info`;

CREATE VIEW `v_tournament_team_info` AS
SELECT
    tt.id AS rel_id,
    tt.tournament_id,
    t.name AS tournament_name,
    tt.team_id,
    tm.name AS team_name,
    tm.short_name AS team_short_name,
    tm.logo AS team_logo,
    tm.type AS sport_type,
    CASE tm.type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    tm.gender,
    CASE tm.gender
        WHEN 1 THEN '男'
        WHEN 2 THEN '女'
        WHEN 3 THEN '混合'
        ELSE '未知'
        END AS gender_name,
    tt.status,
    CASE tt.status
        WHEN 1 THEN '申请中'
        WHEN 2 THEN '已通过'
        WHEN 3 THEN '未通过'
        ELSE '未知'
        END AS status_name,
    COUNT(DISTINCT tmm.id) AS member_count,
    tt.create_time AS apply_time,
    tt.update_time AS audit_time
FROM tournament_team tt
         INNER JOIN tournament t ON tt.tournament_id = t.id
         INNER JOIN team tm ON tt.team_id = tm.id
         LEFT JOIN team_member tmm ON tm.id = tmm.team_id
GROUP BY tt.id, tt.tournament_id, t.name, tt.team_id, tm.name, tm.short_name, tm.logo, tm.type, tm.gender, tt.status, tt.create_time, tt.update_time;


-- =====================================================
-- 视图4: 赛事球员信息 (v_tournament_player_info)
-- =====================================================
DROP VIEW IF EXISTS `v_tournament_player_info`;

CREATE VIEW `v_tournament_player_info` AS
SELECT
    tp.id AS rel_id,
    tp.tournament_id,
    t.name AS tournament_name,
    tp.user_id,
    u.username,
    -- player 字段
    p.jersey_name,
    p.jersey_number,
    p.position,
    -- team 字段（通过 team_member 关联）
    team.id AS team_id,
    team.name AS team_name,
    team.short_name AS team_short_name,
    team.logo AS team_logo,
    team.gender AS team_gender,
    CASE team.gender
        WHEN 1 THEN '男'
        WHEN 2 THEN '女'
        WHEN 3 THEN '混合'
        ELSE '未知'
        END AS team_gender_name,
    team.type AS team_sport_type,
    CASE team.type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS team_sport_type_name,
    -- team_member 字段
    tm.role,
    CASE tm.role
        WHEN 1 THEN '队员'
        WHEN 2 THEN '队长'
        WHEN 3 THEN '教练'
        WHEN 4 THEN '领队'
        ELSE '未知'
        END AS role_name,
    tm.portrait_photo,
    -- tournament_player 字段
    tp.status AS player_status,
    CASE tp.status
        WHEN 1 THEN '可出战'
        WHEN 2 THEN '禁赛中'
        ELSE '未知'
        END AS player_status_name,
    tp.create_time AS register_time
FROM tournament_player tp
         INNER JOIN tournament t ON tp.tournament_id = t.id
         INNER JOIN user u ON tp.user_id = u.id
         LEFT JOIN player p ON tp.user_id = p.user_id
         LEFT JOIN team_member tm ON p.tm_id = tm.id
         LEFT JOIN team ON tm.team_id = team.id;


-- =====================================================
-- 视图5: 比赛详情 (v_match_detail)
-- =====================================================
DROP VIEW IF EXISTS `v_match_detail`;

CREATE VIEW `v_match_detail` AS
SELECT
    m.id AS match_id,
    m.name AS match_name,
    m.tournament_id,
    t.name AS tournament_name,
    m.sport_type,
    CASE m.sport_type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    m.group_stage_id,
    gs.name AS group_stage_name,
    m.stage,
    CASE m.stage
        WHEN 1 THEN '友谊赛'
        WHEN 2 THEN '小组赛'
        WHEN 3 THEN '附加赛'
        WHEN 4 THEN '八分之一决赛'
        WHEN 5 THEN '四分之一决赛'
        WHEN 6 THEN '半决赛'
        WHEN 7 THEN '三四名决赛'
        WHEN 8 THEN '决赛'
        ELSE '未知'
        END AS stage_name,
    m.status,
    CASE m.status
        WHEN 1 THEN '未开始'
        WHEN 2 THEN '进行中'
        WHEN 3 THEN '已结束'
        WHEN 4 THEN '结算中'
        ELSE '未知'
        END AS status_name,
    m.match_time,
    m.location,
    m.team1_id,
    t1.name AS team1_name,
    t1.short_name AS team1_short_name,
    t1.logo AS team1_logo,
    m.team1_score,
    m.team2_id,
    t2.name AS team2_name,
    t2.short_name AS team2_short_name,
    t2.logo AS team2_logo,
    m.team2_score,
    m.set_score,
    m.aggregate_score,
    m.winner_id,
    tw.short_name AS winner_name,
    m.create_time,
    m.update_time
FROM `match` m
         INNER JOIN tournament t ON m.tournament_id = t.id
         LEFT JOIN group_stage gs ON m.group_stage_id = gs.id
         INNER JOIN team t1 ON m.team1_id = t1.id
         INNER JOIN team t2 ON m.team2_id = t2.id
         LEFT JOIN team tw ON m.winner_id = tw.id;


-- =====================================================
-- 视图6: 比赛事件 (v_match_event)
-- =====================================================
DROP VIEW IF EXISTS `v_match_event`;

CREATE VIEW `v_match_event` AS
SELECT
    me.id AS event_id,
    me.match_id,
    me.sport_type,
    CASE me.sport_type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    me.team_id,
    t.short_name AS team_name,
    me.player_id,
    p1.jersey_name AS player_name,
    p1.jersey_number AS player_jersey,
    me.related_player_id,
    p2.jersey_name AS related_player_name,
    p2.jersey_number AS related_player_jersey,
    me.type,
    CASE me.sport_type
        WHEN 1 THEN
            CASE me.type
                WHEN 1 THEN '进球'
                WHEN 2 THEN '助攻'
                WHEN 3 THEN '黄牌'
                WHEN 4 THEN '红牌'
                WHEN 5 THEN '犯规'
                WHEN 6 THEN '换人'
                ELSE '未知'
                END
        WHEN 2 THEN
            CASE me.type
                WHEN 1 THEN '得分'
                WHEN 2 THEN '助攻'
                WHEN 3 THEN '犯规'
                WHEN 4 THEN '换人'
                ELSE '未知'
                END
        WHEN 3 THEN
            CASE me.type
                WHEN 1 THEN '得分'
                WHEN 2 THEN '助攻'
                WHEN 3 THEN '犯规'
                WHEN 4 THEN '换人'
                ELSE '未知'
                END
        ELSE '未知'
        END AS type_name,
    me.description,
    me.event_time,
    CONCAT(me.event_time, '分钟') AS event_time_display,
    me.extra_info,
    me.create_time
FROM match_event me
         INNER JOIN team t ON me.team_id = t.id
         LEFT JOIN player p1 ON me.player_id = p1.id
         LEFT JOIN player p2 ON me.related_player_id = p2.id;


-- =====================================================
-- 视图7: 比赛球员表现 (v_match_player_performance)
-- =====================================================
DROP VIEW IF EXISTS `v_match_player_performance`;

CREATE VIEW `v_match_player_performance` AS
SELECT
    mp.id,
    mp.match_id,
    mp.sport_type,
    CASE mp.sport_type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    m.name AS match_name,
    mp.player_id,
    p.jersey_name AS player_name,
    p.jersey_number,
    tm.team_id,
    t.short_name AS team_name,
    mp.status,
    CASE mp.status
        WHEN 0 THEN '未出场'
        WHEN 1 THEN '首发'
        WHEN 2 THEN '替补'
        ELSE '未知'
        END AS status_name,
    mp.play_time,
    CASE
        WHEN mp.play_time = 0 AND mp.status = 1 THEN '全场'
        WHEN mp.play_time > 0 THEN CONCAT(mp.play_time, '分钟')
        ELSE '-'
        END AS play_time_display,
    -- 从 match_event 聚合统计
    COUNT(CASE WHEN me.type = 1 THEN 1 END) AS goals,
    COUNT(CASE WHEN me.type = 2 THEN 1 END) AS assists,
    COUNT(CASE WHEN me.type = 3 THEN 1 END) AS yellow_cards,
    COUNT(CASE WHEN me.type = 4 THEN 1 END) AS red_cards,
    COUNT(CASE WHEN me.type = 5 THEN 1 END) AS fouls,
    -- 综合评分
    (COUNT(CASE WHEN me.type = 1 THEN 1 END) * 3
         + COUNT(CASE WHEN me.type = 2 THEN 1 END) * 2
        - COUNT(CASE WHEN me.type = 3 THEN 1 END)
        - COUNT(CASE WHEN me.type = 4 THEN 1 END) * 2
        - COUNT(CASE WHEN me.type = 5 THEN 1 END) * 0.5) AS performance_score
FROM match_player mp
         INNER JOIN `match` m ON mp.match_id = m.id
         INNER JOIN player p ON mp.player_id = p.id
         INNER JOIN team_member tm ON p.tm_id = tm.id
         INNER JOIN team t ON tm.team_id = t.id
         LEFT JOIN match_event me ON mp.match_id = me.match_id AND mp.player_id = me.player_id
GROUP BY mp.id, mp.match_id, mp.sport_type, m.name, mp.player_id, p.jersey_name, p.jersey_number, tm.team_id, t.short_name, mp.status, mp.play_time;


-- =====================================================
-- 视图8: 球员赛事累计统计 (v_player_tournament_stats)
-- =====================================================
DROP VIEW IF EXISTS `v_player_tournament_stats`;

CREATE VIEW `v_player_tournament_stats` AS
SELECT
    mp.player_id,
    p.jersey_name AS player_name,
    p.jersey_number,
    tm.team_id,
    t.short_name AS team_name,
    m.tournament_id,
    mp.sport_type,
    CASE mp.sport_type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    COUNT(DISTINCT mp.match_id) AS match_played,
    -- 从 match_event 聚合
    SUM(CASE WHEN me.type = 1 THEN 1 ELSE 0 END) AS total_goals,
    SUM(CASE WHEN me.type = 2 THEN 1 ELSE 0 END) AS total_assists,
    SUM(CASE WHEN me.type = 3 THEN 1 ELSE 0 END) AS total_yellow_cards,
    SUM(CASE WHEN me.type = 4 THEN 1 ELSE 0 END) AS total_red_cards,
    SUM(CASE WHEN me.type = 5 THEN 1 ELSE 0 END) AS total_fouls,
    SUM(mp.play_time) AS total_play_time,
    ROUND(AVG(mp.play_time), 0) AS avg_play_time,
    (SUM(CASE WHEN me.type = 1 THEN 1 ELSE 0 END) * 3
        + SUM(CASE WHEN me.type = 2 THEN 1 ELSE 0 END) * 2) AS total_score
FROM match_player mp
         INNER JOIN player p ON mp.player_id = p.id
         INNER JOIN team_member tm ON p.tm_id = tm.id
         INNER JOIN team t ON tm.team_id = t.id
         INNER JOIN `match` m ON mp.match_id = m.id
         LEFT JOIN match_event me ON mp.match_id = me.match_id AND mp.player_id = me.player_id
WHERE mp.status > 0
GROUP BY mp.player_id, p.jersey_name, p.jersey_number, tm.team_id, t.short_name, m.tournament_id, mp.sport_type;


-- =====================================================
-- 视图9: 比赛事件统计 (v_match_event_stats)
-- =====================================================
DROP VIEW IF EXISTS `v_match_event_stats`;

CREATE VIEW `v_match_event_stats` AS
SELECT
    me.match_id,
    me.sport_type,
    CASE me.sport_type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    me.team_id,
    t.short_name AS team_name,
    COUNT(*) AS total_events,
    SUM(CASE WHEN me.type = 1 THEN 1 ELSE 0 END) AS type_1_count,
    SUM(CASE WHEN me.type = 2 THEN 1 ELSE 0 END) AS type_2_count,
    SUM(CASE WHEN me.type = 3 THEN 1 ELSE 0 END) AS type_3_count,
    SUM(CASE WHEN me.type = 4 THEN 1 ELSE 0 END) AS type_4_count,
    SUM(CASE WHEN me.type = 5 THEN 1 ELSE 0 END) AS type_5_count
FROM match_event me
         INNER JOIN team t ON me.team_id = t.id
GROUP BY me.match_id, me.sport_type, me.team_id;


-- =====================================================
-- 视图10: 比赛统计总览 (v_match_stats_overview)
-- =====================================================
DROP VIEW IF EXISTS `v_match_stats_overview`;

CREATE VIEW `v_match_stats_overview` AS
SELECT
    m.id AS match_id,
    m.name AS match_name,
    m.sport_type,
    CASE m.sport_type
        WHEN 1 THEN '足球'
        WHEN 2 THEN '篮球'
        WHEN 3 THEN '排球'
        ELSE '未知'
        END AS sport_type_name,
    m.match_time,
    m.location,
    -- 队伍1
    t1.short_name AS team1,
    m.team1_score AS team1_score,
    m.team1_score AS team1_goals,
    m.team2_score AS team1_goals_against,
    -- 队伍2
    t2.short_name AS team2,
    m.team2_score AS team2_score,
    m.team2_score AS team2_goals,
    m.team1_score AS team2_goals_against,
    -- 比赛信息
    m.status,
    m.stage,
    CASE m.stage
        WHEN 1 THEN '友谊赛'
        WHEN 2 THEN '小组赛'
        WHEN 3 THEN '附加赛'
        WHEN 4 THEN '八分之一决赛'
        WHEN 5 THEN '四分之一决赛'
        WHEN 6 THEN '半决赛'
        WHEN 7 THEN '三四名决赛'
        WHEN 8 THEN '决赛'
        ELSE '未知'
        END AS stage_name,
    m.aggregate_score,
    m.set_score,
    m.winner_id,
    tw.short_name AS winner_name
FROM `match` m
         INNER JOIN team t1 ON m.team1_id = t1.id
         INNER JOIN team t2 ON m.team2_id = t2.id
         LEFT JOIN team tw ON m.winner_id = tw.id;
