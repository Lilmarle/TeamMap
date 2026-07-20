package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 比赛球员表现 VO（对应 v_match_player_performance 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPlayerPerformanceVO {
    private Long id;                    // 关联ID
    private Long matchId;              // 比赛ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private String matchName;           // 比赛名称
    private Integer playerId;           // 球员ID
    private String playerName;          // 球员名称（球衣名）
    private Integer jerseyNumber;       // 球衣号码
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍名称（简称）
    private Integer status;             // 出场状态：0-未出场，1-首发，2-替补
    private String statusName;          // 出场状态名称
    private Integer playTime;           // 出场时间（分钟）
    private String playTimeDisplay;     // 出场时间显示
    private Integer goals;              // 进球数
    private Integer assists;            // 助攻数
    private Integer yellowCards;        // 黄牌数
    private Integer redCards;           // 红牌数
    private Integer fouls;              // 犯规数
    private Double performanceScore;    // 综合评分
}
