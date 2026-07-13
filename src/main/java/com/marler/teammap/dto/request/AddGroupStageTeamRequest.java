package com.marler.teammap.dto.request;

import java.util.List;

/**
 * 为小组添加一支球队请求
 */
public class AddGroupStageTeamRequest {
    private Integer groupStageId;   // 小组ID
    private Integer teamId;         // 队伍ID

    public Integer getGroupStageId() {
        return groupStageId;
    }

    public void setGroupStageId(Integer groupStageId) {
        this.groupStageId = groupStageId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
