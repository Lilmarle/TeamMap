package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 球员赛事统计 VO（对应 v_player_tournament_stats 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerTournamentStatsVO {
    private Integer playerId;           // 球员ID
    private String playerName;          // 球员名称（球衣名）
    private Integer jerseyNumber;       // 球衣号码
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍简称
    private Integer tournamentId;       // 赛事ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private Integer matchPlayed;        // 出场次数
    private Integer totalGoals;         // 总进球
    private Integer totalAssists;       // 总助攻
    private Integer totalYellowCards;   // 总黄牌
    private Integer totalRedCards;      // 总红牌
    private Integer totalFouls;         // 总犯规
    private Integer totalPlayTime;      // 总出场时间（分钟）
    private Integer avgPlayTime;        // 平均出场时间（分钟）
    private Integer totalScore;         // 综合评分
}
