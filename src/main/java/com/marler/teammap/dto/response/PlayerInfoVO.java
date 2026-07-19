package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 球员信息 VO（对应 v_player_info 视图）
 */
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

    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamShortName() { return teamShortName; }
    public void setTeamShortName(String teamShortName) { this.teamShortName = teamShortName; }

    public String getTeamLogo() { return teamLogo; }
    public void setTeamLogo(String teamLogo) { this.teamLogo = teamLogo; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public Integer getTeamGender() { return teamGender; }
    public void setTeamGender(Integer teamGender) { this.teamGender = teamGender; }

    public String getTeamGenderName() { return teamGenderName; }
    public void setTeamGenderName(String teamGenderName) { this.teamGenderName = teamGenderName; }

    public String getTeamDescription() { return teamDescription; }
    public void setTeamDescription(String teamDescription) { this.teamDescription = teamDescription; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getPortraitPhoto() { return portraitPhoto; }
    public void setPortraitPhoto(String portraitPhoto) { this.portraitPhoto = portraitPhoto; }

    public LocalDateTime getJoinTime() { return joinTime; }
    public void setJoinTime(LocalDateTime joinTime) { this.joinTime = joinTime; }

    public LocalDateTime getMemberUpdateTime() { return memberUpdateTime; }
    public void setMemberUpdateTime(LocalDateTime memberUpdateTime) { this.memberUpdateTime = memberUpdateTime; }

    public String getJerseyName() { return jerseyName; }
    public void setJerseyName(String jerseyName) { this.jerseyName = jerseyName; }

    public Integer getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(Integer jerseyNumber) { this.jerseyNumber = jerseyNumber; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public LocalDateTime getPlayerCreateTime() { return playerCreateTime; }
    public void setPlayerCreateTime(LocalDateTime playerCreateTime) { this.playerCreateTime = playerCreateTime; }

    public LocalDateTime getPlayerUpdateTime() { return playerUpdateTime; }
    public void setPlayerUpdateTime(LocalDateTime playerUpdateTime) { this.playerUpdateTime = playerUpdateTime; }
}
