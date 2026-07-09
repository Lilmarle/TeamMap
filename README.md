# TeamMap 项目说明文档

## 项目概述

`teammap` 是一个基于 Spring Boot 后端与 Vue 3 前端的全栈项目。后端提供用户相关 API、数据库访问、文件上传和 Redis 配置；前端使用 Vite + Vue 3 + Pinia + Element Plus 实现登录、注册、首页等功能。

## 目录结构

- `pom.xml`：后端 Maven 配置
- `src/main/java`：后端源码
- `src/main/resources`：Spring Boot 资源文件
- `frontend/`：前端源码
  - `package.json`：前端依赖与脚本
  - `vite.config.js`：Vite 构建与开发配置
  - `src/`：前端入口与组件

## 开发环境要求

- Java 17
- Maven
- Node.js (推荐 18+)
- npm
- MySQL
- Redis

## 后端配置

主要配置文件：`src/main/resources/application.properties`

关键配置：

- `spring.datasource.url=jdbc:mysql://localhost:3306/teammap_db`
- `spring.datasource.username=root`
- `spring.datasource.password=21326`
- `spring.data.redis.host=localhost`
- `spring.data.redis.port=6379`
- `upload.path=D:/uploads`
- `upload.url=/uploads`

> 如果在其他环境使用，请根据实际情况调整数据库、Redis、上传目录等配置。

## 前端配置

`frontend/vite.config.js` 中配置了：

- 开发服务器端口：`3000`
- 代理 `'/api'` 到 `http://localhost:8080`
- 构建输出目录：`../src/main/resources/static`

前端 API 基准地址为：`/api`

## 前端功能

- Vue 3 + Vite
- Vue Router 路由导航
- Pinia 作为状态管理
- Element Plus UI 组件
- Axios 封装 HTTP 请求
- 用户登录、注册、获取当前用户、更新用户、删除用户

API 示例：

- `POST /api/user/login`
- `POST /api/user/register`
- `GET /api/user/current`
- `GET /api/user/{id}`
- `PUT /api/user/update`
- `DELETE /api/user/{id}`

## 运行方式

### 启动后端

在项目根目录执行：

```bash
./mvnw spring-boot:run
```

或在 Windows 下：

```powershell
.\\mvnw.cmd spring-boot:run
```

默认监听端口：`8080`

### 启动前端

进入 `frontend` 目录后执行：

```bash
cd frontend
npm install
npm run dev
```

前端开发服务默认地址：`http://localhost:3000`

## 打包部署

### 前端构建

进入 `frontend` 目录执行：

```bash
npm run build
```

构建产物会输出到：`src/main/resources/static`

### 后端打包

在项目根目录执行：

```bash
./mvnw clean package
```

生成 `war` 包：`target/teammap-0.0.1-SNAPSHOT.war`

## 备注

- 后端入口类：`src/main/java/com/marler/teammap/TeammapApplication.java`
- 前端入口：`frontend/src/main.js`
- 如果要使用后端静态部署，无需单独运行前端开发服务，直接构建后端即可提供静态页面。
- 目前后端 `UserController` 仅定义了 `/api/user` 基础路径，具体业务实现可能在后续模块中补充。
