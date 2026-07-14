package com.marler.teammap.service.impl;

import com.marler.teammap.dto.request.GenerateGroupScheduleRequest;
import com.marler.teammap.dto.response.GroupStageDetailVO;
import com.marler.teammap.dto.response.MatchDetailVO;
import com.marler.teammap.mapper.GroupStageMapper;
import com.marler.teammap.mapper.GroupStageTeamMapper;
import com.marler.teammap.mapper.MatchMapper;
import com.marler.teammap.mapper.TeamMapper;
import com.marler.teammap.pojo.GroupStage;
import com.marler.teammap.pojo.GroupStageTeam;
import com.marler.teammap.pojo.Match;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.service.GroupStageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private MatchMapper matchMapper;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    @Override
    @Transactional
    public void deleteGroup(Integer id) {
        // 1. 先检查小组是否存在
        GroupStage groupStage = groupStageMapper.selectById(id);
        if (groupStage == null) {
            log.warn("删除小组失败：小组不存在 - id: {}", id);
            throw new RuntimeException("小组不存在");
        }

        // 2. 删除小组关联的球队（group_stage_team）
        groupStageTeamMapper.deleteByGroupStageId(id);
        log.info("删除小组[{}]的球队关联成功", id);

        // 3. 删除小组本身
        groupStageMapper.deleteById(id);
        log.info("删除小组成功 - id: {}, name: {}", id, groupStage.getName());
    }

    // ==================== 全局排赛程（跨所有小组） ====================

    /**
     * {@inheritDoc}
     * <p>
     * 为整个赛事的所有小组统一生成赛程。步骤如下：
     * <ol>
     *   <li>获取赛事所有小组及每个小组的球队</li>
     *   <li>对每个小组调用 Circle Method 生成配对（不带时间）</li>
     *   <li>将所有小组的比赛合并，跨小组统一分配时间</li>
     *   <li>每天最多 3 场比赛，周末跳过</li>
     *   <li>清除该赛事所有小组的旧赛程，批量插入新赛程</li>
     * </ol>
     */
    @Override
    @Transactional
    public List<MatchDetailVO> generateAllSchedules(Integer tournamentId, GenerateGroupScheduleRequest request) {
        log.info("全局排赛程 - tournamentId: {}", tournamentId);

        // 1. 获取赛事所有小组
        List<GroupStage> groups = groupStageMapper.selectByTournamentId(tournamentId);
        if (groups == null || groups.isEmpty()) {
            log.warn("全局排赛程失败：赛事[{}]没有小组", tournamentId);
            throw new RuntimeException("该赛事暂无小组，请先创建小组并分配球队");
        }

        // 2. 为每个小组生成按轮分组的配对
        // allRoundsByGroup[groupIdx] = [round0_matches, round1_matches, ...]
        List<List<List<Match>>> allRoundsByGroup = new ArrayList<>();
        int maxRounds = 0;

        for (GroupStage group : groups) {
            List<GroupStageTeam> stageTeams = groupStageTeamMapper.selectByGroupStageId(group.getId());
            if (stageTeams == null || stageTeams.isEmpty()) {
                log.warn("全局排赛程：小组[{}]没有球队，跳过", group.getName());
                continue;
            }

            List<Integer> teamIds = new ArrayList<>();
            for (GroupStageTeam st : stageTeams) {
                teamIds.add(st.getTeamId());
            }

            Integer roundType = group.getRoundType() != null ? group.getRoundType() : 1;
            List<List<Match>> rounds = generateRoundRobinRounds(group, teamIds, roundType);
            allRoundsByGroup.add(rounds);
            maxRounds = Math.max(maxRounds, rounds.size());
        }

        if (allRoundsByGroup.isEmpty()) {
            log.warn("全局排赛程失败：没有任何小组有球队");
            throw new RuntimeException("所有小组都没有球队，无法生成赛程");
        }

        log.info("全局排赛程：共 {} 个有效小组，最大轮数 {}", allRoundsByGroup.size(), maxRounds);

        // 3. 清除该赛事所有小组的旧赛程
        for (GroupStage group : groups) {
            matchMapper.deleteByGroupStageId(group.getId());
        }
        log.info("已清除赛事[{}]所有小组的旧赛程", tournamentId);

        // 4. 按轮次全局分配时间（所有小组打同一轮 → 再进入下一轮）
        final int[] TIME_SLOTS = {12, 14, 16};
        LocalDateTime currentDay = skipWeekends(parseStartDate(request.getStartDate()));
        int matchesToday = 0;

        List<Match> allScheduledMatches = new ArrayList<>();

        for (int roundIdx = 0; roundIdx < maxRounds; roundIdx++) {
            // 收集本轮所有小组的所有比赛
            List<Match> roundMatches = new ArrayList<>();
            for (List<List<Match>> groupRounds : allRoundsByGroup) {
                if (roundIdx < groupRounds.size()) {
                    roundMatches.addAll(groupRounds.get(roundIdx));
                }
            }

            log.debug("全局排赛程 - 第{}轮: {} 场比赛", roundIdx + 1, roundMatches.size());

            // 为本轮比赛分配时间
            for (Match match : roundMatches) {
                // 检查当天是否已达 3 场上限
                if (matchesToday >= 3) {
                    currentDay = skipWeekends(currentDay.plusDays(1));
                    matchesToday = 0;
                }

                // 固定时段：第1场12:00、第2场14:00、第3场16:00
                LocalDateTime matchTime = currentDay
                        .withHour(TIME_SLOTS[matchesToday])
                        .withMinute(0).withSecond(0).withNano(0);
                match.setMatchTime(matchTime.format(DT_FMT));
                match.setLocation(request.getLocation());

                allScheduledMatches.add(match);
                matchesToday++;
            }

            // 本轮结束，推进到下一轮的首日（使轮次之间有清晰边界）
            currentDay = skipWeekends(currentDay.plusDays(1));
            matchesToday = 0;
        }

        // 5. 批量插入所有比赛
        if (!allScheduledMatches.isEmpty()) {
            matchMapper.insertBatch(allScheduledMatches);
        }
        log.info("全局排赛程成功 - tournamentId: {}, 共 {} 场比赛",
                tournamentId, allScheduledMatches.size());

        // 6. 转为 VO 返回
        return convertToMatchDetailVOs(allScheduledMatches);
    }

    /**
     * 为单个小组生成按轮分组的比赛配对（不分配时间）
     *
     * @return List<List<Match>> — 外层是轮次列表，内层是每轮的比赛
     */
    private List<List<Match>> generateRoundRobinRounds(
            GroupStage groupStage, List<Integer> teamIds, int roundType) {

        int n = teamIds.size();
        boolean hasBye = (n % 2 != 0);
        if (hasBye) {
            teamIds.add(-1);
            n++;
        }

        int totalRounds = n - 1;
        int matchesPerRound = n / 2;

        List<Integer> circle = new ArrayList<>(teamIds);
        List<List<Match>> allRounds = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++) {
            List<Match> roundMatches = new ArrayList<>();

            for (int i = 0; i < matchesPerRound; i++) {
                Integer home = circle.get(i);
                Integer away = circle.get(n - 1 - i);

                if (home == -1 || away == -1) continue;

                Match m = new Match();
                m.setTournamentId(groupStage.getTournamentId());
                m.setGroupStageId(groupStage.getId());
                if ((round + 1) % 2 == 1) {
                    m.setTeam1Id(home);
                    m.setTeam2Id(away);
                } else {
                    m.setTeam1Id(away);
                    m.setTeam2Id(home);
                }
                m.setTeam1Score(0);
                m.setTeam2Score(0);
                m.setStatus(1);
                m.setStage(2);
                roundMatches.add(m);
            }

            allRounds.add(roundMatches);

            // 轮转
            Integer last = circle.remove(n - 1);
            circle.add(1, last);
        }

        // 双循环：镜像复制每一轮
        if (roundType == 2) {
            int originalRoundCount = allRounds.size();
            for (int r = 0; r < originalRoundCount; r++) {
                List<Match> mirrorRound = new ArrayList<>();
                for (Match original : allRounds.get(r)) {
                    Match mirror = new Match();
                    mirror.setTournamentId(original.getTournamentId());
                    mirror.setGroupStageId(original.getGroupStageId());
                    mirror.setTeam1Id(original.getTeam2Id());
                    mirror.setTeam2Id(original.getTeam1Id());
                    mirror.setTeam1Score(0);
                    mirror.setTeam2Score(0);
                    mirror.setStatus(1);
                    mirror.setStage(2);
                    mirrorRound.add(mirror);
                }
                allRounds.add(mirrorRound);
            }
        }

        return allRounds;
    }

    // ==================== 单小组赛程生成 ====================

    /**
     * {@inheritDoc}
     * <p>
     * 算法说明（Circle Method / 图论 1-因子分解）：
     * <pre>
     * 将 n 支球队看作完全图 K_n 的顶点，边表示比赛。
     * 固定顶点 0 为锚点，其余 n-1 个顶点围成一个圆环。
     * 每轮配对：(0 vs n-1), (1 vs n-2), (2 vs n-3), ...
     * 然后固定 0，其余顶点顺时针旋转一个位置，继续下一轮。
     *
     * 例：4 队 (A,B,C,D)，锚点 A：
     *   第1轮：A-D, B-C
     *   第2轮：A-C, D-B  (旋转后 [A, D, B, C])
     *   第3轮：A-B, C-D  (旋转后 [A, C, D, B])
     * </pre>
     * 双循环（roundType=2）：镜像复制单循环赛程，主客场互换。
     */
    @Override
    @Transactional
    public List<MatchDetailVO> generateSchedule(Integer groupStageId, GenerateGroupScheduleRequest request) {
        // 1. 验证小组
        GroupStage groupStage = groupStageMapper.selectById(groupStageId);
        if (groupStage == null) {
            log.warn("生成赛程失败：小组不存在 - id: {}", groupStageId);
            throw new RuntimeException("小组不存在");
        }

        // 2. 获取该小组的所有球队（按关联表 id 排序）
        List<GroupStageTeam> stageTeams = groupStageTeamMapper.selectByGroupStageId(groupStageId);
        if (stageTeams == null || stageTeams.isEmpty()) {
            log.warn("生成赛程失败：小组[{}]没有球队", groupStageId);
            throw new RuntimeException("小组暂无球队，请先分配球队");
        }

        int teamCount = stageTeams.size();
        Integer roundType = groupStage.getRoundType() != null ? groupStage.getRoundType() : 1;

        // 3. 提取球队ID列表
        List<Integer> teamIds = new ArrayList<>();
        for (GroupStageTeam st : stageTeams) {
            teamIds.add(st.getTeamId());
        }

        // 4. 删除该小组已有的赛程（重新生成）
        matchMapper.deleteByGroupStageId(groupStageId);
        log.info("已清除小组[{}]的旧赛程", groupStageId);

        // 5. 使用 Circle Method（轮转法）生成所有配对
        List<Match> matches = generateRoundRobinMatches(
                groupStage, teamIds, roundType, request
        );

        // 6. 批量插入
        if (!matches.isEmpty()) {
            matchMapper.insertBatch(matches);
            log.info("为小组[{}]生成赛程完成 - 共 {} 场比赛，{} 轮",
                    groupStageId, matches.size(),
                    roundType == 2 ? matches.size() / (teamCount / 2) * 2 : matches.size() / (teamCount / 2));
        }

        // 7. 将 Match 转为 MatchDetailVO 返回
        return convertToMatchDetailVOs(matches);
    }

    /**
     * 核心算法：使用 Circle Method（图论完全图的 1-因子分解）生成循环赛配对
     * <p>
     * 原理：将 n 支球队排成一个圆（n 为偶数，奇数时补虚拟球队），
     * 固定第 0 支球队为锚点，其余球队围成一圈。
     * 每轮配对规则：(0 vs n-1), (1 vs n-2), (2 vs n-3), ...
     * 每轮结束后保持锚点不动，其余球队顺时针旋转一个位置。
     *
     * @param groupStage 小组信息
     * @param teamIds    球队ID列表（按顺序）
     * @param roundType  循环类型：1-单循环，2-双循环
     * @param request    时间配置
     * @return 比赛列表
     */
    private List<Match> generateRoundRobinMatches(
            GroupStage groupStage,
            List<Integer> teamIds,
            int roundType,
            GenerateGroupScheduleRequest request) {

        int n = teamIds.size();
        // 如果 n 为奇数，添加一个虚拟 ID（-1 表示轮空）
        boolean hasBye = (n % 2 != 0);
        if (hasBye) {
            teamIds.add(-1); // 虚拟球队 = 轮空
            n++;
        }

        int totalRounds = n - 1; // n 支球队（含虚拟）时，n-1 轮
        int matchesPerRound = n / 2;

        log.info("Circle Method: {}支球队（含虚拟={}），{}轮，每轮{}场比赛，共{}场",
                n, hasBye, totalRounds, matchesPerRound, totalRounds * matchesPerRound);

        // 固定每天三个时段：12:00、14:00、16:00
        final int[] TIME_SLOTS = {12, 14, 16};
        LocalDateTime currentDay = skipWeekends(parseStartDate(request.getStartDate()));
        String location = request.getLocation();
        int matchesToday = 0;
        int matchOrderInDay = 0;

        // 构建可变的球队列表（用于轮转）
        List<Integer> circle = new ArrayList<>(teamIds);

        // 保存生成的比赛
        List<Match> allMatches = new ArrayList<>();

        // === Circle Method 核心循环 ===
        for (int round = 0; round < totalRounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                Integer home = circle.get(i);
                Integer away = circle.get(n - 1 - i);

                if (home == -1 || away == -1) continue;

                // 检查当天比赛是否已达 3 场上限
                if (matchesToday >= 3) {
                    currentDay = skipWeekends(currentDay.plusDays(1));
                    matchesToday = 0;
                    matchOrderInDay = 0;
                }

                // 固定时段：第1场12:00、第2场14:00、第3场16:00
                LocalDateTime matchTime = currentDay
                        .withHour(TIME_SLOTS[matchesToday])
                        .withMinute(0).withSecond(0).withNano(0);

                Match m = buildMatch(groupStage, home, away, matchTime, location);
                allMatches.add(m);
                matchesToday++;
                matchOrderInDay++;
            }

            // 轮转
            Integer last = circle.remove(n - 1);
            circle.add(1, last);

            // 轮次结束后推进到下一周期的首日
            currentDay = skipWeekends(currentDay.plusDays(1));
            matchesToday = 0;
            matchOrderInDay = 0;
        }

        // === 双循环处理 ===
        if (roundType == 2) {
            // 镜像复制：所有比赛主客场互换（team1 ↔ team2），时间后移
            int originalSize = allMatches.size();
            // 第二回合的开始日期（跳过周末）
            LocalDateTime secondLegDay = skipWeekends(currentDay);
            int secondLegMatchesToday = 0;
            int secondLegOrderInDay = 0;

            for (int i = 0; i < originalSize; i++) {
                Match original = allMatches.get(i);
                Match mirror = new Match();
                mirror.setTournamentId(original.getTournamentId());
                mirror.setGroupStageId(original.getGroupStageId());
                // 主客场互换
                mirror.setTeam1Id(original.getTeam2Id());
                mirror.setTeam2Id(original.getTeam1Id());
                mirror.setTeam1Score(0);
                mirror.setTeam2Score(0);
                mirror.setStatus(1);
                mirror.setStage(2);
                mirror.setLocation(original.getLocation());

                // 检查每天 3 场上限
                if (secondLegMatchesToday >= MAX_MATCHES_PER_DAY) {
                    secondLegDay = skipWeekends(secondLegDay.plusDays(1));
                    secondLegMatchesToday = 0;
                    secondLegOrderInDay = 0;
                }

                LocalDateTime matchTime = secondLegDay
                        .withHour(parseStartTime(request.getStartTime()).getHour())
                        .withMinute(parseStartTime(request.getStartTime()).getMinute())
                        .withSecond(0)
                        .withNano(0)
                        .plusMinutes((long) secondLegOrderInDay * matchIntervalMinutes);
                mirror.setMatchTime(matchTime.format(DT_FMT));

                allMatches.add(mirror);
                secondLegMatchesToday++;
                secondLegOrderInDay++;
            }
        }

        return allMatches;
    }

    /**
     * 如果指定日期落在周末（周六或周日），跳到下一个周一
     */
    private LocalDateTime skipWeekends(LocalDateTime dateTime) {
        DayOfWeek dow = dateTime.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY) {
            return dateTime.plusDays(2);
        } else if (dow == DayOfWeek.SUNDAY) {
            return dateTime.plusDays(1);
        }
        return dateTime;
    }

    /**
     * 构建一场比赛对象
     */
    private Match buildMatch(GroupStage groupStage, int team1Id, int team2Id,
                             int round, LocalDateTime baseTime, String location) {
        Match match = new Match();
        match.setTournamentId(groupStage.getTournamentId());
        match.setGroupStageId(groupStage.getId());
        // 使用 round 的奇偶性决定主客场，使每支球队的主客场尽量均衡
        if (round % 2 == 1) {
            match.setTeam1Id(team1Id);
            match.setTeam2Id(team2Id);
        } else {
            match.setTeam1Id(team2Id);
            match.setTeam2Id(team1Id);
        }
        match.setTeam1Score(0);
        match.setTeam2Score(0);
        match.setStatus(1);  // 未开始
        match.setStage(2);   // 小组赛
        match.setMatchTime(baseTime.format(DT_FMT));
        match.setLocation(location);
        return match;
    }

    /**
     * 解析开始日期（仅日期部分 yyyy-MM-dd），为空则使用当前日期
     */
    private LocalDateTime parseStartDate(String startDateStr) {
        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            try {
                // 支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss 格式
                String dateStr = startDateStr.trim();
                if (dateStr.length() <= 10) {
                    return LocalDateTime.parse(dateStr + " 00:00:00", DT_FMT);
                }
                return LocalDateTime.parse(dateStr, DT_FMT);
            } catch (Exception e) {
                log.warn("解析开始日期失败: {}，使用当前日期", startDateStr);
            }
        }
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * 构建一场比赛对象（使用固定时段）
     */
    private Match buildMatch(GroupStage groupStage, int team1Id, int team2Id,
                             LocalDateTime matchTime, String location) {
        Match match = new Match();
        match.setTournamentId(groupStage.getTournamentId());
        match.setGroupStageId(groupStage.getId());
        match.setTeam1Id(team1Id);
        match.setTeam2Id(team2Id);
        match.setTeam1Score(0);
        match.setTeam2Score(0);
        match.setStatus(1);  // 未开始
        match.setStage(2);   // 小组赛
        match.setMatchTime(matchTime.format(DT_FMT));
        match.setLocation(location);
        return match;
    }

    /**
     * 将 Match 列表转为 MatchDetailVO 列表
     */
    private List<MatchDetailVO> convertToMatchDetailVOs(List<Match> matches) {
        List<MatchDetailVO> result = new ArrayList<>();
        for (Match match : matches) {
            MatchDetailVO vo = new MatchDetailVO();
            vo.setId(match.getId());
            vo.setTournamentId(match.getTournamentId());
            vo.setGroupStageId(match.getGroupStageId());
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
            Team team1 = teamMapper.selectById(match.getTeam1Id().longValue());
            Team team2 = teamMapper.selectById(match.getTeam2Id().longValue());
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
        return result;
    }
}
