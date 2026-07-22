package com.marler.teammap.dto.request;

/**
 * 修改比赛事件请求
 */
public class UpdateMatchEventRequest {
    private Integer matchId;            // 比赛ID（可选）
    private Integer sportType;          // 运动类型：1-足球，2-篮球，3-排球（可选）
    private Integer teamId;             // 队伍ID（可选）
    private Integer playerId;           // 球员ID（可选）
    private Integer relatedPlayerId;    // 关联球员ID（可选）
    private Integer type;               // 事件类型（可选）
    private String description;         // 事件描述（可选）
    private Integer eventTime;          // 事件发生时间（分钟）（可选）
    private String extraInfo;           // 额外信息（JSON格式）（可选）

    public Integer getMatchId() { return matchId; }
    public void setMatchId(Integer matchId) { this.matchId = matchId; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }

    public Integer getRelatedPlayerId() { return relatedPlayerId; }
    public void setRelatedPlayerId(Integer relatedPlayerId) { this.relatedPlayerId = relatedPlayerId; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getEventTime() { return eventTime; }
    public void setEventTime(Integer eventTime) { this.eventTime = eventTime; }

    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }
}
