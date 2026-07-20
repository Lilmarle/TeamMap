package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 比赛统计总览 VO（对应 v_match_stats_overview 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
