package com.marler.teammap.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 添加比赛请求
 */
public class AddMatchRequest {
    private Integer tournamentId;   // 关联赛事ID
    private Integer sportType;      // 运动类型：1-足球，2-篮球，3-排球
    private Integer groupStageId;   // 小组赛ID（仅小组赛阶段使用）
    private String name;            // 比赛名称（如：A组第1轮 文学院 vs 外国语学院）
    private Integer team1Id;        // 队伍1ID
    private Integer team2Id;        // 队伍2ID
    private Integer team1Score;     // 队伍1得分（默认0）
    private Integer team2Score;     // 队伍2得分（默认0）
    private String aggregateScore;  // 两回合总比分（如：3-2）
    private String extraTimeScore;  // 加时赛比分（如：1-0）
    private String penaltyScore;    // 点球比分（如：4-3）
    private Integer winnerId;       // 获胜队伍ID
    private String setScore;        // 排球局分（如：25-23,25-20,22-25,25-18）
    private Integer totalSets;      // 总局数（如：3-1表示打了4局）
    private Integer status;         // 状态：1-未开始，2-进行中，3-已结束，4-结算中（默认1）
    private Integer stage;          // 阶段：1-友谊赛，2-小组赛，3-附加赛...（默认0，未指定）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchTime;// 比赛开始时间（可选）
    private String location;        // 比赛地点（可选）

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getGroupStageId() {
        return groupStageId;
    }

    public void setGroupStageId(Integer groupStageId) {
        this.groupStageId = groupStageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(Integer team1Id) {
        this.team1Id = team1Id;
    }

    public Integer getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(Integer team2Id) {
        this.team2Id = team2Id;
    }

    public Integer getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(Integer team1Score) {
        this.team1Score = team1Score;
    }

    public Integer getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(Integer team2Score) {
        this.team2Score = team2Score;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(LocalDateTime matchTime) {
        this.matchTime = matchTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getSportType() {
        return sportType;
    }

    public void setSportType(Integer sportType) {
        this.sportType = sportType;
    }

    public String getAggregateScore() {
        return aggregateScore;
    }

    public void setAggregateScore(String aggregateScore) {
        this.aggregateScore = aggregateScore;
    }

    public String getExtraTimeScore() {
        return extraTimeScore;
    }

    public void setExtraTimeScore(String extraTimeScore) {
        this.extraTimeScore = extraTimeScore;
    }

    public String getPenaltyScore() {
        return penaltyScore;
    }

    public void setPenaltyScore(String penaltyScore) {
        this.penaltyScore = penaltyScore;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public String getSetScore() {
        return setScore;
    }

    public void setSetScore(String setScore) {
        this.setScore = setScore;
    }

    public Integer getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(Integer totalSets) {
        this.totalSets = totalSets;
    }
}
