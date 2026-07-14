package com.marler.teammap.dto.request;

/**
 * 修改比赛信息请求
 */
public class UpdateMatchRequest {
    private Integer team1Score;     // 队伍1得分（可选）
    private Integer team2Score;     // 队伍2得分（可选）
    private Integer status;         // 状态：1-未开始，2-进行中，3-已结束，4-结算中（可选）
    private Integer stage;          // 阶段（可选）
    private Integer groupStageId;   // 小组赛ID（可选，仅小组赛阶段使用）
    private String matchTime;       // 比赛开始时间（可选）
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

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
