package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 小组详情 VO（包含小组信息 + 球队成绩）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupStageDetailVO {
    private Integer id;                 // 小组ID
    private String name;                // 小组名称
    private Integer teamCount;          // 球队总数
    private Integer directAdvance;      // 直接出线数
    private Integer indirectAdvance;    // 间接出线数
    private List<TeamScoreItem> teams;  // 球队成绩列表

    /**
     * 球队成绩项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamScoreItem {
        private Integer teamId;            // 队伍ID
        private String teamName;           // 队伍名称
        private String teamShortName;      // 队伍简称
        private String logo;               // 队伍Logo
        private Integer win;               // 胜场数
        private Integer draw;              // 平场数
        private Integer loss;              // 负场数
        private Integer points;            // 积分
        private Integer goalsFor;          // 进球数（得分）
        private Integer goalsAgainst;      // 失球数（失分）
        private Integer goalDifference;    // 净胜球
    }
}
