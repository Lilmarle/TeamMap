package com.marler.teammap.service;

import com.marler.teammap.dto.response.GroupStageDetailVO;
import com.marler.teammap.pojo.GroupStage;

import java.util.List;

public interface GroupStageService {

    /**
     * 添加单个小组
     */
    void add(GroupStage groupStage);

    /**
     * 批量添加小组
     */
    void addBatch(List<GroupStage> groupStages);

    GroupStage getById(Integer id);

    List<GroupStage> getByTournamentId(Integer tournamentId);

    /**
     * 查询小组详情（包含球队成绩）
     */
    GroupStageDetailVO getDetail(Integer id);
}
