package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 赛况详情 VO（对应 v_match_detail 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
