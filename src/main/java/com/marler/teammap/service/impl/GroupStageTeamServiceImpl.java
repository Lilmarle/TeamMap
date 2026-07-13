package com.marler.teammap.service.impl;

import com.marler.teammap.dto.request.TournamentGroupTeamRequest;
import com.marler.teammap.mapper.GroupStageMapper;
import com.marler.teammap.mapper.GroupStageTeamMapper;
import com.marler.teammap.pojo.GroupStage;
import com.marler.teammap.pojo.GroupStageTeam;
import com.marler.teammap.service.GroupStageTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GroupStageTeamServiceImpl implements GroupStageTeamService {

    @Autowired
    private GroupStageTeamMapper groupStageTeamMapper;

    @Autowired
    private GroupStageMapper groupStageMapper;

    /**
     * 构建 GroupStageTeam 对象（初始成绩为0）
     */
    private GroupStageTeam buildGroupStageTeam(Integer groupStageId, Integer teamId) {
        GroupStageTeam gst = new GroupStageTeam();
        gst.setGroupStageId(groupStageId);
        gst.setTeamId(teamId);
        gst.setWin(0);
        gst.setDraw(0);
        gst.setLoss(0);
        gst.setPoints(0);
        gst.setGoalsFor(0);
        gst.setGoalsAgainst(0);
        gst.setGoalDifference(0);
        return gst;
    }

    @Override
    @Transactional
    public void addTeamToGroup(Integer groupStageId, Integer teamId) {
        GroupStageTeam gst = buildGroupStageTeam(groupStageId, teamId);
        groupStageTeamMapper.insert(gst);
        log.info("为小组[{}]添加球队[{}]成功", groupStageId, teamId);
    }

    @Override
    @Transactional
    public void addTeamsToGroup(Integer groupStageId, List<Integer> teamIds) {
        if (teamIds == null || teamIds.isEmpty()) {
            log.warn("批量添加球队到小组[{}]失败：队伍ID列表为空", groupStageId);
            return;
        }
        List<GroupStageTeam> list = new ArrayList<>();
        for (Integer teamId : teamIds) {
            list.add(buildGroupStageTeam(groupStageId, teamId));
        }
        groupStageTeamMapper.insertBatch(list);
        log.info("为小组[{}]批量添加 {} 支球队成功", groupStageId, teamIds.size());
    }

    @Override
    @Transactional
    public void addTeamsToTournamentGroups(Integer tournamentId,
                                            List<TournamentGroupTeamRequest.GroupAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            log.warn("为赛事[{}]添加球队失败：分配列表为空", tournamentId);
            return;
        }

        int totalTeams = 0;
        for (TournamentGroupTeamRequest.GroupAssignment assignment : assignments) {
            if (assignment.getTeamIds() == null || assignment.getTeamIds().isEmpty()) {
                continue;
            }
            List<GroupStageTeam> list = new ArrayList<>();
            for (Integer teamId : assignment.getTeamIds()) {
                list.add(buildGroupStageTeam(assignment.getGroupStageId(), teamId));
            }
            groupStageTeamMapper.insertBatch(list);
            totalTeams += assignment.getTeamIds().size();
            log.info("赛事[{}] - 小组[{}]已分配 {} 支球队",
                    tournamentId, assignment.getGroupStageId(), assignment.getTeamIds().size());
        }
        log.info("为赛事[{}]的所有小组添加球队完成，共分配 {} 支球队", tournamentId, totalTeams);
    }

    @Override
    public List<GroupStageTeam> getByGroupStageId(Integer groupStageId) {
        return groupStageTeamMapper.selectByGroupStageId(groupStageId);
    }
}
