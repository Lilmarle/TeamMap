package com.marler.teammap.dto.request;

import java.util.List;

/**
 * 添加单个小组请求
 */
public class AddGroupStageRequest {
    private Integer tournamentId;   // 赛事ID
    private String name;            // 小组名称（如：A组、B组）
    private Integer teamCount;      // 球队总数
    private Integer directAdvance;  // 直接出线数
    private Integer indirectAdvance;// 间接出线数
    private Integer roundType;      // 循环数：1-单循环，2-双循环
    private List<Integer> teamIds;  // 队伍ID列表

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(Integer teamCount) {
        this.teamCount = teamCount;
    }

    public Integer getDirectAdvance() {
        return directAdvance;
    }

    public void setDirectAdvance(Integer directAdvance) {
        this.directAdvance = directAdvance;
    }

    public Integer getIndirectAdvance() {
        return indirectAdvance;
    }

    public void setIndirectAdvance(Integer indirectAdvance) {
        this.indirectAdvance = indirectAdvance;
    }

    public Integer getRoundType() {
        return roundType;
    }

    public void setRoundType(Integer roundType) {
        this.roundType = roundType;
    }

    public List<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<Integer> teamIds) {
        this.teamIds = teamIds;
    }
}
