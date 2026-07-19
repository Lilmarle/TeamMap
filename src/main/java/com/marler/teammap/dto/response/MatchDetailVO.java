package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 赛况详情 VO（对应 v_match_detail 视图）
 */
public class MatchDetailVO {
    // ===== 比赛信息 =====
    private Long id;                 // 比赛ID
    private String name;             // 比赛名称
    private Integer tournamentId;    // 关联赛事ID
    private String tournamentName;   // 赛事名称
    private Integer sportType;       // 运动类型：1-足球，2-篮球，3-排球
    private String sportTypeName;    // 运动类型名称
    private Integer groupStageId;    // 小组赛ID
    private String groupStageName;   // 小组赛名称
    private Integer stage;           // 阶段
    private String stageName;        // 阶段名称
    private Integer status;          // 状态：1-未开始，2-进行中，3-已结束，4-结算中
    private String statusName;       // 状态名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchTime; // 比赛开始时间
    private String location;         // 比赛地点
    // 淘汰赛扩展字段
    private String aggregateScore;   // 两回合总比分（如：3-2）
    private String extraTimeScore;   // 加时赛比分（如：1-0）
    private String penaltyScore;     // 点球比分（如：4-3）
    private Integer winnerId;        // 获胜队伍ID
    // 排球扩展字段
    private String setScore;         // 排球局分
    private Integer totalSets;       // 总局数

    // ===== 队伍1信息 =====
    private Integer team1Id;         // 队伍1ID
    private String team1Name;        // 队伍1名称
    private String team1ShortName;   // 队伍1简称
    private String team1Logo;        // 队伍1Logo
    private Integer team1Score;      // 队伍1得分
    private Integer team1Goals;      // 队伍1进球数
    private Integer team1GoalsAgainst; // 队伍1失球数
    private Integer team1YellowCards;  // 队伍1黄牌数
    private Integer team1RedCards;     // 队伍1红牌数
    private Integer team1Fouls;        // 队伍1犯规数
    private Integer team1Substitutions; // 队伍1换人数

    // ===== 队伍2信息 =====
    private Integer team2Id;         // 队伍2ID
    private String team2Name;        // 队伍2名称
    private String team2ShortName;   // 队伍2简称
    private String team2Logo;        // 队伍2Logo
    private Integer team2Score;      // 队伍2得分
    private Integer team2Goals;      // 队伍2进球数
    private Integer team2GoalsAgainst; // 队伍2失球数
    private Integer team2YellowCards;  // 队伍2黄牌数
    private Integer team2RedCards;     // 队伍2红牌数
    private Integer team2Fouls;        // 队伍2犯规数
    private Integer team2Substitutions; // 队伍2换人数

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;       // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;       // 修改时间

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTournamentId() { return tournamentId; }
    public void setTournamentId(Integer tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public Integer getGroupStageId() { return groupStageId; }
    public void setGroupStageId(Integer groupStageId) { this.groupStageId = groupStageId; }

    public String getGroupStageName() { return groupStageName; }
    public void setGroupStageName(String groupStageName) { this.groupStageName = groupStageName; }

    public Integer getStage() { return stage; }
    public void setStage(Integer stage) { this.stage = stage; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public LocalDateTime getMatchTime() { return matchTime; }
    public void setMatchTime(LocalDateTime matchTime) { this.matchTime = matchTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAggregateScore() { return aggregateScore; }
    public void setAggregateScore(String aggregateScore) { this.aggregateScore = aggregateScore; }

    public String getExtraTimeScore() { return extraTimeScore; }
    public void setExtraTimeScore(String extraTimeScore) { this.extraTimeScore = extraTimeScore; }

    public String getPenaltyScore() { return penaltyScore; }
    public void setPenaltyScore(String penaltyScore) { this.penaltyScore = penaltyScore; }

    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }

    public String getSetScore() { return setScore; }
    public void setSetScore(String setScore) { this.setScore = setScore; }

    public Integer getTotalSets() { return totalSets; }
    public void setTotalSets(Integer totalSets) { this.totalSets = totalSets; }

    public Integer getTeam1Id() { return team1Id; }
    public void setTeam1Id(Integer team1Id) { this.team1Id = team1Id; }

    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public String getTeam1ShortName() { return team1ShortName; }
    public void setTeam1ShortName(String team1ShortName) { this.team1ShortName = team1ShortName; }

    public String getTeam1Logo() { return team1Logo; }
    public void setTeam1Logo(String team1Logo) { this.team1Logo = team1Logo; }

    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

    public Integer getTeam1Goals() { return team1Goals; }
    public void setTeam1Goals(Integer team1Goals) { this.team1Goals = team1Goals; }

    public Integer getTeam1GoalsAgainst() { return team1GoalsAgainst; }
    public void setTeam1GoalsAgainst(Integer team1GoalsAgainst) { this.team1GoalsAgainst = team1GoalsAgainst; }

    public Integer getTeam1YellowCards() { return team1YellowCards; }
    public void setTeam1YellowCards(Integer team1YellowCards) { this.team1YellowCards = team1YellowCards; }

    public Integer getTeam1RedCards() { return team1RedCards; }
    public void setTeam1RedCards(Integer team1RedCards) { this.team1RedCards = team1RedCards; }

    public Integer getTeam1Fouls() { return team1Fouls; }
    public void setTeam1Fouls(Integer team1Fouls) { this.team1Fouls = team1Fouls; }

    public Integer getTeam1Substitutions() { return team1Substitutions; }
    public void setTeam1Substitutions(Integer team1Substitutions) { this.team1Substitutions = team1Substitutions; }

    public Integer getTeam2Id() { return team2Id; }
    public void setTeam2Id(Integer team2Id) { this.team2Id = team2Id; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public String getTeam2ShortName() { return team2ShortName; }
    public void setTeam2ShortName(String team2ShortName) { this.team2ShortName = team2ShortName; }

    public String getTeam2Logo() { return team2Logo; }
    public void setTeam2Logo(String team2Logo) { this.team2Logo = team2Logo; }

    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

    public Integer getTeam2Goals() { return team2Goals; }
    public void setTeam2Goals(Integer team2Goals) { this.team2Goals = team2Goals; }

    public Integer getTeam2GoalsAgainst() { return team2GoalsAgainst; }
    public void setTeam2GoalsAgainst(Integer team2GoalsAgainst) { this.team2GoalsAgainst = team2GoalsAgainst; }

    public Integer getTeam2YellowCards() { return team2YellowCards; }
    public void setTeam2YellowCards(Integer team2YellowCards) { this.team2YellowCards = team2YellowCards; }

    public Integer getTeam2RedCards() { return team2RedCards; }
    public void setTeam2RedCards(Integer team2RedCards) { this.team2RedCards = team2RedCards; }

    public Integer getTeam2Fouls() { return team2Fouls; }
    public void setTeam2Fouls(Integer team2Fouls) { this.team2Fouls = team2Fouls; }

    public Integer getTeam2Substitutions() { return team2Substitutions; }
    public void setTeam2Substitutions(Integer team2Substitutions) { this.team2Substitutions = team2Substitutions; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
