package com.marler.teammap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marler.teammap.dto.request.RegisterRequest;
import com.marler.teammap.mapper.UserMapper;
import com.marler.teammap.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户注册接口集成测试
 * 使用真实数据库（@Transactional 自动回滚，不污染数据）
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional  // 每个测试方法执行完毕后自动回滚数据库
@DisplayName("用户注册接口 - 集成测试（写入真实数据库）")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    // ==================== 辅助方法 ====================

    private RegisterRequest buildRequest(String username, String password, Integer role) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRole(role);
        return request;
    }

    private void performRegisterAndExpectSuccess(RegisterRequest request) throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("注册成功"));
    }

    // ==================== 批量用户数据提供者 ====================

    static Stream<RegisterRequest> batchUserProvider() {
        return Stream.of(
                buildRequestStatic("batch_user_01", "Test123456", 1),
                buildRequestStatic("batch_user_02", "Pass@word1", 1),
                buildRequestStatic("batch_user_03", "Admin@2024", 1),
                buildRequestStatic("batch_user_04", "Sys@Admin!", 1),
                buildRequestStatic("batch_user_05", "Player!123", 1)
        );
    }

    /**
     * 无效请求数据提供者（必须为 static，且被外层的 @MethodSource 引用）
     */
    static Stream<RegisterRequest> invalidRequestProvider() {
        return Stream.of(
                buildRequestStatic(null, "password123", 1),
                buildRequestStatic("", "password123", 1),
                buildRequestStatic("   ", "password123", 1),
                buildRequestStatic("no_db_write_01", null, 1),
                buildRequestStatic("no_db_write_02", "", 1),
                buildRequestStatic("no_db_write_03", "12345", 1)
        );
    }

    private static RegisterRequest buildRequestStatic(String username, String password, Integer role) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRole(role);
        return request;
    }

    // ================================================================
    //  集成测试用例
    // ================================================================

    @Nested
    @DisplayName("批量用户注册 - 逐条写入数据库验证")
    class BatchRegisterToDatabase {

        @ParameterizedTest(name = "批量注册 - username={0}, role={1}")
        @MethodSource("com.marler.teammap.controller.UserControllerIntegrationTest#batchUserProvider")
        void testBatchRegisterPersistToDatabase(RegisterRequest request) throws Exception {
            // given - 注册前数据库中不应存在该用户
            User before = userMapper.findByUsername(request.getUsername());
            assertNull(before, "注册前用户不应存在: " + request.getUsername());

            // when - 执行注册请求（真实写入数据库）
            performRegisterAndExpectSuccess(request);

            // then - 验证数据已写入数据库
            User savedUser = userMapper.findByUsername(request.getUsername());
            assertNotNull(savedUser, "注册后用户应存在于数据库: " + request.getUsername());
            assertEquals(request.getUsername(), savedUser.getUsername(), "用户名应匹配");

            // 验证密码已加密（存库的是密文，不是明文）
            assertNotNull(savedUser.getPassword(), "密码不应为 null");
            assertNotEquals(request.getPassword(), savedUser.getPassword(), "密码应已被加密");

            // 验证角色：服务层在没有邀请码时强制设为 1（普通用户）
            assertEquals(Integer.valueOf(1), savedUser.getRole(), "无邀请码注册时角色应为1（普通用户）");

            // 验证时间字段自动填充
            assertNotNull(savedUser.getCreateTime(), "创建时间不应为空");
            assertNotNull(savedUser.getUpdateTime(), "更新时间不应为空");

            // 验证自增 ID 已回填
            assertNotNull(savedUser.getId(), "自增 ID 应已回填");
        }
    }

    @Nested
    @DisplayName("重复用户名注册 - 数据库唯一性验证")
    class DuplicateUsernameInDatabase {

        @Test
        @DisplayName("先插入一条用户，再注册相同用户名应失败")
        void testRegisterDuplicateUsernameFails() throws Exception {
            // given - 先注册一个用户（真实写入数据库）
            RegisterRequest request = buildRequest("unique_test_user", "password123", 1);
            performRegisterAndExpectSuccess(request);

            // 确认已写入数据库
            User firstSave = userMapper.findByUsername("unique_test_user");
            assertNotNull(firstSave);

            // when - 再次注册相同用户名
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户名已存在"));

            // then - 数据库中仍只有一条记录
            User user = userMapper.findByUsername("unique_test_user");
            assertNotNull(user);
            // 验证 ID 和第一次插入的一致（没有重复插入）
            assertEquals(firstSave.getId(), user.getId());
        }
    }

    @Nested
    @DisplayName("多用户批量注册 - 业务完整性验证")
    class MultiUserBatchRegister {

        @Test
        @DisplayName("连续注册5个用户，全部验证数据库落盘")
        void testMultipleUsersInSequence() throws Exception {
            // 准备5个用户（无邀请码注册，角色统一为1）
            RegisterRequest[] users = {
                    buildRequest("seq_user_01", "Pass123456", 1),
                    buildRequest("seq_user_02", "Pass123456", 1),
                    buildRequest("seq_user_03", "Pass123456", 1),
                    buildRequest("seq_user_04", "Pass123456", 1),
                    buildRequest("seq_user_05", "Pass123456", 1)
            };

            // 逐个注册并验证
            for (RegisterRequest user : users) {
                performRegisterAndExpectSuccess(user);

                // 实时验证数据库落盘
                User saved = userMapper.findByUsername(user.getUsername());
                assertNotNull(saved, "用户 " + user.getUsername() + " 应已写入数据库");
                assertEquals(user.getUsername(), saved.getUsername());
                // 无邀请码时，服务层强制设为角色1
                assertEquals(Integer.valueOf(1), saved.getRole());
            }

            // 全部注册完成后，验证总共5条记录
            for (RegisterRequest user : users) {
                User saved = userMapper.findByUsername(user.getUsername());
                assertNotNull(saved, "用户 " + user.getUsername() + " 应仍存在于数据库");
            }
        }
    }

    @Nested
    @DisplayName("默认角色注册 - 数据库落盘验证")
    class DefaultRolePersistence {

        @Test
        @DisplayName("不传role时，数据库应存储默认角色1")
        void testDefaultRolePersistedInDatabase() throws Exception {
            // given - 不设置 role
            RegisterRequest request = new RegisterRequest();
            request.setUsername("default_role_integration");
            request.setPassword("password123");

            // when
            performRegisterAndExpectSuccess(request);

            // then - 数据库中 role 应为 1
            User saved = userMapper.findByUsername("default_role_integration");
            assertNotNull(saved);
            assertEquals(1, saved.getRole().intValue(), "默认角色应为1（普通用户）");
        }

        @Test
        @DisplayName("role传null时，数据库应存储默认角色1")
        void testNullRolePersistedInDatabase() throws Exception {
            // given
            RegisterRequest request = buildRequest("null_role_integration", "password123", null);

            // when
            performRegisterAndExpectSuccess(request);

            // then - 数据库中 role 应为 1
            User saved = userMapper.findByUsername("null_role_integration");
            assertNotNull(saved);
            assertEquals(1, saved.getRole().intValue(), "role为null时应默认角色1");
        }
    }

    @Nested
    @DisplayName("参数校验 - 不入库验证")
    class ValidationNoDatabaseWrite {

        @ParameterizedTest(name = "无效数据不入库 - username={0}")
        @MethodSource("com.marler.teammap.controller.UserControllerIntegrationTest#invalidRequestProvider")
        void testInvalidRequestNotPersisted(RegisterRequest request) throws Exception {
            // when - 发送无效请求
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));

            // then - 数据库中不应有该用户记录
            if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                User saved = userMapper.findByUsername(request.getUsername());
                assertNull(saved, "无效数据不应写入数据库");
            }
        }
    }
}
