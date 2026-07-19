package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 比赛事件 VO（对应 v_match_event 视图）
 */
public class MatchEventVO {
    private Long eventId;               // 事件ID
    private Long matchId;              // 比赛ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍名称（简称）
    private Integer playerId;           // 球员ID
    private String playerName;          // 球员名称（球衣名）
    private Integer playerJersey;       // 球员球衣号码
    private Integer relatedPlayerId;    // 关联球员ID
    private String relatedPlayerName;   // 关联球员名称
    private Integer relatedPlayerJersey; // 关联球员球衣号码
    private Integer type;               // 事件类型编号
    private String typeName;            // 事件类型名称
    private String description;         // 事件描述
    private Integer eventTime;          // 事件发生时间（分钟）
    private String eventTimeDisplay;    // 事件时间显示
    private String extraInfo;           // 扩展信息（JSON）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;   // 创建时间

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Integer getPlayerJersey() { return playerJersey; }
    public void setPlayerJersey(Integer playerJersey) { this.playerJersey = playerJersey; }

    public Integer getRelatedPlayerId() { return relatedPlayerId; }
    public void setRelatedPlayerId(Integer relatedPlayerId) { this.relatedPlayerId = relatedPlayerId; }

    public String getRelatedPlayerName() { return relatedPlayerName; }
    public void setRelatedPlayerName(String relatedPlayerName) { this.relatedPlayerName = relatedPlayerName; }

    public Integer getRelatedPlayerJersey() { return relatedPlayerJersey; }
    public void setRelatedPlayerJersey(Integer relatedPlayerJersey) { this.relatedPlayerJersey = relatedPlayerJersey; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getEventTime() { return eventTime; }
    public void setEventTime(Integer eventTime) { this.eventTime = eventTime; }

    public String getEventTimeDisplay() { return eventTimeDisplay; }
    public void setEventTimeDisplay(String eventTimeDisplay) { this.eventTimeDisplay = eventTimeDisplay; }

    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
