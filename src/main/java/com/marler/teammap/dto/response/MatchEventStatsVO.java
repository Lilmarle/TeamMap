package com.marler.teammap.dto.response;

/**
 * 比赛事件统计 VO（对应 v_match_event_stats 视图）
 */
public class MatchEventStatsVO {
    private Long matchId;               // 比赛ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍简称
    private Integer totalEvents;        // 事件总数
    // 足球统计
    private Integer footballGoals;      // 足球进球
    private Integer footballAssists;    // 足球助攻
    private Integer yellowCards;        // 黄牌
    private Integer redCards;           // 红牌
    private Integer footballFouls;      // 足球犯规
    private Integer footballSubstitutions; // 足球换人
    // 篮球统计
    private Integer twoPointers;        // 两分球
    private Integer threePointers;      // 三分球
    private Integer freeThrows;         // 罚球
    private Integer basketballAssists;  // 篮球助攻
    private Integer rebounds;           // 篮板
    private Integer steals;             // 抢断
    private Integer blocks;             // 盖帽
    private Integer basketballFouls;    // 篮球犯规
    private Integer turnovers;          // 失误
    private Integer basketballSubstitutions; // 篮球换人
    // 排球统计
    private Integer volleyballScores;   // 排球得分
    private Integer volleyballAssists;  // 排球助攻
    private Integer blocksVb;           // 拦网
    private Integer spikes;             // 扣球
    private Integer serveScores;        // 发球得分
    private Integer volleyballFouls;    // 排球犯规
    private Integer volleyballSubstitutions; // 排球换人

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public Integer getSportType() { return sportType; }
    public void setSportType(Integer sportType) { this.sportType = sportType; }

    public String getSportTypeName() { return sportTypeName; }
    public void setSportTypeName(String sportTypeName) { this.sportTypeName = sportTypeName; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Integer getTotalEvents() { return totalEvents; }
    public void setTotalEvents(Integer totalEvents) { this.totalEvents = totalEvents; }

    public Integer getFootballGoals() { return footballGoals; }
    public void setFootballGoals(Integer footballGoals) { this.footballGoals = footballGoals; }

    public Integer getFootballAssists() { return footballAssists; }
    public void setFootballAssists(Integer footballAssists) { this.footballAssists = footballAssists; }

    public Integer getYellowCards() { return yellowCards; }
    public void setYellowCards(Integer yellowCards) { this.yellowCards = yellowCards; }

    public Integer getRedCards() { return redCards; }
    public void setRedCards(Integer redCards) { this.redCards = redCards; }

    public Integer getFootballFouls() { return footballFouls; }
    public void setFootballFouls(Integer footballFouls) { this.footballFouls = footballFouls; }

    public Integer getFootballSubstitutions() { return footballSubstitutions; }
    public void setFootballSubstitutions(Integer footballSubstitutions) { this.footballSubstitutions = footballSubstitutions; }

    public Integer getTwoPointers() { return twoPointers; }
    public void setTwoPointers(Integer twoPointers) { this.twoPointers = twoPointers; }

    public Integer getThreePointers() { return threePointers; }
    public void setThreePointers(Integer threePointers) { this.threePointers = threePointers; }

    public Integer getFreeThrows() { return freeThrows; }
    public void setFreeThrows(Integer freeThrows) { this.freeThrows = freeThrows; }

    public Integer getBasketballAssists() { return basketballAssists; }
    public void setBasketballAssists(Integer basketballAssists) { this.basketballAssists = basketballAssists; }

    public Integer getRebounds() { return rebounds; }
    public void setRebounds(Integer rebounds) { this.rebounds = rebounds; }

    public Integer getSteals() { return steals; }
    public void setSteals(Integer steals) { this.steals = steals; }

    public Integer getBlocks() { return blocks; }
    public void setBlocks(Integer blocks) { this.blocks = blocks; }

    public Integer getBasketballFouls() { return basketballFouls; }
    public void setBasketballFouls(Integer basketballFouls) { this.basketballFouls = basketballFouls; }

    public Integer getTurnovers() { return turnovers; }
    public void setTurnovers(Integer turnovers) { this.turnovers = turnovers; }

    public Integer getBasketballSubstitutions() { return basketballSubstitutions; }
    public void setBasketballSubstitutions(Integer basketballSubstitutions) { this.basketballSubstitutions = basketballSubstitutions; }

    public Integer getVolleyballScores() { return volleyballScores; }
    public void setVolleyballScores(Integer volleyballScores) { this.volleyballScores = volleyballScores; }

    public Integer getVolleyballAssists() { return volleyballAssists; }
    public void setVolleyballAssists(Integer volleyballAssists) { this.volleyballAssists = volleyballAssists; }

    public Integer getBlocksVb() { return blocksVb; }
    public void setBlocksVb(Integer blocksVb) { this.blocksVb = blocksVb; }

    public Integer getSpikes() { return spikes; }
    public void setSpikes(Integer spikes) { this.spikes = spikes; }

    public Integer getServeScores() { return serveScores; }
    public void setServeScores(Integer serveScores) { this.serveScores = serveScores; }

    public Integer getVolleyballFouls() { return volleyballFouls; }
    public void setVolleyballFouls(Integer volleyballFouls) { this.volleyballFouls = volleyballFouls; }

    public Integer getVolleyballSubstitutions() { return volleyballSubstitutions; }
    public void setVolleyballSubstitutions(Integer volleyballSubstitutions) { this.volleyballSubstitutions = volleyballSubstitutions; }
}
