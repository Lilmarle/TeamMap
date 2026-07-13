package com.marler.teammap.service;

import com.marler.teammap.dto.request.TournamentGroupTeamRequest;
import com.marler.teammap.pojo.GroupStageTeam;

import java.util.List;

public interface GroupStageTeamService {

    /**
     * 为一个小组添加一支球队
     */
    void addTeamToGroup(Integer groupStageId, Integer teamId);

    /**
     * 为一个小组批量添加多支球队
     */
    void addTeamsToGroup(Integer groupStageId, List<Integer> teamIds);

    /**
     * 为整个赛事的所有小组添加球队
     */
    void addTeamsToTournamentGroups(Integer tournamentId,
                                    List<TournamentGroupTeamRequest.GroupAssignment> assignments);

    /**
     * 查询某小组的所有球队
     */
    List<GroupStageTeam> getByGroupStageId(Integer groupStageId);
}
