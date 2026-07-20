package com.marler.teammap.service.impl;

import com.marler.teammap.dto.request.AddMatchEventRequest;
import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.dto.response.MatchEventVO;
import com.marler.teammap.mapper.MatchEventMapper;
import com.marler.teammap.mapper.MatchMapper;
import com.marler.teammap.pojo.Match;
import com.marler.teammap.pojo.MatchEvent;
import com.marler.teammap.pojo.Player;
import com.marler.teammap.service.MatchEventService;
import com.marler.teammap.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MatchEventServiceImpl implements MatchEventService {

    @Autowired
    private MatchEventMapper matchEventMapper;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private PlayerService playerService;

    @Override
    @Transactional
    public MatchEvent add(AddMatchEventRequest request) {
        // ========== 1. 入参基础校验 ==========
        validateRequired(request);

        // ========== 2. 校验比赛是否存在 ==========
        Match match = matchMapper.selectById(request.getMatchId().longValue());
        if (match == null) {
            log.warn("添加比赛事件失败：比赛不存在 - matchId: {}", request.getMatchId());
            throw new RuntimeException("比赛不存在，matchId: " + request.getMatchId());
        }

        // ========== 3. 校验队伍是否参与该场比赛 ==========
        if (!request.getTeamId().equals(match.getTeam1Id()) && !request.getTeamId().equals(match.getTeam2Id())) {
            log.warn("添加比赛事件失败：队伍未参与该场比赛 - matchId: {}, teamId: {}",
                    request.getMatchId(), request.getTeamId());
            throw new RuntimeException("队伍未参与该场比赛，teamId: " + request.getTeamId());
        }

        // ========== 4. 校验球员存在性 ==========
        if (request.getPlayerId() != null) {
            Player player = playerService.getById(request.getPlayerId());
            if (player == null) {
                log.warn("添加比赛事件失败：球员不存在 - playerId: {}", request.getPlayerId());
                throw new RuntimeException("球员不存在，playerId: " + request.getPlayerId());
            }
        }

        if (request.getRelatedPlayerId() != null) {
            Player relatedPlayer = playerService.getById(request.getRelatedPlayerId());
            if (relatedPlayer == null) {
                log.warn("添加比赛事件失败：关联球员不存在 - relatedPlayerId: {}", request.getRelatedPlayerId());
                throw new RuntimeException("关联球员不存在，relatedPlayerId: " + request.getRelatedPlayerId());
            }
        }

        // ========== 5. 事件类型专项校验 ==========
        validateEventType(request);

        // ========== 6. 构建事件对象并插入 ==========
        MatchEvent event = new MatchEvent();
        event.setMatchId(request.getMatchId());
        event.setSportType(request.getSportType());
        event.setTeamId(request.getTeamId());
        event.setPlayerId(request.getPlayerId());
        event.setRelatedPlayerId(request.getRelatedPlayerId());
        event.setType(request.getType());
        event.setDescription(request.getDescription());
        event.setEventTime(request.getEventTime());
        event.setExtraInfo(request.getExtraInfo());

        matchEventMapper.insert(event);
        log.info("添加比赛事件成功 - id: {}, matchId: {}, type: {}, eventTime: {}分钟",
                event.getId(), event.getMatchId(), event.getType(), event.getEventTime());
        return event;
    }

    /**
     * 校验必填参数
     */
    private void validateRequired(AddMatchEventRequest request) {
        if (request.getMatchId() == null) {
            throw new RuntimeException("比赛ID不能为空");
        }
        if (request.getSportType() == null) {
            throw new RuntimeException("运动类型不能为空");
        }
        if (request.getTeamId() == null) {
            throw new RuntimeException("队伍ID不能为空");
        }
        if (request.getType() == null) {
            throw new RuntimeException("事件类型不能为空");
        }
        if (request.getEventTime() == null) {
            throw new RuntimeException("事件发生时间不能为空");
        }

        // 校验运动类型范围
        if (request.getSportType() < 1 || request.getSportType() > 3) {
            throw new RuntimeException("无效的运动类型: " + request.getSportType() + "（1-足球，2-篮球，3-排球）");
        }

        // 校验事件类型范围
        if (request.getType() < 1 || request.getType() > 5) {
            throw new RuntimeException("无效的事件类型: " + request.getType() + "（1-进球，2-红牌，3-黄牌，4-犯规，5-换人）");
        }

        // 校验事件时间范围（通常比赛不超过120分钟，含加时）
        if (request.getEventTime() < 0 || request.getEventTime() > 120) {
            throw new RuntimeException("事件发生时间无效（需在0-120分钟之间）");
        }
    }

    /**
     * 事件类型专项校验
     */
    private void validateEventType(AddMatchEventRequest request) {
        switch (request.getType()) {
            case 1: // 进球
                if (request.getPlayerId() == null) {
                    throw new RuntimeException("进球事件必须指定进球球员( playerId )");
                }
                break;
            case 2: // 红牌
                if (request.getPlayerId() == null) {
                    throw new RuntimeException("红牌事件必须指定吃牌球员( playerId )");
                }
                break;
            case 3: // 黄牌
                if (request.getPlayerId() == null) {
                    throw new RuntimeException("黄牌事件必须指定吃牌球员( playerId )");
                }
                break;
            case 4: // 犯规
                if (request.getPlayerId() == null) {
                    throw new RuntimeException("犯规事件必须指定犯规球员( playerId )");
                }
                break;
            case 5: // 换人
                if (request.getPlayerId() == null) {
                    throw new RuntimeException("换人事件必须指定换上球员( playerId )");
                }
                if (request.getRelatedPlayerId() == null) {
                    throw new RuntimeException("换人事件必须指定被换下球员( relatedPlayerId )");
                }
                break;
            default:
                throw new RuntimeException("未知的事件类型: " + request.getType());
        }
    }

    @Override
    public List<MatchEvent> getByMatchId(Integer matchId) {
        return matchEventMapper.selectByMatchId(matchId);
    }

    @Override
    public List<MatchEventVO> getEventVOByMatchId(Integer matchId) {
        return matchEventMapper.selectEventVOByMatchId(matchId);
    }

    @Override
    public List<MatchEventStatsVO> getEventStatsByMatchId(Integer matchId) {
        return matchEventMapper.selectEventStatsByMatchId(matchId);
    }
}
