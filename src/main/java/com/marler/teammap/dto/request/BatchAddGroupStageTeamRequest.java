package com.marler.teammap.dto.request;

import java.util.List;

/**
 * 为一个小组批量添加多支球队请求
 */
public class BatchAddGroupStageTeamRequest {
    private Integer groupStageId;       // 小组ID
    private List<Integer> teamIds;      // 队伍ID列表

    public Integer getGroupStageId() {
        return groupStageId;
    }

    public void setGroupStageId(Integer groupStageId) {
        this.groupStageId = groupStageId;
    }

    public List<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<Integer> teamIds) {
        this.teamIds = teamIds;
    }
}
