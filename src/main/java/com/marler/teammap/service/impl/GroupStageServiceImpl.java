package com.marler.teammap.service.impl;

import com.marler.teammap.dto.response.GroupStageDetailVO;
import com.marler.teammap.mapper.GroupStageMapper;
import com.marler.teammap.mapper.GroupStageTeamMapper;
import com.marler.teammap.mapper.TeamMapper;
import com.marler.teammap.pojo.GroupStage;
import com.marler.teammap.pojo.GroupStageTeam;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.service.GroupStageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GroupStageServiceImpl implements GroupStageService {

    @Autowired
    private GroupStageMapper groupStageMapper;

    @Autowired
    private GroupStageTeamMapper groupStageTeamMapper;

    @Autowired
    private TeamMapper teamMapper;

    // ==================== 积分计算函数 ====================

    /**
     * 足球计分制：赢3分，平1分，输0分
     */
    public int calculateFootballPoints(int win, int draw, int loss) {
        return win * 3 + draw * 1 + loss * 0;
    }

    /**
     * 篮球计分制：赢2分，平0分，输1分（占位，后续可根据实际规则调整）
     */
    public int calculateBasketballPoints(int win, int draw, int loss) {
        return win * 2 + draw * 0 + loss * 1;
    }

    /**
     * 排球计分制：赢2分，平0分，输0分（占位，后续可根据实际规则调整）
     */
    public int calculateVolleyballPoints(int win, int draw, int loss) {
        return win * 2 + draw * 0 + loss * 0;
    }

    /**
     * 根据赛事类型获取积分
     *
     * @param tournamentType 赛事类型：1-足球，2-篮球，3-排球
     * @param win            胜场数
     * @param draw           平场数
     * @param loss           负场数
     * @return 计算后的总积分
     */
    public int calculatePoints(int tournamentType, int win, int draw, int loss) {
        switch (tournamentType) {
            case 1: // 足球
                return calculateFootballPoints(win, draw, loss);
            case 2: // 篮球
                return calculateBasketballPoints(win, draw, loss);
            case 3: // 排球
                return calculateVolleyballPoints(win, draw, loss);
            default:
                log.warn("未知赛事类型: {}，默认使用足球计分制", tournamentType);
                return calculateFootballPoints(win, draw, loss);
        }
    }

    // ==================== 业务方法 ====================

    @Override
    @Transactional
    public void add(GroupStage groupStage) {
        groupStageMapper.insert(groupStage);
        log.info("添加小组成功 - id: {}, name: {}, tournamentId: {}", groupStage.getId(), groupStage.getName(), groupStage.getTournamentId());
    }

    @Override
    @Transactional
    public void addBatch(List<GroupStage> groupStages) {
        if (groupStages == null || groupStages.isEmpty()) {
            log.warn("批量添加小组失败：小组列表为空");
            return;
        }
        for (GroupStage groupStage : groupStages) {
            groupStageMapper.insert(groupStage);
            log.info("批量添加小组 - id: {}, name: {}, tournamentId: {}", groupStage.getId(), groupStage.getName(), groupStage.getTournamentId());
        }
        log.info("批量添加小组完成，共添加 {} 个小组", groupStages.size());
    }

    @Override
    public GroupStage getById(Integer id) {
        return groupStageMapper.selectById(id);
    }

    @Override
    public List<GroupStage> getByTournamentId(Integer tournamentId) {
        return groupStageMapper.selectByTournamentId(tournamentId);
    }

    @Override
    public GroupStageDetailVO getDetail(Integer id) {
        // 1. 查询小组基本信息
        GroupStage groupStage = groupStageMapper.selectById(id);
        if (groupStage == null) {
            log.warn("查询小组详情失败：小组不存在 - id: {}", id);
            return null;
        }

        // 2. 构建 VO
        GroupStageDetailVO vo = new GroupStageDetailVO();
        vo.setId(groupStage.getId());
        vo.setName(groupStage.getName());
        vo.setTeamCount(groupStage.getTeamCount());
        vo.setDirectAdvance(groupStage.getDirectAdvance());
        vo.setIndirectAdvance(groupStage.getIndirectAdvance());

        // 3. 查询该小组的球队成绩
        List<GroupStageTeam> stageTeams = groupStageTeamMapper.selectByGroupStageId(id);
        List<GroupStageDetailVO.TeamScoreItem> teamScores = new ArrayList<>();

        for (GroupStageTeam st : stageTeams) {
            GroupStageDetailVO.TeamScoreItem item = new GroupStageDetailVO.TeamScoreItem();
            item.setTeamId(st.getTeamId());
            item.setWin(st.getWin());
            item.setDraw(st.getDraw());
            item.setLoss(st.getLoss());
            item.setPoints(st.getPoints());
            item.setGoalsFor(st.getGoalsFor());
            item.setGoalsAgainst(st.getGoalsAgainst());
            item.setGoalDifference(st.getGoalDifference());

            // 联表查询球队名称和Logo
            Team team = teamMapper.selectById(st.getTeamId().longValue());
            if (team != null) {
                item.setTeamName(team.getName());
                item.setTeamShortName(team.getShortName());
                item.setLogo(team.getLogo());
            }

            teamScores.add(item);
        }
        vo.setTeams(teamScores);

        log.info("查询小组详情成功 - id: {}, name: {}, teams: {}", id, groupStage.getName(), teamScores.size());
        return vo;
    }
}
