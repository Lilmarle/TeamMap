package com.marler.teammap.dto.response;

/**
 * 比赛球员表现 VO（对应 v_match_player_performance 视图）
 */
public class MatchPlayerPerformanceVO {
    private Long id;                    // 关联ID
    private Long matchId;              // 比赛ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private String matchName;           // 比赛名称
    private Integer playerId;           // 球员ID
    private String playerName;          // 球员名称（球衣名）
    private Integer jerseyNumber;       // 球衣号码
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍名称（简称）
    private Integer status;             // 出场状态：0-未出场，1-首发，2-替补
    private String statusName;          // 出场状态名称
    private Integer playTime;           // 出场时间（分钟）
    private String playTimeDisplay;     // 出场时间显示
    private Integer goals;              // 进球数
    private Integer assists;            // 助攻数
    private Integer yellowCards;        // 黄牌数
    private Integer redCards;           // 红牌数
    private Integer fouls;              // 犯规数
    private Double performanceScore;    // 综合评分

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public String getMatchName() { return matchName; }
    public void setMatchName(String matchName) { this.matchName = matchName; }

    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Integer getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(Integer jerseyNumber) { this.jerseyNumber = jerseyNumber; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public Integer getPlayTime() { return playTime; }
    public void setPlayTime(Integer playTime) { this.playTime = playTime; }

    public String getPlayTimeDisplay() { return playTimeDisplay; }
    public void setPlayTimeDisplay(String playTimeDisplay) { this.playTimeDisplay = playTimeDisplay; }

    public Integer getGoals() { return goals; }
    public void setGoals(Integer goals) { this.goals = goals; }

    public Integer getAssists() { return assists; }
    public void setAssists(Integer assists) { this.assists = assists; }

    public Integer getYellowCards() { return yellowCards; }
    public void setYellowCards(Integer yellowCards) { this.yellowCards = yellowCards; }

    public Integer getRedCards() { return redCards; }
    public void setRedCards(Integer redCards) { this.redCards = redCards; }

    public Integer getFouls() { return fouls; }
    public void setFouls(Integer fouls) { this.fouls = fouls; }

    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }
}
