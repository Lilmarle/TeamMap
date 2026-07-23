-- =====================================================
-- 修改触发器1: before_insert_player
-- 原限制: IF v_role > 2 THEN (只允许队员/队长)
-- 新逻辑: 移除角色限制，保留号码唯一性检查
-- =====================================================
use teammap_db;
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
-- 修改触发器2: after_update_tournament_team
-- 原限制: tm.role <= 2 (只自动报名队员/队长)
-- 新逻辑: tm.role IN (1, 2, 3, 4) (包含教练/领队)
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
