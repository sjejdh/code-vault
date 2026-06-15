# CodeVault - 在线代码片段管理系统

## 项目简介

CodeVault 是一个基于 Spring Boot 的在线代码片段管理系统，支持代码片段的创建、编辑、删除、搜索、分类管理、标签管理、点赞收藏等功能。系统采用前后端分离架构，后端提供 RESTful API，使用 Redis 缓存热门数据提升访问性能。

## 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot 2.7 | 后端主框架 |
| MyBatis | ORM框架，XML映射文件 |
| MySQL 8.0 | 关系型数据库 |
| Redis 7 | 缓存中间件（热门片段缓存） |
| JWT | 无状态身份认证 |
| Lombok | 简化Java实体类 |
| Docker | 容器化部署 |

## 功能特性

- **用户模块**：注册、登录、JWT鉴权
- **代码片段管理**：创建、编辑、删除、逻辑删除
- **公开浏览**：公开片段的分页查询、关键词搜索、分类筛选、语言筛选
- **热门排行**：按浏览量降序展示热门片段，Redis缓存加速
- **互动功能**：点赞、取消点赞、收藏、取消收藏、我的收藏列表
- **分类管理**：分类的增删改查
- **标签管理**：标签查询和创建，片段与标签多对多关联
- **安全控制**：JWT拦截器认证、用户只能操作自己的片段

## 项目结构

```
code-vault/
├── sql/
│   └── init.sql                      # 数据库初始化脚本
├── docker/
│   ├── Dockerfile                    # 应用镜像构建文件
│   └── docker-compose.yml            # Docker编排文件
├── src/main/java/com/codevault/
│   ├── CodeVaultApplication.java    # 启动类
│   ├── common/
│   │   ├── exception/
│   │   │   ├── BusinessException.java        # 业务异常
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   └── result/
│   │       └── Result.java           # 统一响应结果封装
│   ├── config/
│   │   ├── WebConfig.java            # Web配置（拦截器注册）
│   │   ├── RedisConfig.java         # Redis序列化配置
│   │   └── HotSnippetCache.java     # 热门片段缓存管理
│   ├── controller/
│   │   ├── UserController.java       # 用户接口
│   │   ├── SnippetController.java    # 代码片段接口
│   │   ├── CategoryController.java   # 分类接口
│   │   ├── TagController.java        # 标签接口
│   │   └── InteractionController.java # 互动接口（点赞/收藏）
│   ├── dto/
│   │   ├── LoginDTO.java             # 登录请求参数
│   │   ├── RegisterDTO.java          # 注册请求参数
│   │   └── SnippetDTO.java           # 代码片段请求参数
│   ├── entity/
│   │   ├── User.java                 # 用户实体
│   │   ├── Snippet.java              # 代码片段实体
│   │   ├── Category.java             # 分类实体
│   │   ├── Tag.java                  # 标签实体
│   │   ├── Like.java                 # 点赞实体
│   │   └── Collection.java           # 收藏实体
│   ├── interceptor/
│   │   └── JwtInterceptor.java       # JWT认证拦截器
│   ├── mapper/
│   │   ├── UserMapper.java           # 用户Mapper
│   │   ├── SnippetMapper.java       # 片段Mapper
│   │   ├── CategoryMapper.java      # 分类Mapper
│   │   ├── TagMapper.java           # 标签Mapper
│   │   ├── LikeMapper.java          # 点赞Mapper
│   │   └── CollectionMapper.java     # 收藏Mapper
│   ├── service/
│   │   ├── UserService.java          # 用户服务接口
│   │   ├── SnippetService.java       # 片段服务接口
│   │   ├── CategoryService.java      # 分类服务接口
│   │   ├── TagService.java           # 标签服务接口
│   │   └── impl/
│   │       ├── UserServiceImpl.java
│   │       ├── SnippetServiceImpl.java
│   │       ├── CategoryServiceImpl.java
│   │       └── TagServiceImpl.java
│   └── utils/
│       └── JwtUtils.java             # JWT工具类
└── src/main/resources/
    ├── application.yml               # 应用配置文件
    └── mapper/                       # MyBatis XML映射文件
        ├── UserMapper.xml
        ├── SnippetMapper.xml
        ├── CategoryMapper.xml
        ├── TagMapper.xml
        ├── LikeMapper.xml
        └── CollectionMapper.xml
```

## 环境要求

- **JDK**: 8+
- **Maven**: 3.6+
- **MySQL**: 8.0
- **Redis**: 7.0+
- **Docker**（可选，用于容器化部署）

## 本地启动步骤

### 1. 初始化数据库

创建数据库并执行初始化脚本：

```sql
CREATE DATABASE code_vault DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE code_vault;
-- 执行 sql/init.sql 脚本
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，配置数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/code_vault?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root        # 修改为你的数据库密码
  redis:
    host: localhost
    port: 6379
```

### 3. 启动Redis

确保本地Redis服务已启动：

```bash
redis-server
```

### 4. 编译并运行

```bash
mvn clean package -DskipTests
java -jar target/code-vault-1.0.0.jar
```

### 5. 验证启动

访问 `http://localhost:8080/api/snippet/public` 验证接口是否正常。

## Docker一键部署

### 1. 打包项目

```bash
mvn clean package -DskipTests
```

### 2. 使用docker-compose启动

```bash
cd docker
docker-compose up -d
```

该命令会自动启动以下三个服务：

| 服务 | 说明 | 端口 |
|------|------|------|
| mysql | MySQL 8.0 数据库，自动执行init.sql初始化 | 3306 |
| redis | Redis 7 缓存服务 | 6379 |
| app | CodeVault 应用服务 | 8080 |

### 3. 验证服务

```bash
# 检查所有容器状态
docker-compose ps

# 测试接口
curl http://localhost:8080/api/snippet/public
```

### 4. 停止服务

```bash
docker-compose down

# 停止并清除数据卷
docker-compose down -v
```

## API接口列表

### 用户模块 `/api/user`

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录，返回JWT Token | 否 |

### 代码片段模块 `/api/snippet`

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| GET | `/api/snippet/public` | 分页查询公开片段（支持搜索、分类、语言筛选） | 否 |
| GET | `/api/snippet/public/hot` | 查询热门片段（按浏览量降序） | 否 |
| GET | `/api/snippet/public/{id}` | 获取公开片段详情（自动增加浏览量） | 否 |
| GET | `/api/snippet/my` | 查询我的代码片段（分页） | 是 |
| POST | `/api/snippet` | 创建代码片段 | 是 |
| PUT | `/api/snippet/{id}` | 更新代码片段（只能更新自己的） | 是 |
| DELETE | `/api/snippet/{id}` | 删除代码片段（逻辑删除，只能删除自己的） | 是 |

### 分类模块 `/api/category`

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| GET | `/api/category` | 获取所有分类 | 否 |
| POST | `/api/category` | 创建分类 | 是 |
| PUT | `/api/category/{id}` | 更新分类 | 是 |
| DELETE | `/api/category/{id}` | 删除分类 | 是 |

### 标签模块 `/api/tag`

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| GET | `/api/tag` | 获取所有标签 | 否 |
| POST | `/api/tag` | 创建标签 | 是 |

### 互动模块 `/api/interaction`

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| POST | `/api/interaction/like/{snippetId}` | 点赞代码片段 | 是 |
| DELETE | `/api/interaction/like/{snippetId}` | 取消点赞 | 是 |
| POST | `/api/interaction/collect/{snippetId}` | 收藏代码片段 | 是 |
| DELETE | `/api/interaction/collect/{snippetId}` | 取消收藏 | 是 |
| GET | `/api/interaction/collect/my` | 查询我的收藏列表（分页） | 是 |

### 请求头说明

需要登录的接口，请在请求头中携带JWT Token：

```
Authorization: Bearer <your_token>
```

## 简历中可以怎么写这个项目

### 项目描述

> CodeVault - 在线代码片段管理系统
>
> 基于 Spring Boot + MyBatis + MySQL + Redis 构建的代码片段管理平台，支持片段的 CRUD、搜索、分类标签管理、点赞收藏等功能，使用 Redis 缓存热门数据，支持 Docker 容器化部署。

### 技术亮点（可根据实际掌握情况选择）

1. **Spring Boot + MyBatis**：使用 Spring Boot 构建 RESTful API，MyBatis 作为 ORM 框架，XML 映射文件管理复杂 SQL
2. **JWT 无状态认证**：自定义 JWT 拦截器实现用户身份认证，区分公开接口与需认证接口
3. **Redis 缓存优化**：热门片段数据使用 Redis 缓存，设置 30 分钟过期时间，缓存未命中时自动回源数据库，创建/更新/删除操作时主动清除缓存保证数据一致性
4. **事务管理**：使用 `@Transactional` 保证片段创建、更新、删除时数据操作的事务一致性
5. **Docker 容器化部署**：编写 Dockerfile 和 docker-compose.yml，实现 MySQL + Redis + 应用服务一键编排部署
6. **统一响应与异常处理**：封装统一响应结果 `Result`，使用全局异常处理器统一处理业务异常
7. **参数校验**：使用 `@Validated` 注解实现接口入参自动校验
