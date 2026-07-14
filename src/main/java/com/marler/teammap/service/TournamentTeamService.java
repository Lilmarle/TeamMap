package com.marler.teammap.service;

import com.marler.teammap.dto.response.TournamentTeamInfoVO;
import com.marler.teammap.dto.response.TournamentTeamSimpleVO;

import java.util.List;

public interface TournamentTeamService {
    /**
     * 球队申请加入赛事
     *
     * @param tournamentId 赛事ID
     * @param teamId       球队ID
     * @param userId       当前用户ID
     */
    void apply(Long tournamentId, Long teamId, Long userId);

    /**
     * 查询与赛事关联的球队列表
     *
     * @param tournamentId 赛事ID
     * @return 关联球队信息列表
     */
    List<TournamentTeamInfoVO> getTeamsByTournamentId(Long tournamentId);

    /**
     * 查询某赛事的球队（简化版，仅返回 teamId + teamName + teamLogo）
     *
     * @param tournamentId 赛事ID
     * @return 球队简化信息列表
     */
    List<TournamentTeamSimpleVO> getSimpleTeamsByTournamentId(Long tournamentId);

    /**
     * 审批球队申请
     *
     * @param relId   tournament_team 关联记录ID
     * @param status  目标状态：2-已通过，3-未通过
     * @param userId  当前用户ID
     * @param role    当前用户角色
     */
    void approve(Long relId, Integer status, Long userId, Integer role);

    /**
     * 主办方邀请单个球队加入赛事
     * 权限：role >= 3（赛事管理员可邀请自己创建的赛事，系统管理员可邀请所有）
     *
     * @param tournamentId 赛事ID
     * @param teamId       球队ID
     * @param userId       当前用户ID
     * @param role         当前用户角色
     */
    void invite(Long tournamentId, Long teamId, Long userId, Integer role);

    /**
     * 主办方批量邀请球队加入赛事
     * 权限：role >= 3（赛事管理员可邀请自己创建的赛事，系统管理员可邀请所有）
     *
     * @param tournamentId 赛事ID
     * @param teamIds      球队ID列表
     * @param userId       当前用户ID
     * @param role         当前用户角色
     * @return 邀请结果汇总信息
     */
    String inviteBatch(Long tournamentId, List<Long> teamIds, Long userId, Integer role);

    /**
     * 判断某球队是否已在某赛事中（状态为已通过）
     *
     * @param tournamentId 赛事ID
     * @param teamId       球队ID
     * @return true-已在赛事中，false-未在赛事中
     */
    boolean isTeamInTournament(Long tournamentId, Long teamId);

    /**
     * 主办方从赛事中移除球队（物理删除记录）
     * 权限：role >= 3（赛事管理员可操作自己创建的赛事，系统管理员可操作所有）
     *
     * @param tournamentId 赛事ID
     * @param teamId       球队ID
     * @param userId       当前用户ID
     * @param role         当前用户角色
     */
    void removeTeam(Long tournamentId, Long teamId, Long userId, Integer role);
}
