package com.marler.teammap.service;

import com.marler.teammap.dto.request.AddMatchRequest;
import com.marler.teammap.dto.request.UpdateMatchRequest;
import com.marler.teammap.dto.response.MatchDetailVO;
import com.marler.teammap.pojo.Match;

import java.util.List;

public interface MatchService {

    /**
     * 添加一场比赛
     */
    Match add(AddMatchRequest request);

    /**
     * 修改比赛信息
     */
    Match update(Long id, UpdateMatchRequest request);

    /**
     * 查询比赛详情（含双方球队信息）
     */
    MatchDetailVO getDetail(Long id);

    /**
     * 查询某赛事的所有比赛（含双方球队信息）
     */
    List<MatchDetailVO> listByTournament(Integer tournamentId);

    /**
     * 查询某只球队参与的所有比赛（含双方球队信息）
     */
    List<MatchDetailVO> listByTeam(Integer teamId);

    /**
     * 查询某小组的所有比赛（含双方球队信息）
     */
    List<MatchDetailVO> listByGroupStage(Integer groupStageId);
}
