package com.marler.teammap.service;

import com.marler.teammap.dto.request.GenerateGroupScheduleRequest;
import com.marler.teammap.dto.response.GroupStageDetailVO;
import com.marler.teammap.dto.response.MatchDetailVO;
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

    /**
     * 删除小组（同时删除已分配的球队关联）
     */
    void deleteGroup(Integer id);

    /**
     * 为指定小组生成赛程（利用图结构轮转法/ Circle Method）
     * <p>
     * 原理：将 n 支球队视为完全图 K_n 的顶点，边表示比赛。
     * 通过 Circle Method（固定锚点 + 轮转）得到所有配对。
     * <ul>
     *   <li>n 为偶数时：n-1 轮，每轮 n/2 场比赛</li>
     *   <li>n 为奇数时：n 轮，每轮 (n-1)/2 场比赛（有一队轮空）</li>
     * </ul>
     *
     * @param groupStageId 小组ID
     * @param request      赛程配置（开始时间、间隔等）
     * @return 生成的比赛列表（含轮次信息）
     */
    List<MatchDetailVO> generateSchedule(Integer groupStageId, GenerateGroupScheduleRequest request);

    /**
     * 为整个赛事的所有小组统一生成赛程（跨小组统一排期）
     * <p>
     * 先为每个小组独立生成配对（Circle Method），
     * 再跨所有小组统一分配时间，确保：
     * <ul>
     *   <li>每天最多 3 场比赛（跨所有小组合计）</li>
     *   <li>周末不安排比赛</li>
     * </ul>
     *
     * @param tournamentId 赛事ID
     * @param request      赛程配置
     * @return 每个小组的生成的比赛列表（按小组ID分组）
     */
    List<MatchDetailVO> generateAllSchedules(Integer tournamentId, GenerateGroupScheduleRequest request);
}
