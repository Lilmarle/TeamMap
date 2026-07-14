package com.marler.teammap.dto.request;

/**
 * 添加比赛请求
 */
public class AddMatchRequest {
    private Integer tournamentId;   // 关联赛事ID
    private Integer groupStageId;   // 小组赛ID（仅小组赛阶段使用）
    private Integer team1Id;        // 队伍1ID
    private Integer team2Id;        // 队伍2ID
    private Integer team1Score;     // 队伍1得分（默认0）
    private Integer team2Score;     // 队伍2得分（默认0）
    private Integer status;         // 状态：1-未开始，2-进行中，3-已结束，4-结算中（默认1）
    private Integer stage;          // 阶段：1-友谊赛，2-小组赛，3-附加赛...（默认0，未指定）
    private String matchTime;       // 比赛开始时间（可选）
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
