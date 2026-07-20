package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 赛事球员信息 VO（对应 v_tournament_player_info 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentPlayerInfoVO {
    private Long relId;               // 关联ID
    private Long tournamentId;        // 赛事ID
    private String tournamentName;    // 赛事名称
    private Long userId;              // 用户ID
    private String username;          // 用户名
    private String jerseyName;        // 球衣名
    private Integer jerseyNumber;     // 球衣号码
    private String position;          // 场上位置
    private Long teamId;              // 队伍ID
    private String teamName;          // 队伍名称
    private String teamShortName;     // 队伍简称
    private String teamLogo;          // 队伍Logo
    private Integer teamGender;       // 队伍性别
    private String teamGenderName;    // 队伍性别名称
    private Integer teamSportType;    // 运动类型
    private String teamSportTypeName; // 运动类型名称
    private Integer role;             // 角色：1-队员，2-队长，3-教练，4-领队
    private String roleName;          // 角色名称
    private String portraitPhoto;     // 定妆照
    private Integer playerStatus;     // 球员参赛状态：1-可出战，2-禁赛中
    private String playerStatusName;  // 球员参赛状态名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime; // 报名时间
}
