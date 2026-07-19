package com.marler.teammap.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 修改比赛信息请求
 */
public class UpdateMatchRequest {
    private Integer team1Score;     // 队伍1得分（可选）
    private Integer team2Score;     // 队伍2得分（可选）
    private Integer sportType;      // 运动类型：1-足球，2-篮球，3-排球（可选）
    private String aggregateScore;  // 两回合总比分（如：3-2）（可选）
    private String extraTimeScore;  // 加时赛比分（如：1-0）（可选）
    private String penaltyScore;    // 点球比分（如：4-3）（可选）
    private Integer winnerId;       // 获胜队伍ID（可选）
    private String setScore;        // 排球局分（如：25-23,25-20,22-25,25-18）（可选）
    private Integer totalSets;      // 总局数（如：3-1表示打了4局）（可选）
    private Integer status;         // 状态：1-未开始，2-进行中，3-已结束，4-结算中（可选）
    private Integer stage;          // 阶段（可选）
    private Integer groupStageId;   // 小组赛ID（可选，仅小组赛阶段使用）
    private String name;            // 比赛名称（可选）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchTime;// 比赛开始时间（可选）
    private String location;        // 比赛地点（可选）

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
