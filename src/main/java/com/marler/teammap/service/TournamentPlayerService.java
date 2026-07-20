package com.marler.teammap.service;

import com.marler.teammap.dto.response.TournamentPlayerInfoVO;
import com.marler.teammap.pojo.TournamentPlayer;

import java.util.List;

public interface TournamentPlayerService {

    /**
     * 球队批量报名球员参赛（所有球员状态设为 0-审核中）
     *
     * @param tournamentId 赛事ID
     * @param teamId       球队ID
     * @param playerIds    球员ID列表
     * @param userId       操作人用户ID
     * @return 报名结果描述
     */
    String registerBatch(Long tournamentId, Long teamId, List<Long> playerIds, Long userId);

    /**
     * 球员单独报名参赛（状态设为 0-审核中）
     *
     * @param tournamentId 赛事ID
     * @param playerId     球员ID
     * @param userId       操作人用户ID
     * @return 创建的报名记录
     */
    TournamentPlayer register(Long tournamentId, Long playerId, Long userId);

    /**
     * 单独审批球员准入
     *
     * @param id       TournamentPlayer 记录ID
     * @param status   目标状态：1-通过（可出战），3-拒绝（退出）
     * @param userId   操作人用户ID
     * @param role     操作人角色
     */
    void approve(Long id, Integer status, Long userId, Integer role);

    /**
     * 查询赛事的所有球员报名信息
     *
     * @param tournamentId 赛事ID
     * @return 球员报名信息列表
     */
    List<TournamentPlayerInfoVO> getPlayersByTournamentId(Long tournamentId);

    /**
     * 按球队查询赛事的球员报名信息
     *
     * @param tournamentId 赛事ID
     * @param teamId       球队ID
     * @return 球员报名信息列表
     */
    List<TournamentPlayerInfoVO> getPlayersByTournamentIdAndTeamId(Long tournamentId, Long teamId);
}
