package com.marler.teammap.dto.response;

/**
 * 比赛详情 VO（含双方球队信息）
 */
public class MatchDetailVO {
    // ===== 比赛信息 =====
    private Long id;                 // 比赛ID
    private Integer tournamentId;    // 关联赛事ID
    private Integer groupStageId;    // 小组赛ID（仅小组赛阶段有效）
    private Integer team1Id;         // 队伍1ID
    private Integer team2Id;         // 队伍2ID
    private Integer team1Score;      // 队伍1得分
    private Integer team2Score;      // 队伍2得分
    private Integer status;          // 状态：1-未开始，2-进行中，3-已结束，4-结算中
    private Integer stage;           // 阶段
    private String matchTime;        // 比赛开始时间
    private String location;         // 比赛地点
    private String createTime;       // 创建时间
    private String updateTime;       // 修改时间

    // ===== 球队1信息 =====
    private String team1Name;        // 队伍1名称
    private String team1ShortName;   // 队伍1简称
    private String team1Logo;        // 队伍1Logo

    // ===== 球队2信息 =====
    private String team2Name;        // 队伍2名称
    private String team2ShortName;   // 队伍2简称
    private String team2Logo;        // 队伍2Logo

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTournamentId() { return tournamentId; }
    public void setTournamentId(Integer tournamentId) { this.tournamentId = tournamentId; }

    public Integer getGroupStageId() { return groupStageId; }
    public void setGroupStageId(Integer groupStageId) { this.groupStageId = groupStageId; }

    public Integer getTeam1Id() { return team1Id; }
    public void setTeam1Id(Integer team1Id) { this.team1Id = team1Id; }

    public Integer getTeam2Id() { return team2Id; }
    public void setTeam2Id(Integer team2Id) { this.team2Id = team2Id; }

    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getStage() { return stage; }
    public void setStage(Integer stage) { this.stage = stage; }

    public String getMatchTime() { return matchTime; }
    public void setMatchTime(String matchTime) { this.matchTime = matchTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public String getTeam1ShortName() { return team1ShortName; }
    public void setTeam1ShortName(String team1ShortName) { this.team1ShortName = team1ShortName; }

    public String getTeam1Logo() { return team1Logo; }
    public void setTeam1Logo(String team1Logo) { this.team1Logo = team1Logo; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public String getTeam2ShortName() { return team2ShortName; }
    public void setTeam2ShortName(String team2ShortName) { this.team2ShortName = team2ShortName; }

    public String getTeam2Logo() { return team2Logo; }
    public void setTeam2Logo(String team2Logo) { this.team2Logo = team2Logo; }
}
