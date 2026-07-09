# TeamMap 前端文档

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | ^3.4.0 | 响应式 UI 框架（Composition API + `<script setup>`） |
| Vue Router 4 | ^4.2.0 | 客户端路由（懒加载） |
| Pinia | ^2.1.0 | 状态管理 |
| Axios | ^1.6.0 | HTTP 客户端 |
| Element Plus | ^2.4.0 | UI 组件库 |
| Vite 5 | ^5.0.0 | 构建工具和开发服务器 |

## 目录结构

```
frontend/
├── index.html              # 入口 HTML
├── package.json            # 依赖与脚本
├── vite.config.js          # Vite 配置（别名、代理、构建输出）
├── public/                 # 公共静态资源
└── src/
    ├── main.js             # 应用入口（挂载 Vue + Pinia + Router + Element Plus）
    ├── App.vue             # 根组件（仅包含 <router-view>）
    ├── api/
    │   ├── request.js      # Axios 实例（请求/响应拦截器）
    │   └── user.js         # 用户相关 API
    ├── router/
    │   └── index.js        # 路由配置
    ├── store/
    │   └── user.js         # 用户认证状态管理
    ├── utils/
    │   └── validate.js     # 校验工具（手机号、邮箱、URL）
    └── views/
        ├── Home.vue        # 首页
        ├── Login.vue       # 登录页
        └── Register.vue    # 注册页
```

## 开发环境

### 前置要求

- Node.js >= 18
- npm

### 安装与运行

```bash
cd frontend
npm install
npm run dev        # 启动开发服务器（默认 :3000）
npm run build      # 构建生产版本
npm run preview    # 预览生产构建
```

开发服务器通过 Vite 代理将 `/api` 请求转发到后端 `http://localhost:8080`。

### 构建输出

构建产物输出到 `../src/main/resources/static`，由 Spring Boot 在打包后直接托管。

## 功能模块

### 1. 用户认证

| 路由 | 视图 | 说明 |
|------|------|------|
| `/` | Home.vue | 首页，包含欢迎信息和登录/注册入口 |
| `/login` | Login.vue | 登录表单（用户名 + 密码） |
| `/register` | Register.vue | 注册表单（用户名 + 密码 + 确认密码 + 邮箱） |

### 2. 状态管理（Pinia）

`useUserStore` 管理用户认证状态：

- `user` — 当前用户信息
- `token` — JWT 令牌（持久化到 localStorage）
- `login(data)` — 登录并保存 token
- `register(data)` — 注册新用户
- `getCurrentUser()` — 获取当前登录用户信息
- `logout()` — 清除登录状态

## API 接口

基础路径：`/api`（Axios 配置）

| 方法 | 路径 | 说明 | 前端调用 |
|------|------|------|----------|
| POST | `/api/user/login` | 用户登录 | `userApi.login(data)` |
| POST | `/api/user/register` | 用户注册 | `userApi.register(data)` |
| GET | `/api/user/current` | 获取当前用户 | `userApi.getCurrentUser()` |
| GET | `/api/user/{id}` | 按 ID 获取用户 | `userApi.getUserById(id)` |
| PUT | `/api/user/update` | 更新用户信息 | `userApi.updateUser(data)` |
| DELETE | `/api/user/{id}` | 删除用户 | `userApi.deleteUser(id)` |

### 响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

`code !== 200` 时在响应拦截器中被拦截并通过 `ElMessage` 显示错误提示。

### 认证方式

JWT Bearer Token 存储在 `localStorage` 的 `token` 字段中，请求拦截器自动附加 `Authorization: Bearer <token>` 头。

## 路由守卫

全局前置守卫 `router.beforeEach` 自动更新页面标题为 `页面名 - TeamMap`。

## 校验规则

`src/utils/validate.js` 提供通用正则校验：

- `isMobile(value)` — 中国大陆手机号
- `isEmail(value)` — 邮箱格式
- `isUrl(value)` — URL 格式

表单校验在 Login.vue 和 Register.vue 中通过 Element Plus 的 `el-form` 规则实现（必填、长度限制、密码一致性、邮箱格式）。

## 开发规范

- 使用 Vue 3 Composition API + `<script setup>` 语法
- 组件样式使用 `<style scoped>` 避免污染
- API 调用封装在 `src/api/` 目录下
- 状态管理集中到 `src/store/` 目录
- 通用工具函数放在 `src/utils/`

## 扩展建议

- `src/components/` 目录可用于放置可复用的公共组件
- `src/styles/` 目录可放置全局样式变量和混入
- `src/assets/` 目录可放置图片、字体等静态资源
- 新功能页面建议在 `src/views/` 下新建对应目录，按业务模块组织
