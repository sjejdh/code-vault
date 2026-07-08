-- ============================================
-- CodeVault 在线代码片段管理系统 数据库初始化脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS code_vault
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE code_vault;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL                COMMENT '密码（BCrypt加密）',
    `nickname`    VARCHAR(50)  DEFAULT ''              COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT ''              COMMENT '头像URL',
    `email`       VARCHAR(100) DEFAULT ''              COMMENT '邮箱',
    `role`        VARCHAR(20)  DEFAULT 'USER'          COMMENT '角色: USER / ADMIN',
    `status`      TINYINT      DEFAULT 1               COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 分类表
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50)  NOT NULL                COMMENT '分类名称',
    `icon`        VARCHAR(50)  DEFAULT ''              COMMENT '分类图标',
    `sort_order`  INT          DEFAULT 0               COMMENT '排序值（越小越靠前）',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ----------------------------
-- 3. 标签表
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name`        VARCHAR(30)  NOT NULL                COMMENT '标签名称',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- 4. 代码片段表
-- ----------------------------
DROP TABLE IF EXISTS `snippet`;
CREATE TABLE `snippet` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '片段ID',
    `user_id`       BIGINT        NOT NULL               COMMENT '作者ID',
    `title`         VARCHAR(100)  NOT NULL               COMMENT '标题',
    `description`   VARCHAR(500) DEFAULT ''             COMMENT '描述',
    `content`       TEXT          NOT NULL               COMMENT '代码内容',
    `language`      VARCHAR(30)   NOT NULL               COMMENT '编程语言: Java/Python/JavaScript等',
    `category_id`   BIGINT        DEFAULT NULL           COMMENT '分类ID',
    `view_count`    INT           DEFAULT 0              COMMENT '浏览次数',
    `like_count`    INT           DEFAULT 0              COMMENT '点赞次数',
    `collect_count` INT           DEFAULT 0              COMMENT '收藏次数',
    `is_public`     TINYINT       DEFAULT 1              COMMENT '是否公开: 0-私有, 1-公开',
    `status`        TINYINT       DEFAULT 1              COMMENT '状态: 0-删除, 1-正常',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_language` (`language`),
    KEY `idx_create_time` (`create_time`),
    FULLTEXT KEY `ft_title_desc` (`title`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码片段表';

-- ----------------------------
-- 5. 代码片段-标签关联表
-- ----------------------------
DROP TABLE IF EXISTS `snippet_tag`;
CREATE TABLE `snippet_tag` (
    `snippet_id`  BIGINT NOT NULL COMMENT '片段ID',
    `tag_id`      BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`snippet_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码片段-标签关联表';

-- ----------------------------
-- 6. 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '用户ID',
    `snippet_id`  BIGINT   NOT NULL               COMMENT '片段ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_snippet` (`user_id`, `snippet_id`),
    KEY `idx_snippet_id` (`snippet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 7. 点赞表
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '用户ID',
    `snippet_id`  BIGINT   NOT NULL               COMMENT '片段ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_snippet` (`user_id`, `snippet_id`),
    KEY `idx_snippet_id` (`snippet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ============================================
-- 初始数据：预设分类和标签
-- ============================================

-- 预设分类
INSERT INTO `category` (`name`, `icon`, `sort_order`) VALUES
('后端开发', 'server', 1),
('前端开发', 'browser', 2),
('数据库', 'database', 3),
('算法', 'algorithm', 4),
('DevOps', 'ops', 5),
('工具脚本', 'tool', 6);

-- 预设标签
INSERT INTO `tag` (`name`) VALUES
('Spring Boot'), ('MyBatis'), ('Vue'), ('React'), ('MySQL'),
('Redis'), ('Docker'), ('Python'), ('JavaScript'), ('Java'),
('API'), ('排序'), ('动态规划'), ('Linux'), ('Git');

-- 注意：初始部署后请通过注册接口创建管理员账号
-- 不在初始化脚本中预设管理员密码，避免弱密码风险
