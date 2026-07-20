package com.marler.teammap.controller;

import com.marler.teammap.dto.request.AddTeamMemberRequest;
import com.marler.teammap.mapper.TeamMapper;
import com.marler.teammap.mapper.UserMapper;
import com.marler.teammap.pojo.Team;
import com.marler.teammap.pojo.User;
import com.marler.teammap.service.TeamMemberService;
import com.marler.teammap.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║  批量数据生成器                                                  ║
 * ║  Step 1: 生成 500 个用户                                         ║
 * ║  Step 2: 将用户分配到 type=1（足球）和 type=2（篮球）的球队中     ║
 * ║                                                                  ║
 * ║  ⚠️ 注意：此测试不含 @Transactional                              ║
 * ║  写入的数据会持久化到数据库！                                     ║
 * ║                                                                  ║
 * ║  清理 SQL:                                                       ║
 * ║  DELETE FROM player WHERE user_id IN                            ║
 * ║      (SELECT id FROM user WHERE username LIKE 'batch_user_%');   ║
 * ║  DELETE FROM team_member WHERE user_id IN                       ║
 * ║      (SELECT id FROM user WHERE username LIKE 'batch_user_%');   ║
 * ║  DELETE FROM user_profile WHERE user_id IN                      ║
 * ║      (SELECT id FROM user WHERE username LIKE 'batch_user_%');   ║
 * ║  DELETE FROM user WHERE username LIKE 'batch_user_%';            ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 *
 * 使用方法：去掉 @Disabled 注解，直接运行此测试类
 */
@SpringBootTest
@Disabled("默认禁用，避免误运行。如需生成数据，请去掉此注解")
@DisplayName("批量生成500个用户并分配到球队 - 持久化到数据库")
class BatchUserGenerator {

    private static final Logger log = LoggerFactory.getLogger(BatchUserGenerator.class);

    private static final int TOTAL_USERS = 500;
    private static final String DEFAULT_PASSWORD = "Test123456";

    private static final String[] POSITIONS_FOOTBALL = {
            "守门员", "中后卫", "边后卫", "后腰", "中前卫",
            "边前卫", "前腰", "边锋", "前锋", "影子前锋"
    };

    private static final String[] POSITIONS_BASKETBALL = {
            "控球后卫", "得分后卫", "小前锋", "大前锋", "中锋"
    };

    @Autowired
    private UserService userService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 记录生成的用户 ID，用于后续分配球队
     */
    private final List<Long> generatedUserIds = new ArrayList<>();

    @Test
    @DisplayName("Step1+2: 生成用户并分配到球队（全流程）")
    void generate500UsersAndAssignToTeams() {
        // ==================== Step 1: 生成用户 ====================
        generateUsers();

        // ==================== Step 2: 分配到球队 ====================
        assignUsersToTeams();
    }

    @Test
    @DisplayName("仅分配：将已有 batch_user 分配到球队（不复生成用户）")
    void assignOnly() {
        log.info("========== 仅分配：将已有用户分配到球队 ==========");
        // 从数据库加载已有的 batch_user_* 用户
        List<User> batchUsers = userMapper.findByUsernamePrefix("batch_user_");
        log.info("查询到已有 batch_user_* 用户: {} 人", batchUsers.size());

        if (batchUsers.isEmpty()) {
            log.warn("没有找到 batch_user_* 用户，请先运行 generate500UsersAndAssignToTeams()");
            return;
        }

        for (User u : batchUsers) {
            generatedUserIds.add(u.getId());
        }
        assignUsersToTeams();
    }

    /**
     * 生成 500 个用户
     */
    private void generateUsers() {
        log.info("========== Step 1: 批量生成 {} 个用户 ==========", TOTAL_USERS);
        log.info("密码统一为: {}", DEFAULT_PASSWORD);
        log.info("用户名格式: batch_user_001 ~ batch_user_{}", String.format("%03d", TOTAL_USERS));
        log.warn("数据将持久化到数据库，不会被回滚！");

        int successCount = 0;
        int failCount = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= TOTAL_USERS; i++) {
            try {
                User user = new User();
                String username = String.format("batch_user_%03d", i);
                user.setUsername(username);
                user.setPassword(DEFAULT_PASSWORD);
                user.setRole(1); // 普通用户

                userService.register(user);

                // 记录生成的用户 ID
                generatedUserIds.add(user.getId());
                successCount++;

                if (i % 50 == 0) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    log.info("用户生成进度: {}/{} ({}%) - 已耗时: {}ms",
                            i, TOTAL_USERS, (i * 100 / TOTAL_USERS), elapsed);
                }
            } catch (Exception e) {
                failCount++;
                log.warn("用户 [{}] 生成失败: {}", i, e.getMessage());
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("用户生成完毕 - 成功: {} / 失败: {} / 耗时: {}ms",
                successCount, failCount, totalTime);
    }

    /**
     * 将用户分配到 type=1（足球）和 type=2（篮球）的球队
     */
    private void assignUsersToTeams() {
        log.info("========== Step 2: 将用户分配到球队 ==========");

        // 查询 type=1（足球）和 type=2（篮球）的球队
        List<Team> footballTeams = teamMapper.selectByType(1);
        List<Team> basketballTeams = teamMapper.selectByType(2);

        log.info("查询到 足球(type=1)球队: {} 支", footballTeams.size());
        log.info("查询到 篮球(type=2)球队: {} 支", basketballTeams.size());

        if (generatedUserIds.isEmpty()) {
            log.warn("没有用户可分配，跳过球队分配");
            return;
        }

        if (footballTeams.isEmpty() && basketballTeams.isEmpty()) {
            log.warn("数据库中没有 type=1 或 type=2 的球队！");
            log.warn("请先创建球队后再运行此生成器");
            return;
        }

        // 分配策略：前 60% 用户给足球(1)，后 40% 给篮球(2)
        int totalUsers = generatedUserIds.size();
        int footballCount = (int) (totalUsers * 0.6);

        List<Long> footballUsers = generatedUserIds.subList(0, Math.min(footballCount, totalUsers));
        List<Long> basketballUsers = generatedUserIds.subList(
                footballUsers.size(), totalUsers);

        int fbAssigned = assignToTeamType(footballUsers, footballTeams, 1, POSITIONS_FOOTBALL);
        int bbAssigned = assignToTeamType(basketballUsers, basketballTeams, 2, POSITIONS_BASKETBALL);

        log.info("========== 球队分配完成 ==========");
        log.info("足球(type=1) 分配: {} 人", fbAssigned);
        log.info("篮球(type=2) 分配: {} 人", bbAssigned);
        log.info("合计: {} 人", fbAssigned + bbAssigned);
    }

    /**
     * 将一批用户分配到指定类型的球队中
     */
    private int assignToTeamType(List<Long> userIds, List<Team> teams,
                                  int sportType, String[] positions) {
        if (teams.isEmpty() || userIds.isEmpty()) {
            log.info("运动类型 {} 没有球队或没有用户可分配", sportType);
            return 0;
        }

        int successCount = 0;
        int failCount = 0;
        long startTime = System.currentTimeMillis();
        int teamIndex = 0;

        for (int i = 0; i < userIds.size(); i++) {
            Long userId = userIds.get(i);
            Team team = teams.get(teamIndex % teams.size()); // 轮询分配

            try {
                AddTeamMemberRequest request = new AddTeamMemberRequest();
                request.setTeamId(team.getId());
                request.setUserId(userId);
                request.setJerseyName("Player" + (i + 1));

                // 球衣号码：1-99 循环
                int jerseyNumber = (i % 99) + 1;
                request.setJerseyNumber(jerseyNumber);

                // 位置：根据运动类型分配
                String position = positions[(i / teams.size()) % positions.length];
                request.setPosition(position);

                teamMemberService.add(request);
                successCount++;

                // 切换到下一支球队（轮询）
                teamIndex++;

                if ((i + 1) % 100 == 0) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    log.info("运动类型{} 分配进度: {}/{} - 耗时: {}ms",
                            sportType, i + 1, userIds.size(), elapsed);
                }
            } catch (Exception e) {
                failCount++;
                log.warn("用户ID[{}] 分配到球队[{}] 失败: {}",
                        userId, team.getName(), e.getMessage());
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("运动类型{} 分配完毕 - 成功: {} / 失败: {} / 耗时: {}ms",
                sportType, successCount, failCount, totalTime);
        return successCount;
    }
}
