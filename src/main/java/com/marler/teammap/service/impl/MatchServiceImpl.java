package com.marler.teammap.service.impl;

import com.marler.teammap.dto.request.AddMatchRequest;
import com.marler.teammap.dto.request.UpdateMatchRequest;
import com.marler.teammap.dto.response.MatchDetailVO;
import com.marler.teammap.mapper.GroupStageMapper;
import com.marler.teammap.mapper.MatchMapper;
import com.marler.teammap.pojo.GroupStage;
import com.marler.teammap.pojo.Match;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.service.MatchService;
import com.marler.teammap.service.TeamService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private GroupStageMapper groupStageMapper;

    @Override
    @Transactional
    public Match add(AddMatchRequest request) {
        // 1. 构建 Match 对象，设置默认值
        Match match = new Match();
        match.setTournamentId(request.getTournamentId());
        match.setTeam1Id(request.getTeam1Id());
        match.setTeam2Id(request.getTeam2Id());

        // 队伍得分默认0
        match.setTeam1Score(request.getTeam1Score() != null ? request.getTeam1Score() : 0);
        match.setTeam2Score(request.getTeam2Score() != null ? request.getTeam2Score() : 0);

        // 状态默认1（未开始）
        match.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        // 阶段没填默认0（未指定）
        match.setStage(request.getStage() != null ? request.getStage() : 0);

        // 小组赛ID（仅小组赛阶段使用）
        match.setGroupStageId(request.getGroupStageId());

        // 比赛名称：如果未提供则自动生成
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            match.setName(request.getName());
        } else {
            match.setName(generateMatchName(request.getStage(), request.getGroupStageId()));
        }

        // 比赛时间、地点不填默认为空
        match.setMatchTime(request.getMatchTime());
        match.setLocation(request.getLocation());

        // 2. 插入数据库
        matchMapper.insert(match);
        log.info("添加比赛成功 - id: {}, tournamentId: {}, team1Id: {}, team2Id: {}",
                match.getId(), match.getTournamentId(), match.getTeam1Id(), match.getTeam2Id());

        return match;
    }

    @Override
    @Transactional
    public Match update(Long id, UpdateMatchRequest request) {
        // 1. 检查比赛是否存在
        Match existing = matchMapper.selectById(id);
        if (existing == null) {
            log.warn("修改比赛失败：比赛不存在 - id: {}", id);
            throw new RuntimeException("比赛不存在");
        }

        // 2. 只更新请求中传入的字段（非 null 的字段）
        if (request.getTeam1Score() != null) {
            existing.setTeam1Score(request.getTeam1Score());
        }
        if (request.getTeam2Score() != null) {
            existing.setTeam2Score(request.getTeam2Score());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getStage() != null) {
            existing.setStage(request.getStage());
        }
        if (request.getGroupStageId() != null) {
            existing.setGroupStageId(request.getGroupStageId());
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getMatchTime() != null) {
            existing.setMatchTime(request.getMatchTime());
        }
        if (request.getLocation() != null) {
            existing.setLocation(request.getLocation());
        }

        // 3. 执行更新
        matchMapper.updateById(existing);
        log.info("修改比赛成功 - id: {}, status: {}, team1Score: {}, team2Score: {}",
                id, existing.getStatus(), existing.getTeam1Score(), existing.getTeam2Score());

        return existing;
    }

    @Override
    public MatchDetailVO getDetail(Long id) {
        // 1. 查询比赛
        Match match = matchMapper.selectById(id);
        if (match == null) {
            log.warn("查询比赛详情失败：比赛不存在 - id: {}", id);
            return null;
        }

        // 2. 查询双方球队信息
        Team team1 = teamService.getById(match.getTeam1Id().longValue());
        Team team2 = teamService.getById(match.getTeam2Id().longValue());

        // 3. 构建 VO
        MatchDetailVO vo = new MatchDetailVO();
        vo.setId(match.getId());
        vo.setTournamentId(match.getTournamentId());
        vo.setGroupStageId(match.getGroupStageId());
        vo.setName(match.getName());
        vo.setTeam1Id(match.getTeam1Id());
        vo.setTeam2Id(match.getTeam2Id());
        vo.setTeam1Score(match.getTeam1Score());
        vo.setTeam2Score(match.getTeam2Score());
        vo.setStatus(match.getStatus());
        vo.setStage(match.getStage());
        vo.setMatchTime(match.getMatchTime());
        vo.setLocation(match.getLocation());
        vo.setCreateTime(match.getCreateTime());
        vo.setUpdateTime(match.getUpdateTime());

        if (team1 != null) {
            vo.setTeam1Name(team1.getName());
            vo.setTeam1ShortName(team1.getShortName());
            vo.setTeam1Logo(team1.getLogo());
        }
        if (team2 != null) {
            vo.setTeam2Name(team2.getName());
            vo.setTeam2ShortName(team2.getShortName());
            vo.setTeam2Logo(team2.getLogo());
        }

        log.info("查询比赛详情成功 - id: {}, team1: {}, team2: {}",
                id, team1 != null ? team1.getName() : "null", team2 != null ? team2.getName() : "null");
        return vo;
    }

    @Override
    public List<MatchDetailVO> listByTournament(Integer tournamentId) {
        // 1. 查询该赛事的所有比赛
        List<Match> matches = matchMapper.selectByTournamentId(tournamentId);
        if (matches == null || matches.isEmpty()) {
            log.info("查询赛事[{}]的比赛列表为空", tournamentId);
            return Collections.emptyList();
        }

        // 2. 为每场比赛组装详情 VO
        List<MatchDetailVO> result = new ArrayList<>();
        for (Match match : matches) {
            MatchDetailVO vo = new MatchDetailVO();
            vo.setId(match.getId());
            vo.setTournamentId(match.getTournamentId());
            vo.setGroupStageId(match.getGroupStageId());
            vo.setName(match.getName());
            vo.setTeam1Id(match.getTeam1Id());
            vo.setTeam2Id(match.getTeam2Id());
            vo.setTeam1Score(match.getTeam1Score());
            vo.setTeam2Score(match.getTeam2Score());
            vo.setStatus(match.getStatus());
            vo.setStage(match.getStage());
            vo.setMatchTime(match.getMatchTime());
            vo.setLocation(match.getLocation());
            vo.setCreateTime(match.getCreateTime());
            vo.setUpdateTime(match.getUpdateTime());

            // 联查球队信息
            Team team1 = teamService.getById(match.getTeam1Id().longValue());
            Team team2 = teamService.getById(match.getTeam2Id().longValue());
            if (team1 != null) {
                vo.setTeam1Name(team1.getName());
                vo.setTeam1ShortName(team1.getShortName());
                vo.setTeam1Logo(team1.getLogo());
            }
            if (team2 != null) {
                vo.setTeam2Name(team2.getName());
                vo.setTeam2ShortName(team2.getShortName());
                vo.setTeam2Logo(team2.getLogo());
            }

            result.add(vo);
        }

        log.info("查询赛事[{}]的比赛列表成功，共 {} 场比赛", tournamentId, result.size());
        return result;
    }

    @Override
    public List<MatchDetailVO> listByTeam(Integer teamId) {
        // 1. 查询该球队参与的所有比赛
        List<Match> matches = matchMapper.selectByTeamId(teamId);
        if (matches == null || matches.isEmpty()) {
            log.info("查询球队[{}]的比赛列表为空", teamId);
            return Collections.emptyList();
        }

        // 2. 为每场比赛组装详情 VO
        List<MatchDetailVO> result = new ArrayList<>();
        for (Match match : matches) {
            MatchDetailVO vo = new MatchDetailVO();
            vo.setId(match.getId());
            vo.setTournamentId(match.getTournamentId());
            vo.setGroupStageId(match.getGroupStageId());
            vo.setName(match.getName());
            vo.setTeam1Id(match.getTeam1Id());
            vo.setTeam2Id(match.getTeam2Id());
            vo.setTeam1Score(match.getTeam1Score());
            vo.setTeam2Score(match.getTeam2Score());
            vo.setStatus(match.getStatus());
            vo.setStage(match.getStage());
            vo.setMatchTime(match.getMatchTime());
            vo.setLocation(match.getLocation());
            vo.setCreateTime(match.getCreateTime());
            vo.setUpdateTime(match.getUpdateTime());

            // 联查球队信息
            Team team1 = teamService.getById(match.getTeam1Id().longValue());
            Team team2 = teamService.getById(match.getTeam2Id().longValue());
            if (team1 != null) {
                vo.setTeam1Name(team1.getName());
                vo.setTeam1ShortName(team1.getShortName());
                vo.setTeam1Logo(team1.getLogo());
            }
            if (team2 != null) {
                vo.setTeam2Name(team2.getName());
                vo.setTeam2ShortName(team2.getShortName());
                vo.setTeam2Logo(team2.getLogo());
            }

            result.add(vo);
        }

        log.info("查询球队[{}]的比赛列表成功，共 {} 场比赛", teamId, result.size());
        return result;
    }

    @Override
    public List<MatchDetailVO> listByGroupStage(Integer groupStageId) {
        List<Match> matches = matchMapper.selectByGroupStageId(groupStageId);
        if (matches == null || matches.isEmpty()) {
            log.info("查询小组[{}]的比赛列表为空", groupStageId);
            return Collections.emptyList();
        }

        List<MatchDetailVO> result = new ArrayList<>();
        for (Match match : matches) {
            MatchDetailVO vo = buildMatchDetailVO(match);
            if (vo != null) {
                result.add(vo);
            }
        }

        log.info("查询小组[{}]的比赛列表成功，共 {} 场比赛", groupStageId, result.size());
        return result;
    }

    /**
     * 构建 MatchDetailVO（含球队信息）
     */
    /**
     * 根据阶段和小组信息自动生成比赛名称
     */
    private String generateMatchName(Integer stage, Integer groupStageId) {
        if (stage == null) return null;

        if (stage == 2) {
            // 小组赛：组名 + 小组赛
            if (groupStageId != null) {
                GroupStage groupStage = groupStageMapper.selectById(groupStageId);
                if (groupStage != null && groupStage.getName() != null) {
                    return groupStage.getName() + " 小组赛";
                }
            }
            return "小组赛";
        } else if (stage >= 4) {
            return getStageName(stage);
        }
        return null;
    }

    /**
     * 获取阶段名称
     */
    private String getStageName(int stage) {
        switch (stage) {
            case 4: return "八分之一决赛";
            case 5: return "四分之一决赛";
            case 6: return "半决赛";
            case 7: return "三四名决赛";
            case 8: return "决赛";
            default: return null;
        }
    }

    private MatchDetailVO buildMatchDetailVO(Match match) {
        MatchDetailVO vo = new MatchDetailVO();
        vo.setId(match.getId());
        vo.setTournamentId(match.getTournamentId());
        vo.setGroupStageId(match.getGroupStageId());
        vo.setName(match.getName());
        vo.setTeam1Id(match.getTeam1Id());
        vo.setTeam2Id(match.getTeam2Id());
        vo.setTeam1Score(match.getTeam1Score());
        vo.setTeam2Score(match.getTeam2Score());
        vo.setStatus(match.getStatus());
        vo.setStage(match.getStage());
        vo.setMatchTime(match.getMatchTime());
        vo.setLocation(match.getLocation());
        vo.setCreateTime(match.getCreateTime());
        vo.setUpdateTime(match.getUpdateTime());

        Team team1 = teamService.getById(match.getTeam1Id().longValue());
        Team team2 = teamService.getById(match.getTeam2Id().longValue());
        if (team1 != null) {
            vo.setTeam1Name(team1.getName());
            vo.setTeam1ShortName(team1.getShortName());
            vo.setTeam1Logo(team1.getLogo());
        }
        if (team2 != null) {
            vo.setTeam2Name(team2.getName());
            vo.setTeam2ShortName(team2.getShortName());
            vo.setTeam2Logo(team2.getLogo());
        }
        return vo;
    }
}
