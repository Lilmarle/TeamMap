package com.marler.teammap.dto.request;

import java.util.List;

/**
 * 为整个赛事的所有小组添加球队请求
 */
public class TournamentGroupTeamRequest {
    private Integer tournamentId;                   // 赛事ID
    private List<GroupAssignment> assignments;      // 小组分配列表

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public List<GroupAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<GroupAssignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * 单个小组的队伍分配
     */
    public static class GroupAssignment {
        private Integer groupStageId;       // 小组ID
        private List<Integer> teamIds;      // 分配给该小组的队伍ID列表

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
}
