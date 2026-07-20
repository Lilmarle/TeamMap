package com.marler.teammap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marler.teammap.dto.request.RegisterRequest;
import com.marler.teammap.pojo.User;
import com.marler.teammap.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户注册接口批量测试
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    // ==================== 辅助方法 ====================

    /**
     * 构建注册请求
     */
    private RegisterRequest buildRequest(String username, String password, Integer role) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRole(role);
        return request;
    }

    /**
     * 执行注册请求并验证成功结果
     */
    private void performRegisterAndExpectSuccess(RegisterRequest request) throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("注册成功"));
    }

    /**
     * 执行注册请求并验证错误结果
     */
    private void performRegisterAndExpectError(RegisterRequest request, String expectedErrorMsg) throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(expectedErrorMsg));
    }

    // ==================== 参数化测试数据提供者 ====================

    /**
     * 批量合法用户数据 - 包含不同用户名、角色组合
     */
    static Stream<RegisterRequest> validUserProvider() {
        return Stream.of(
                buildRequestStatic("testuser_01", "password123", 1),
                buildRequestStatic("testuser_02", "12345678", 1),
                buildRequestStatic("admin_user", "Admin@123", 3),
                buildRequestStatic("sys_admin", "Sys@dmin2024", 4),
                buildRequestStatic("player_01", "player@123", 2),
                buildRequestStatic("zhang_san", "Zhangsan@123", 1),
                buildRequestStatic("li_si", "Lisi@2024!", 1),
                buildRequestStatic("wang_wu", "Ww@2024!!", 3),
                buildRequestStatic("edge_user_a", "a1234567", 1),
                buildRequestStatic("edge_user_", "abcdefghij", 1)
        );
    }

    /**
     * 批量无效用户数据 - 包含各种边界和异常情况
     */
    static Stream<RegisterRequest> invalidUserProvider() {
        return Stream.of(
                buildRequestStatic(null, "password123", 1),         // 用户名为 null
                buildRequestStatic("", "password123", 1),           // 用户名为空串
                buildRequestStatic("   ", "password123", 1),        // 用户名全空格
                buildRequestStatic("valid_user", null, 1),          // 密码为 null
                buildRequestStatic("valid_user", "", 1),            // 密码为空串
                buildRequestStatic("valid_user", "12345", 1),       // 密码长度不足6位
                buildRequestStatic("valid_user", "abc", 1)          // 密码仅3位
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
    //  测试用例
    // ================================================================

    @Nested
    @DisplayName("批量注册成功测试 - 10组合法用户数据")
    class BatchRegisterSuccess {

        @ParameterizedTest(name = "注册成功 - username={0}, role={1}")
        @MethodSource("com.marler.teammap.controller.UserControllerTest#validUserProvider")
        void testBatchRegisterSuccess(RegisterRequest request) throws Exception {
            // given
            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            // verify: 验证 userService.register 被调用，且参数传递正确
            verify(userService, times(1)).register(any(User.class));
        }
    }

    @Nested
    @DisplayName("参数校验失败测试 - 批量无效数据")
    class ParameterValidationFailure {

        @ParameterizedTest(name = "注册失败 - username={0}, password={1}")
        @MethodSource("com.marler.teammap.controller.UserControllerTest#invalidUserProvider")
        void testRegisterWithInvalidParams(RegisterRequest request) throws Exception {
            // given
            // 不调用 userService.register，因为 Controller 层校验会先拦截

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));

            // verify: 参数校验失败时，userService.register 不应被调用
            verify(userService, never()).register(any());
        }
    }

    @Nested
    @DisplayName("用户名重复测试")
    class DuplicateUsername {

        @Test
        @DisplayName("注册已存在的用户名应返回错误")
        void testRegisterDuplicateUsername() throws Exception {
            // given
            RegisterRequest request = buildRequest("existing_user", "password123", 1);
            doThrow(new RuntimeException("用户名已存在"))
                    .when(userService).register(any(User.class));

            // when & then
            performRegisterAndExpectError(request, "用户名已存在");

            verify(userService, times(1)).register(any(User.class));
        }
    }

    @Nested
    @DisplayName("不同角色注册测试")
    class RoleScenarios {

        @Test
        @DisplayName("不传role时默认角色为1（普通用户）")
        void testRegisterWithDefaultRole() throws Exception {
            // given
            RegisterRequest request = new RegisterRequest();
            request.setUsername("default_role_user");
            request.setPassword("password123");
            // 不设置 role

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            // verify: 确保传入 service 的 user 的 role 被设置为 1
            verify(userService, times(1)).register(argThat(user ->
                    user.getRole() == 1
            ));
        }

        @Test
        @DisplayName("注册赛事管理员角色 (role=3)")
        void testRegisterWithEventAdminRole() throws Exception {
            // given
            RegisterRequest request = buildRequest("event_admin", "Admin@123", 3);

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            verify(userService, times(1)).register(argThat(user ->
                    user.getRole() == 3
            ));
        }

        @Test
        @DisplayName("注册系统管理员角色 (role=4)")
        void testRegisterWithSystemAdminRole() throws Exception {
            // given
            RegisterRequest request = buildRequest("sys_admin", "Admin@123", 4);

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            verify(userService, times(1)).register(argThat(user ->
                    user.getRole() == 4
            ));
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCases {

        @Test
        @DisplayName("密码恰好6位（边界值）")
        void testRegisterWithMinLengthPassword() throws Exception {
            // given
            RegisterRequest request = buildRequest("min_pwd_user", "123456", 1);

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            verify(userService, times(1)).register(any(User.class));
        }

        @Test
        @DisplayName("超长用户名")
        void testRegisterWithLongUsername() throws Exception {
            // given
            String longUsername = "a".repeat(50);
            RegisterRequest request = buildRequest(longUsername, "password123", 1);

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            verify(userService, times(1)).register(any(User.class));
        }

        @Test
        @DisplayName("密码含特殊字符")
        void testRegisterWithSpecialCharPassword() throws Exception {
            // given
            RegisterRequest request = buildRequest("special_user", "!@#$%^&*()", 1);

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            verify(userService, times(1)).register(any(User.class));
        }

        @Test
        @DisplayName("角色传null时使用默认值1")
        void testRegisterWithNullRole() throws Exception {
            // given
            RegisterRequest request = buildRequest("null_role_user", "password123", null);

            when(userService.register(any(User.class))).thenReturn(1);

            // when & then
            performRegisterAndExpectSuccess(request);

            verify(userService, times(1)).register(argThat(user ->
                    user.getRole() == 1
            ));
        }

        @Test
        @DisplayName("服务层抛出通用异常")
        void testRegisterWithServiceException() throws Exception {
            // given
            RegisterRequest request = buildRequest("error_user", "password123", 1);
            doThrow(new RuntimeException("数据库异常"))
                    .when(userService).register(any(User.class));

            // when & then
            performRegisterAndExpectError(request, "数据库异常");

            verify(userService, times(1)).register(any(User.class));
        }
    }
}
