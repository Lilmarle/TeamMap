package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 比赛统计总览 VO（对应 v_match_stats_overview 视图）
 */
public class MatchStatsOverviewVO {
    private Long matchId;               // 比赛ID
    private String matchName;           // 比赛名称
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchTime;    // 比赛时间
    private String location;            // 比赛地点
    // 队伍1
    private String team1;               // 队伍1简称
    private Integer team1Score;         // 队伍1得分
    private Integer team1Goals;         // 队伍1进球
    private Integer team1GoalsAgainst;  // 队伍1失球
    private Integer team1Yellow;        // 队伍1黄牌
    private Integer team1Red;           // 队伍1红牌
    private Integer team1Fouls;         // 队伍1犯规
    private Integer team1Subs;          // 队伍1换人
    // 队伍2
    private String team2;               // 队伍2简称
    private Integer team2Score;         // 队伍2得分
    private Integer team2Goals;         // 队伍2进球
    private Integer team2GoalsAgainst;  // 队伍2失球
    private Integer team2Yellow;        // 队伍2黄牌
    private Integer team2Red;           // 队伍2红牌
    private Integer team2Fouls;         // 队伍2犯规
    private Integer team2Subs;          // 队伍2换人
    // 比赛信息
    private Integer status;             // 状态
    private Integer stage;              // 阶段
    private String stageName;           // 阶段名称
    // 统计汇总
    private Integer totalGoals;         // 总进球
    private Integer totalYellowCards;   // 总黄牌
    private Integer totalRedCards;      // 总红牌
    private Integer totalFouls;         // 总犯规
    private Integer totalSubstitutions; // 总换人

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getMatchName() { return matchName; }
    public void setMatchName(String matchName) { this.matchName = matchName; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public LocalDateTime getMatchTime() { return matchTime; }
    public void setMatchTime(LocalDateTime matchTime) { this.matchTime = matchTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTeam1() { return team1; }
    public void setTeam1(String team1) { this.team1 = team1; }

    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

    public Integer getTeam1Goals() { return team1Goals; }
    public void setTeam1Goals(Integer team1Goals) { this.team1Goals = team1Goals; }

    public Integer getTeam1GoalsAgainst() { return team1GoalsAgainst; }
    public void setTeam1GoalsAgainst(Integer team1GoalsAgainst) { this.team1GoalsAgainst = team1GoalsAgainst; }

    public Integer getTeam1Yellow() { return team1Yellow; }
    public void setTeam1Yellow(Integer team1Yellow) { this.team1Yellow = team1Yellow; }

    public Integer getTeam1Red() { return team1Red; }
    public void setTeam1Red(Integer team1Red) { this.team1Red = team1Red; }

    public Integer getTeam1Fouls() { return team1Fouls; }
    public void setTeam1Fouls(Integer team1Fouls) { this.team1Fouls = team1Fouls; }

    public Integer getTeam1Subs() { return team1Subs; }
    public void setTeam1Subs(Integer team1Subs) { this.team1Subs = team1Subs; }

    public String getTeam2() { return team2; }
    public void setTeam2(String team2) { this.team2 = team2; }

    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

    public Integer getTeam2Goals() { return team2Goals; }
    public void setTeam2Goals(Integer team2Goals) { this.team2Goals = team2Goals; }

    public Integer getTeam2GoalsAgainst() { return team2GoalsAgainst; }
    public void setTeam2GoalsAgainst(Integer team2GoalsAgainst) { this.team2GoalsAgainst = team2GoalsAgainst; }

    public Integer getTeam2Yellow() { return team2Yellow; }
    public void setTeam2Yellow(Integer team2Yellow) { this.team2Yellow = team2Yellow; }

    public Integer getTeam2Red() { return team2Red; }
    public void setTeam2Red(Integer team2Red) { this.team2Red = team2Red; }

    public Integer getTeam2Fouls() { return team2Fouls; }
    public void setTeam2Fouls(Integer team2Fouls) { this.team2Fouls = team2Fouls; }

    public Integer getTeam2Subs() { return team2Subs; }
    public void setTeam2Subs(Integer team2Subs) { this.team2Subs = team2Subs; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getStage() { return stage; }
    public void setStage(Integer stage) { this.stage = stage; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public Integer getTotalGoals() { return totalGoals; }
    public void setTotalGoals(Integer totalGoals) { this.totalGoals = totalGoals; }

    public Integer getTotalYellowCards() { return totalYellowCards; }
    public void setTotalYellowCards(Integer totalYellowCards) { this.totalYellowCards = totalYellowCards; }

    public Integer getTotalRedCards() { return totalRedCards; }
    public void setTotalRedCards(Integer totalRedCards) { this.totalRedCards = totalRedCards; }

    public Integer getTotalFouls() { return totalFouls; }
    public void setTotalFouls(Integer totalFouls) { this.totalFouls = totalFouls; }

    public Integer getTotalSubstitutions() { return totalSubstitutions; }
    public void setTotalSubstitutions(Integer totalSubstitutions) { this.totalSubstitutions = totalSubstitutions; }
}
