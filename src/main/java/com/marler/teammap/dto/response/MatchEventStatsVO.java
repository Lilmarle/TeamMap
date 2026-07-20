package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 比赛事件统计 VO（对应 v_match_event_stats 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
