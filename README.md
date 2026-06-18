# CodeVault - 在线代码片段管理系统

基于 **Spring Boot 3.2 + MyBatis + MySQL + Redis** 独立设计与开发的后端服务，提供用户认证、代码片段 CRUD、搜索分页、点赞收藏、分类标签管理等核心功能，支持 Docker 容器化部署。

---

## 技术栈

| 技术 | 版本 | 作用 |
|---|---|---|
| Spring Boot | 3.2.5 | 后端主框架，原生适配 JDK 17 |
| MyBatis | 3.0.3 | ORM 框架，XML 映射管理复杂 SQL |
| MySQL | 8.0 | 关系型数据库，7 张表含多对多关联 |
| Redis | 7 | 热门片段缓存，30 分钟 TTL |
| JWT | 0.9.1 | 无状态身份认证 |
| Docker + Docker Compose | - | 一键编排 MySQL + Redis + App |
| Lombok | 1.18.30 | 简化实体类开发 |

---

## 核心功能

- **用户模块**：注册/登录，JWT 鉴权，BCrypt 密码加密
- **代码片段管理**：创建/编辑/删除/逻辑删除，支持公开/私有设置
- **搜索与筛选**：关键词模糊搜索（title + description）、分类筛选、编程语言筛选，MySQL 索引优化
- **热门排行**：按浏览量降序，Redis 缓存热门列表，写操作自动清除缓存保证一致性
- **互动功能**：点赞/取消点赞、收藏/取消收藏、我的收藏列表（分页）
- **分类标签**：多对多关联，创建片段时自动查找或创建标签
- **权限控制**：JWT 拦截器区分公开/私有接口，用户只能操作自己的数据

---

## 技术亮点

### 1. 分层架构与统一规范
Controller-Service-Mapper 三层分离，统一响应封装 `Result<T>`，全局异常处理 `GlobalExceptionHandler`，业务异常与系统异常分层处理。

### 2. JWT 无状态认证
自定义 `JwtInterceptor` 拦截器，从 `Authorization: Bearer <token>` 头中提取并验证 JWT，有效则将 `userId` 注入 request attribute，供后续业务层使用。公开接口（`/api/snippet/public/**`）白名单放行。

### 3. Redis 缓存策略
- 缓存 key：`hot_snippets`，缓存前 10 条热门片段
- TTL：30 分钟，缓存未命中自动从数据库刷新
- 数据一致性：片段创建/更新/删除后主动调用 `clearCache()` 清除缓存

### 4. 数据库设计与优化
- 7 张表：用户、分类、标签、代码片段、片段-标签关联、点赞、收藏
- 代码片段表建立 4 个索引：`user_id`、`category_id`、`language`、`create_time`
- 搜索使用 `LIKE '%keyword%'` 匹配 title 和 description
- 点赞/收藏使用 `GREATEST(count - 1, 0)` 防止计数为负

### 5. 事务管理
片段创建/更新涉及多张表操作（snippet + snippet_tag），使用 `@Transactional(rollbackFor = Exception.class)` 保证原子性。

### 6. Docker 容器化
Dockerfile 打包应用镜像，docker-compose.yml 编排 MySQL 8.0 + Redis 7 + App，实现一键启动。

---

## API 概览

| 模块 | 接口数量 | 典型接口 |
|---|---|---|
| 用户 | 2 | 注册、登录（返回 JWT） |
| 代码片段 | 6 | 公开搜索分页、热门排行、CRUD |
| 分类 | 4 | 查询、增删改（管理员） |
| 标签 | 2 | 查询、创建 |
| 互动 | 5 | 点赞、收藏、取消、我的收藏 |

总计 **19 个 RESTful API**，全部通过 Postman 自测验证。

---

## 数据库关系

```
user (1) ----< snippet (N) ----> category (1)
              |
              |----< snippet_tag >---- tag (N)
              |
              |----< like >---- user (N)
              |
              |----< collection >---- user (N)
```

---

## 项目结构

```
code-vault/
├── sql/init.sql                    # 数据库建表脚本 + 初始数据
├── docker/
│   ├── Dockerfile                  # 应用镜像
│   └── docker-compose.yml          # 一键编排
├── src/main/java/com/codevault/
│   ├── controller/                 # 5 个控制器
│   ├── service/                    # 4 个服务接口 + 实现
│   ├── mapper/                     # 6 个 MyBatis Mapper
│   ├── entity/                     # 6 个实体类
│   ├── dto/                        # 3 个请求参数 DTO
│   ├── config/                     # Web/Redis/缓存配置
│   ├── interceptor/                # JWT 认证拦截器
│   ├── common/                     # 统一响应 + 全局异常
│   └── utils/                      # JWT 工具类
└── src/main/resources/mapper/      # 6 个 MyBatis XML 映射文件
```

---

## 本地启动

```bash
# 1. 创建数据库并执行 sql/init.sql
# 2. 修改 application.yml 中的数据库密码
# 3. 启动 Redis
# 4. 编译运行
mvn compile
mvn spring-boot:run

# 或 Docker 一键启动
cd docker
docker-compose up -d
```
