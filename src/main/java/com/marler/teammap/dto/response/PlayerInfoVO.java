package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 球员信息 VO（对应 v_player_info 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfoVO {
    private Integer playerId;           // 球员ID
    private Integer userId;             // 用户ID
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍名称
    private String teamShortName;       // 队伍简称
    private String teamLogo;            // 队伍Logo
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private Integer teamGender;         // 队伍性别
    private String teamGenderName;      // 队伍性别名称
    private String teamDescription;     // 队伍描述

    private Long memberId;              // 成员关联ID
    private Integer role;               // 角色：1-队员，2-队长，3-教练，4-领队
    private String roleName;            // 角色名称
    private String portraitPhoto;       // 头像
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;     // 加入时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime memberUpdateTime; // 成员信息更新时间

    private String jerseyName;          // 球衣名
    private Integer jerseyNumber;       // 球衣号码
    private String position;            // 场上位置
    private Integer status;             // 状态：1-可出战，2-禁赛中
    private String statusName;          // 状态名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playerCreateTime; // 球员创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playerUpdateTime; // 球员更新时间
}
