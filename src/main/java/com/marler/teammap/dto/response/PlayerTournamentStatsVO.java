package com.marler.teammap.dto.response;

/**
 * 球员赛事统计 VO（对应 v_player_tournament_stats 视图）
 */
public class PlayerTournamentStatsVO {
    private Integer playerId;           // 球员ID
    private String playerName;          // 球员名称（球衣名）
    private Integer jerseyNumber;       // 球衣号码
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍简称
    private Integer tournamentId;       // 赛事ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private Integer matchPlayed;        // 出场次数
    private Integer totalGoals;         // 总进球
    private Integer totalAssists;       // 总助攻
    private Integer totalYellowCards;   // 总黄牌
    private Integer totalRedCards;      // 总红牌
    private Integer totalFouls;         // 总犯规
    private Integer totalPlayTime;      // 总出场时间（分钟）
    private Integer avgPlayTime;        // 平均出场时间（分钟）
    private Integer totalScore;         // 综合评分

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

    public Integer getTournamentId() { return tournamentId; }
    public void setTournamentId(Integer tournamentId) { this.tournamentId = tournamentId; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public Integer getMatchPlayed() { return matchPlayed; }
    public void setMatchPlayed(Integer matchPlayed) { this.matchPlayed = matchPlayed; }

    public Integer getTotalGoals() { return totalGoals; }
    public void setTotalGoals(Integer totalGoals) { this.totalGoals = totalGoals; }

    public Integer getTotalAssists() { return totalAssists; }
    public void setTotalAssists(Integer totalAssists) { this.totalAssists = totalAssists; }

    public Integer getTotalYellowCards() { return totalYellowCards; }
    public void setTotalYellowCards(Integer totalYellowCards) { this.totalYellowCards = totalYellowCards; }

    public Integer getTotalRedCards() { return totalRedCards; }
    public void setTotalRedCards(Integer totalRedCards) { this.totalRedCards = totalRedCards; }

    public Integer getTotalFouls() { return totalFouls; }
    public void setTotalFouls(Integer totalFouls) { this.totalFouls = totalFouls; }

    public Integer getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(Integer totalPlayTime) { this.totalPlayTime = totalPlayTime; }

    public Integer getAvgPlayTime() { return avgPlayTime; }
    public void setAvgPlayTime(Integer avgPlayTime) { this.avgPlayTime = avgPlayTime; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
}
