/*
 Navicat Premium Data Transfer

 Source Server         : 海外测试
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Schema         : ddd_auth

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 29/11/2025 17:49:00
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_auth` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_auth`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for content_article
-- ----------------------------
DROP TABLE IF EXISTS `content_article`;
CREATE TABLE `content_article`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `user_id` bigint NOT NULL COMMENT '作者ID',
                                    `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
                                    `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
                                    `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
                                    `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容（Markdown/HTML）',
                                    `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MARKDOWN' COMMENT '内容类型：MARKDOWN- Markdown，RICH_TEXT-富文本',
                                    `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
                                    `view_count` int NULL DEFAULT 0 COMMENT '阅读数',
                                    `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                    `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
                                    `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
                                    `share_count` int NULL DEFAULT 0 COMMENT '分享数',
                                    `publish_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DRAFT' COMMENT '发布状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下架',
                                    `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
                                    `access_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PUBLIC' COMMENT '访问级别：PUBLIC-公开，PRIVATE-私有，PAID-付费可见，SPECIFIED-指定用户可见',
                                    `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
                                    `is_recommend` tinyint NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
                                    `is_hot` tinyint NULL DEFAULT 0 COMMENT '是否热门：0-否，1-是',
                                    `version` int NULL DEFAULT 1 COMMENT '版本号，用于历史版本回溯',
                                    `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                    INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
                                    INDEX `idx_publish_status`(`publish_status` ASC) USING BTREE,
                                    INDEX `idx_publish_time`(`publish_time` DESC) USING BTREE,
                                    INDEX `idx_view_count`(`view_count` DESC) USING BTREE,
                                    FULLTEXT INDEX `ft_title_summary`(`title`, `summary`) COMMENT '全文索引'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_article
-- ----------------------------
INSERT INTO `content_article` VALUES (1, 1, 'Vue 3 Composition API 完全指南', '深入理解 Vue 3 的 Composition API，掌握响应式系统的核心原理', NULL, '# Vue 3 Composition API 完全指南\n\nVue 3 引入了 Composition API，这是一个全新的 API 设计，提供了更好的逻辑复用和代码组织方式。\n\n## 什么是 Composition API\n\nComposition API 是一组基于函数的 API，允许你更灵活地组织组件代码。\n\n## 核心概念\n\n### ref 和 reactive\n\n- `ref`: 用于创建响应式的原始值\n- `reactive`: 用于创建响应式的对象\n\n### computed 和 watch\n\n- `computed`: 计算属性\n- `watch`: 监听器\n\n## 最佳实践\n\n1. 使用 Composition API 组织逻辑\n2. 提取可复用的逻辑到 composables\n3. 保持代码的清晰和可维护性', 'MARKDOWN', 1, 1250, 89, 23, 45, 12, 'PUBLISHED', '2025-11-29 12:44:31', 'PUBLIC', 1, 1, 1, 1, 1, '2025-11-29 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (2, 1, 'Spring Boot 微服务实践', '从零开始构建 Spring Boot 微服务应用，包含服务注册、配置中心、网关等', NULL, '# Spring Boot 微服务实践\n\n微服务架构是现代应用开发的主流模式，Spring Boot 提供了完善的微服务支持。\n\n## 微服务架构设计\n\n### 服务拆分原则\n\n1. 单一职责原则\n2. 高内聚低耦合\n3. 数据独立\n\n### 服务通信\n\n- RESTful API\n- 消息队列\n- gRPC\n\n## Spring Cloud 组件\n\n- Eureka/Nacos: 服务注册与发现\n- Config: 配置中心\n- Gateway: API 网关\n- Feign: 服务调用', 'MARKDOWN', 2, 980, 67, 18, 32, 8, 'PUBLISHED', '2025-11-28 12:44:31', 'PUBLIC', 0, 1, 1, 1, 1, '2025-11-28 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (3, 2, 'React Hooks 深入理解', '全面解析 React Hooks 的使用场景和最佳实践', NULL, '# React Hooks 深入理解\n\nReact Hooks 是 React 16.8 引入的新特性，它允许你在函数组件中使用状态和其他 React 特性。\n\n## 常用 Hooks\n\n### useState\n\n用于在函数组件中添加状态。\n\n### useEffect\n\n用于处理副作用，如数据获取、订阅等。\n\n### useContext\n\n用于在组件树中共享数据。\n\n## 自定义 Hooks\n\n可以创建自定义 Hooks 来复用逻辑。', 'MARKDOWN', 1, 756, 45, 12, 20, 5, 'PUBLISHED', '2025-11-27 12:44:31', 'PUBLIC', 0, 0, 0, 1, 1, '2025-11-27 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (4, 1, 'Docker 容器化部署指南', '使用 Docker 容器化应用，实现快速部署和扩展', NULL, '# Docker 容器化部署指南\n\nDocker 是一个开源的容器化平台，可以帮助我们快速构建、部署和运行应用。\n\n## Docker 基础\n\n### 镜像和容器\n\n- 镜像：应用的模板\n- 容器：镜像的运行实例\n\n### Dockerfile\n\n用于构建镜像的配置文件。\n\n## 最佳实践\n\n1. 使用多阶段构建\n2. 优化镜像大小\n3. 合理使用缓存\n\n## 部署流程\n\n1. 编写 Dockerfile\n2. 构建镜像\n3. 运行容器', 'MARKDOWN', 2, 542, 32, 8, 15, 3, 'PUBLISHED', '2025-11-26 12:44:31', 'PUBLIC', 0, 0, 0, 1, 1, '2025-11-26 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (5, 2, 'TypeScript 类型系统详解', '深入理解 TypeScript 的类型系统，提升代码质量', NULL, '# TypeScript 类型系统详解\n\nTypeScript 是 JavaScript 的超集，添加了静态类型检查。\n\n## 基础类型\n\n- number\n- string\n- boolean\n- array\n- object\n\n## 高级类型\n\n- 联合类型\n- 交叉类型\n- 泛型\n- 条件类型\n\n## 类型推断\n\nTypeScript 可以自动推断类型，减少类型注解。', 'MARKDOWN', 1, 432, 28, 6, 12, 2, 'PUBLISHED', '2025-11-25 12:44:31', 'PUBLIC', 0, 0, 0, 1, 1, '2025-11-25 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (6, 1, 'Redis 缓存策略与实践', 'Redis 在微服务架构中的应用和最佳实践', NULL, '# Redis 缓存策略与实践\n\nRedis 是一个高性能的内存数据库，广泛应用于缓存、消息队列等场景。\n\n## 缓存策略\n\n### 缓存穿透\n\n解决缓存穿透的方法：\n1. 布隆过滤器\n2. 缓存空值\n\n### 缓存击穿\n\n解决缓存击穿的方法：\n1. 互斥锁\n2. 热点数据永不过期\n\n### 缓存雪崩\n\n解决缓存雪崩的方法：\n1. 过期时间随机化\n2. 多级缓存\n\n## 实践建议\n\n1. 合理设置过期时间\n2. 监控缓存命中率\n3. 使用合适的序列化方式', 'MARKDOWN', 2, 389, 24, 5, 10, 1, 'PUBLISHED', '2025-11-24 12:44:31', 'PUBLIC', 0, 0, 0, 1, 1, '2025-11-24 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (7, 2, '前端性能优化实战', '前端性能优化的各种技巧和工具', NULL, '# 前端性能优化实战\n\n前端性能优化是提升用户体验的关键。\n\n## 优化策略\n\n### 资源加载优化\n\n1. 代码分割\n2. 懒加载\n3. 预加载\n\n### 渲染优化\n\n1. 虚拟滚动\n2. 防抖节流\n3. 使用 Web Workers\n\n### 网络优化\n\n1. HTTP/2\n2. CDN\n3. 压缩资源\n\n## 工具推荐\n\n- Lighthouse\n- WebPageTest\n- Chrome DevTools', 'MARKDOWN', 1, 321, 19, 4, 8, 1, 'PUBLISHED', '2025-11-23 12:44:31', 'PUBLIC', 0, 0, 0, 1, 1, '2025-11-23 12:44:31', '2025-11-29 12:44:31', 0);
INSERT INTO `content_article` VALUES (8, 1, 'MySQL 索引优化技巧', '深入理解 MySQL 索引原理，提升查询性能', NULL, '# MySQL 索引优化技巧\n\n索引是数据库性能优化的关键。\n\n## 索引类型\n\n- B-Tree 索引\n- 哈希索引\n- 全文索引\n\n## 索引设计原则\n\n1. 选择性高的列\n2. 经常用于 WHERE 的列\n3. 避免过多索引\n\n## 优化技巧\n\n1. 使用 EXPLAIN 分析\n2. 避免索引失效\n3. 合理使用覆盖索引', 'MARKDOWN', 2, 278, 16, 3, 6, 0, 'PUBLISHED', '2025-11-22 12:44:31', 'PUBLIC', 0, 0, 0, 1, 1, '2025-11-22 12:44:31', '2025-11-29 12:44:31', 0);

-- ----------------------------
-- Table structure for content_article_collect
-- ----------------------------
DROP TABLE IF EXISTS `content_article_collect`;
CREATE TABLE `content_article_collect`  (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `user_id` bigint NOT NULL COMMENT '用户ID',
                                            `article_id` bigint NOT NULL COMMENT '文章ID',
                                            `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            UNIQUE INDEX `uk_user_article`(`user_id` ASC, `article_id` ASC) USING BTREE,
                                            INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_article_collect
-- ----------------------------

-- ----------------------------
-- Table structure for content_article_like
-- ----------------------------
DROP TABLE IF EXISTS `content_article_like`;
CREATE TABLE `content_article_like`  (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `user_id` bigint NOT NULL COMMENT '用户ID',
                                         `article_id` bigint NOT NULL COMMENT '文章ID',
                                         `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE INDEX `uk_user_article`(`user_id` ASC, `article_id` ASC) USING BTREE,
                                         INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_article_like
-- ----------------------------

-- ----------------------------
-- Table structure for content_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `content_article_tag`;
CREATE TABLE `content_article_tag`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `article_id` bigint NOT NULL COMMENT '文章ID',
                                        `tag_id` bigint NOT NULL COMMENT '标签ID',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_article_tag`(`article_id` ASC, `tag_id` ASC) USING BTREE,
                                        INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
                                        INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_article_tag
-- ----------------------------
INSERT INTO `content_article_tag` VALUES (1, 1, 1, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (2, 1, 2, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (3, 2, 3, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (4, 2, 5, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (5, 2, 6, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (6, 3, 2, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (7, 4, 7, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (8, 5, 1, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (9, 6, 3, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (10, 7, 1, '2025-11-29 12:44:31');
INSERT INTO `content_article_tag` VALUES (11, 8, 3, '2025-11-29 12:44:31');

-- ----------------------------
-- Table structure for content_article_version
-- ----------------------------
DROP TABLE IF EXISTS `content_article_version`;
CREATE TABLE `content_article_version`  (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `article_id` bigint NOT NULL COMMENT '文章ID',
                                            `version` int NOT NULL COMMENT '版本号',
                                            `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
                                            `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
                                            `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
                                            `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建版本的用户ID',
                                            `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
                                            INDEX `idx_version`(`version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章历史版本表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_article_version
-- ----------------------------

-- ----------------------------
-- Table structure for content_boiling_point
-- ----------------------------
DROP TABLE IF EXISTS `content_boiling_point`;
CREATE TABLE `content_boiling_point`  (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `user_id` bigint NOT NULL COMMENT '发布用户ID',
                                          `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '沸点内容',
                                          `image_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URLs，JSON数组格式',
                                          `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                          `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
                                          `share_count` int NULL DEFAULT 0 COMMENT '分享数',
                                          `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
                                          `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                          `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                          INDEX `idx_create_time`(`create_time` DESC) USING BTREE,
                                          FULLTEXT INDEX `ft_content`(`content`) COMMENT '全文索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '沸点表（短内容）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_boiling_point
-- ----------------------------

-- ----------------------------
-- Table structure for content_category
-- ----------------------------
DROP TABLE IF EXISTS `content_category`;
CREATE TABLE `content_category`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `parent_id` bigint NULL DEFAULT NULL COMMENT '父分类ID，NULL或0表示顶级分类',
                                     `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码',
                                     `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
                                     `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
                                     `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
                                     `sort` int NULL DEFAULT 0 COMMENT '排序号',
                                     `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                     `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `uk_category_code`(`category_code` ASC) USING BTREE,
                                     INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_category
-- ----------------------------
INSERT INTO `content_category` VALUES (1, NULL, 'FRONTEND', '前端', 'Frontend', '前端开发相关', 1, 1, '2025-11-29 10:15:05', '2025-11-29 10:15:05', 0);
INSERT INTO `content_category` VALUES (2, NULL, 'BACKEND', '后端', 'Backend', '后端开发相关', 2, 1, '2025-11-29 10:15:05', '2025-11-29 10:15:05', 0);
INSERT INTO `content_category` VALUES (3, NULL, 'MOBILE', '移动端', 'Mobile', '移动端开发相关', 3, 1, '2025-11-29 10:15:05', '2025-11-29 10:15:05', 0);
INSERT INTO `content_category` VALUES (4, NULL, 'AI', '人工智能', 'AI', 'AI相关', 4, 1, '2025-11-29 10:15:05', '2025-11-29 10:15:05', 0);

-- ----------------------------
-- Table structure for content_comment
-- ----------------------------
DROP TABLE IF EXISTS `content_comment`;
CREATE TABLE `content_comment`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `user_id` bigint NOT NULL COMMENT '评论用户ID',
                                    `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标类型：ARTICLE-文章，COMMENT-评论（回复）',
                                    `target_id` bigint NOT NULL COMMENT '目标ID（文章ID或评论ID）',
                                    `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID，NULL表示顶级评论',
                                    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
                                    `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                    `reply_count` int NULL DEFAULT 0 COMMENT '回复数',
                                    `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
                                    `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
                                    `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
                                    `audit_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核原因',
                                    `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                    INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                    INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
                                    INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_comment
-- ----------------------------

-- ----------------------------
-- Table structure for content_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `content_comment_like`;
CREATE TABLE `content_comment_like`  (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `user_id` bigint NOT NULL COMMENT '用户ID',
                                         `comment_id` bigint NOT NULL COMMENT '评论ID',
                                         `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE INDEX `uk_user_comment`(`user_id` ASC, `comment_id` ASC) USING BTREE,
                                         INDEX `idx_comment_id`(`comment_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_comment_like
-- ----------------------------

-- ----------------------------
-- Table structure for content_tag
-- ----------------------------
DROP TABLE IF EXISTS `content_tag`;
CREATE TABLE `content_tag`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
                                `tag_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签编码',
                                `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签描述',
                                `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
                                `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `uk_tag_code`(`tag_code` ASC) USING BTREE,
                                INDEX `idx_tag_name`(`tag_name` ASC) USING BTREE,
                                INDEX `idx_use_count`(`use_count` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_tag
-- ----------------------------
INSERT INTO `content_tag` VALUES (1, 'Vue', 'VUE', 'Vue.js 前端框架', 10, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (2, 'React', 'REACT', 'React 前端框架', 8, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (3, 'Java', 'JAVA', 'Java 编程语言', 15, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (4, 'Python', 'PYTHON', 'Python 编程语言', 12, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (5, 'Spring Boot', 'SPRING_BOOT', 'Spring Boot 框架', 9, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (6, '微服务', 'MICROSERVICE', '微服务架构', 7, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (7, 'Docker', 'DOCKER', 'Docker 容器技术', 5, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);
INSERT INTO `content_tag` VALUES (8, 'Kubernetes', 'KUBERNETES', 'K8s 容器编排', 4, 1, '2025-11-29 12:44:30', '2025-11-29 12:44:30', 0);

-- ----------------------------
-- Table structure for content_topic
-- ----------------------------
DROP TABLE IF EXISTS `content_topic`;
CREATE TABLE `content_topic`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `topic_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专题名称',
                                  `topic_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专题编码',
                                  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
                                  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '专题描述',
                                  `article_count` int NULL DEFAULT 0 COMMENT '文章数量',
                                  `follower_count` int NULL DEFAULT 0 COMMENT '关注数',
                                  `access_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PUBLIC' COMMENT '访问级别：PUBLIC-公开，PRIVATE-私有，PAID-付费可见，SPECIFIED-指定用户可见',
                                  `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uk_topic_code`(`topic_code` ASC) USING BTREE,
                                  INDEX `idx_topic_name`(`topic_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_topic
-- ----------------------------
INSERT INTO `content_topic` VALUES (1, 'Vue.js 实战', 'VUE_PRACTICE', NULL, 'Vue.js 实战开发经验分享', 2, 120, 'PUBLIC', 1, '2025-11-29 12:44:30', '2025-11-29 12:44:31', 0);
INSERT INTO `content_topic` VALUES (2, 'Spring Boot 进阶', 'SPRING_BOOT_ADVANCED', NULL, 'Spring Boot 高级特性与最佳实践', 2, 200, 'PUBLIC', 1, '2025-11-29 12:44:30', '2025-11-29 12:44:31', 0);
INSERT INTO `content_topic` VALUES (3, '微服务架构', 'MICROSERVICE_ARCH', NULL, '微服务架构设计与实践', 2, 150, 'PUBLIC', 1, '2025-11-29 12:44:30', '2025-11-29 12:44:31', 0);
INSERT INTO `content_topic` VALUES (4, '前端工程化', 'FRONTEND_ENGINEERING', NULL, '前端工程化实践与工具链', 0, 80, 'PUBLIC', 1, '2025-11-29 12:44:30', '2025-11-29 12:44:31', 0);
INSERT INTO `content_topic` VALUES (5, '数据库优化', 'DATABASE_OPTIMIZATION', NULL, '数据库性能优化与调优技巧', 0, 100, 'PUBLIC', 1, '2025-11-29 12:44:30', '2025-11-29 12:44:31', 0);

-- ----------------------------
-- Table structure for content_topic_article
-- ----------------------------
DROP TABLE IF EXISTS `content_topic_article`;
CREATE TABLE `content_topic_article`  (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `topic_id` bigint NOT NULL COMMENT '专题ID',
                                          `article_id` bigint NOT NULL COMMENT '文章ID',
                                          `sort` int NULL DEFAULT 0 COMMENT '排序号',
                                          `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE INDEX `uk_topic_article`(`topic_id` ASC, `article_id` ASC) USING BTREE,
                                          INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE,
                                          INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专题文章关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_topic_article
-- ----------------------------
INSERT INTO `content_topic_article` VALUES (1, 1, 1, 1, '2025-11-29 12:44:31');
INSERT INTO `content_topic_article` VALUES (2, 1, 3, 2, '2025-11-29 12:44:31');
INSERT INTO `content_topic_article` VALUES (3, 2, 2, 1, '2025-11-29 12:44:31');
INSERT INTO `content_topic_article` VALUES (4, 2, 6, 2, '2025-11-29 12:44:31');
INSERT INTO `content_topic_article` VALUES (5, 3, 2, 1, '2025-11-29 12:44:31');
INSERT INTO `content_topic_article` VALUES (6, 3, 4, 2, '2025-11-29 12:44:31');

-- ----------------------------
-- Table structure for statistics_content_ranking
-- ----------------------------
DROP TABLE IF EXISTS `statistics_content_ranking`;
CREATE TABLE `statistics_content_ranking`  (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                               `stat_date` date NOT NULL COMMENT '统计日期',
                                               `content_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容类型：ARTICLE-文章，TOPIC-专题，BOILING_POINT-沸点，COURSE-课程',
                                               `content_id` bigint NOT NULL COMMENT '内容ID',
                                               `content_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容标题',
                                               `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
                                               `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                               `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
                                               `share_count` int NULL DEFAULT 0 COMMENT '分享数',
                                               `hot_score` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '热度分数（综合计算）',
                                               `ranking` int NULL DEFAULT 0 COMMENT '排名',
                                               `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               UNIQUE INDEX `uk_stat_content`(`stat_date` ASC, `content_type` ASC, `content_id` ASC) USING BTREE,
                                               INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
                                               INDEX `idx_hot_score`(`hot_score` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容排行统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of statistics_content_ranking
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_event_log
-- ----------------------------
DROP TABLE IF EXISTS `statistics_event_log`;
CREATE TABLE `statistics_event_log`  (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID，未登录用户为NULL',
                                         `event_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事件类型：VIEW-浏览，CLICK-点击，LIKE-点赞，COMMENT-评论，SHARE-分享，SEARCH-搜索等',
                                         `event_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事件分类：ARTICLE-文章，TOPIC-专题，BOILING_POINT-沸点，COURSE-课程等',
                                         `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID（文章ID、专题ID等）',
                                         `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标类型',
                                         `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
                                         `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理（浏览器信息）',
                                         `referer` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源页面',
                                         `device_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型：PC，MOBILE，TABLET',
                                         `os_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统类型',
                                         `browser_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器类型',
                                         `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话ID',
                                         `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展数据，JSON格式',
                                         `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                         INDEX `idx_event_type`(`event_type` ASC) USING BTREE,
                                         INDEX `idx_event_category`(`event_category` ASC) USING BTREE,
                                         INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                         INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
                                         INDEX `idx_session_id`(`session_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '事件日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of statistics_event_log
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_source_channel
-- ----------------------------
DROP TABLE IF EXISTS `statistics_source_channel`;
CREATE TABLE `statistics_source_channel`  (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                              `stat_date` date NOT NULL COMMENT '统计日期',
                                              `channel_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '渠道类型：NATURAL-自然流量，PAID-付费投放，SOCIAL-社交分享，SEARCH-搜索引擎，DIRECT-直接访问',
                                              `channel_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '渠道名称（如：百度、微信、微博等）',
                                              `visit_count` int NULL DEFAULT 0 COMMENT '访问次数',
                                              `user_count` int NULL DEFAULT 0 COMMENT '用户数',
                                              `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              UNIQUE INDEX `uk_stat_channel`(`stat_date` ASC, `channel_type` ASC, `channel_name` ASC) USING BTREE,
                                              INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '来源渠道统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of statistics_source_channel
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_tag_hot
-- ----------------------------
DROP TABLE IF EXISTS `statistics_tag_hot`;
CREATE TABLE `statistics_tag_hot`  (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `stat_date` date NOT NULL COMMENT '统计日期',
                                       `tag_id` bigint NOT NULL COMMENT '标签ID',
                                       `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
                                       `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
                                       `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
                                       `hot_score` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '热度分数',
                                       `ranking` int NULL DEFAULT 0 COMMENT '排名',
                                       `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `uk_stat_tag`(`stat_date` ASC, `tag_id` ASC) USING BTREE,
                                       INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
                                       INDEX `idx_hot_score`(`hot_score` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签热度统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of statistics_tag_hot
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_user_profile
-- ----------------------------
DROP TABLE IF EXISTS `statistics_user_profile`;
CREATE TABLE `statistics_user_profile`  (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `user_id` bigint NOT NULL COMMENT '用户ID',
                                            `visit_count` int NULL DEFAULT 0 COMMENT '总访问次数',
                                            `last_visit_time` datetime NULL DEFAULT NULL COMMENT '最后访问时间',
                                            `favorite_tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关注标签，JSON数组格式',
                                            `favorite_categories` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关注分类，JSON数组格式',
                                            `interaction_count` int NULL DEFAULT 0 COMMENT '互动次数（点赞+评论+分享）',
                                            `article_count` int NULL DEFAULT 0 COMMENT '发布文章数',
                                            `boiling_point_count` int NULL DEFAULT 0 COMMENT '发布沸点数',
                                            `follower_count` int NULL DEFAULT 0 COMMENT '粉丝数',
                                            `following_count` int NULL DEFAULT 0 COMMENT '关注数',
                                            `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
                                            INDEX `idx_interaction_count`(`interaction_count` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户画像统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of statistics_user_profile
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_visit_daily
-- ----------------------------
DROP TABLE IF EXISTS `statistics_visit_daily`;
CREATE TABLE `statistics_visit_daily`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `stat_date` date NOT NULL COMMENT '统计日期',
                                           `pv` int NULL DEFAULT 0 COMMENT '页面访问量（PV）',
                                           `uv` int NULL DEFAULT 0 COMMENT '独立访客数（UV）',
                                           `ip_count` int NULL DEFAULT 0 COMMENT '独立IP数',
                                           `new_user_count` int NULL DEFAULT 0 COMMENT '新用户数',
                                           `bounce_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '跳出率（百分比）',
                                           `avg_visit_duration` int NULL DEFAULT 0 COMMENT '平均访问时长（秒）',
                                           `page_count` int NULL DEFAULT 0 COMMENT '平均访问页数',
                                           `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE INDEX `uk_stat_date`(`stat_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日访问统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of statistics_visit_daily
-- ----------------------------

-- ----------------------------
-- Table structure for sys_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
                                  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人用户名',
                                  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型：CREATE-新增，UPDATE-修改，DELETE-删除，VIEW-查看，LOGIN-登录，LOGOUT-登出等',
                                  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块：USER-用户，ROLE-角色，PERMISSION-权限，ARTICLE-文章，TOPIC-专题等',
                                  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标类型',
                                  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID',
                                  `target_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标名称',
                                  `operation_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
                                  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
                                  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
                                  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法：GET，POST，PUT，DELETE等',
                                  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求URL',
                                  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数，JSON格式',
                                  `old_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更前值，JSON格式',
                                  `new_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更后值，JSON格式',
                                  `changed_fields` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变更字段，逗号分隔',
                                  `operation_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SUCCESS' COMMENT '操作状态：SUCCESS-成功，FAILED-失败',
                                  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
                                  `execution_time` int NULL DEFAULT NULL COMMENT '执行时间（毫秒）',
                                  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
                                  INDEX `idx_module`(`module` ASC) USING BTREE,
                                  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                  INDEX `idx_create_time`(`create_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统审计日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_audit_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_email_verify
-- ----------------------------
DROP TABLE IF EXISTS `sys_email_verify`;
CREATE TABLE `sys_email_verify`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
                                     `verify_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
                                     `verify_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证类型：REGISTER-注册，RESET_PASSWORD-重置密码，CHANGE_EMAIL-修改邮箱',
                                     `expire_time` datetime NOT NULL COMMENT '过期时间',
                                     `is_used` tinyint NULL DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
                                     `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
                                     `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_email`(`email` ASC) USING BTREE,
                                     INDEX `idx_verify_code`(`verify_code` ASC) USING BTREE,
                                     INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱验证码表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_email_verify
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `parent_id` bigint NULL DEFAULT NULL COMMENT '父菜单ID，NULL或0表示顶级菜单',
                             `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单编码',
                             `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
                             `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
                             `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
                             `sort` int NULL DEFAULT 0 COMMENT '排序号',
                             `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                             `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                             `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_menu_code`(`menu_code` ASC) USING BTREE,
                             INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
                             INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, NULL, 'SYSTEM', '系统管理', '/system', 'Setting', 1, '系统管理菜单', 1, '2025-11-28 13:49:45', '2025-11-28 13:49:45', 0);
INSERT INTO `sys_menu` VALUES (2, 1, 'SYSTEM_MENU', '菜单管理', '/system/menu', 'Menu', 1, '菜单管理菜单', 1, '2025-11-28 13:49:45', '2025-11-28 13:49:45', 0);
INSERT INTO `sys_menu` VALUES (3, 1, 'SYSTEM_ROLE', '角色管理', '/system/role', 'UserFilled', 2, '角色管理菜单', 1, '2025-11-28 13:49:45', '2025-11-28 13:49:45', 0);
INSERT INTO `sys_menu` VALUES (4, 1, 'SYSTEM_USER', '用户管理', '/system/user', 'User', 3, '用户管理菜单', 1, '2025-11-28 13:49:45', '2025-11-28 13:49:45', 0);

-- ----------------------------
-- Table structure for sys_menu_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_permission`;
CREATE TABLE `sys_menu_permission`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                        `permission_id` bigint NOT NULL COMMENT '权限ID',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_menu_permission`(`menu_id` ASC, `permission_id` ASC) USING BTREE,
                                        INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE,
                                        INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu_permission
-- ----------------------------

-- ----------------------------
-- Table structure for sys_password_reset
-- ----------------------------
DROP TABLE IF EXISTS `sys_password_reset`;
CREATE TABLE `sys_password_reset`  (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `user_id` bigint NOT NULL COMMENT '用户ID',
                                       `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
                                       `reset_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '重置令牌',
                                       `expire_time` datetime NOT NULL COMMENT '过期时间',
                                       `is_used` tinyint NULL DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
                                       `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
                                       `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                       INDEX `idx_reset_token`(`reset_token` ASC) USING BTREE,
                                       INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '密码重置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_password_reset
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `permission_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限编码',
                                   `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
                                   `permission_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'BUTTON' COMMENT '权限类型：BUTTON-按钮权限，DATA-数据权限',
                                   `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源路径',
                                   `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法（GET、POST、PUT、DELETE等）',
                                   `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                   `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                   `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE,
                                   INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
                                   INDEX `idx_permission_type`(`permission_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 'USER_VIEW', '查看用户', 'BUTTON', '/api/user/**', 'GET', '查看用户信息', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (2, 'USER_EDIT', '编辑用户', 'BUTTON', '/api/user/**', 'POST', '编辑用户信息', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (3, 'USER_DELETE', '删除用户', 'BUTTON', '/api/user/**', 'DELETE', '删除用户', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (4, 'ROLE_VIEW', '查看角色', 'BUTTON', '/api/role/**', 'GET', '查看角色信息', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (5, 'ROLE_EDIT', '编辑角色', 'BUTTON', '/api/role/**', 'POST', '编辑角色信息', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (6, 'ROLE_DELETE', '删除角色', 'BUTTON', '/api/role/**', 'DELETE', '删除角色', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (7, 'ORDER_VIEW', '查看订单', 'BUTTON', '/api/order/**', 'GET', '查看订单信息', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (8, 'ORDER_CREATE', '创建订单', 'BUTTON', '/api/order/**', 'POST', '创建订单', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (9, 'SECKILL_VIEW', '查看秒杀', 'BUTTON', '/api/seckill/**', 'GET', '查看秒杀活动', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (10, 'SECKILL_JOIN', '参与秒杀', 'BUTTON', '/api/seckill/**', 'POST', '参与秒杀活动', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (11, 'USER_DATA_ALL', '用户数据-全部', 'DATA', '/api/user/**', 'GET', '可查看所有用户数据', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (12, 'USER_DATA_DEPT', '用户数据-部门', 'DATA', '/api/user/**', 'GET', '只能查看本部门用户数据', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (13, 'USER_DATA_SELF', '用户数据-自己', 'DATA', '/api/user/**', 'GET', '只能查看自己的数据', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (14, 'ORDER_DATA_ALL', '订单数据-全部', 'DATA', '/api/order/**', 'GET', '可查看所有订单数据', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_permission` VALUES (15, 'ORDER_DATA_SELF', '订单数据-自己', 'DATA', '/api/order/**', 'GET', '只能查看自己的订单数据', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
                             `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
                             `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                             `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                             `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE,
                             INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', '系统管理员，拥有所有权限', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role` VALUES (2, 'USER', '普通用户', '普通用户，拥有基本权限', 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `role_id` bigint NOT NULL COMMENT '角色ID',
                                        `permission_id` bigint NOT NULL COMMENT '权限ID',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
                                        INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
                                        INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (2, 1, 2, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (3, 1, 3, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (4, 1, 4, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (5, 1, 5, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (6, 1, 6, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (7, 1, 7, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (8, 1, 8, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (9, 1, 9, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (10, 1, 10, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (11, 1, 11, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (12, 1, 12, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (13, 1, 13, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (14, 1, 14, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (15, 1, 15, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (16, 2, 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (17, 2, 7, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (18, 2, 9, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (19, 2, 13, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_role_permission` VALUES (20, 2, 15, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
                             `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
                             `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `wechat_open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信OpenID',
                             `wechat_union_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信UnionID',
                             `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                             `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盐值',
                             `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
                             UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
                             UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
                             INDEX `idx_wechat_open_id`(`wechat_open_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '4d6a203f65ffa22dc78a8abd71822422', '13800138000', 'admin@example.com', NULL, NULL, 1, 'salt123', '2025-11-27 17:27:56', '2025-11-27 17:27:56', 0);
INSERT INTO `sys_user` VALUES (2, 'test', '1145c103eb846dad9303b18b8d455929', '13800138001', 'test@example.com', NULL, NULL, 1, 'salt456', '2025-11-27 17:27:56', '2025-11-27 17:27:56', 0);

-- ----------------------------
-- Table structure for sys_user_extend
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_extend`;
CREATE TABLE `sys_user_extend`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `user_id` bigint NOT NULL COMMENT '用户ID',
                                    `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
                                    `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
                                    `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
                                    `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别：MALE-男，FEMALE-女，UNKNOWN-未知',
                                    `birthday` date NULL DEFAULT NULL COMMENT '生日',
                                    `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在地',
                                    `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司',
                                    `position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位',
                                    `website` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人网站',
                                    `github` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GitHub账号',
                                    `weibo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微博账号',
                                    `tech_stack` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技术栈，JSON数组格式',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户扩展信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_extend
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_follow`;
CREATE TABLE `sys_user_follow`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `user_id` bigint NOT NULL COMMENT '用户ID（关注者）',
                                    `follow_user_id` bigint NOT NULL COMMENT '被关注用户ID',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_user_follow`(`user_id` ASC, `follow_user_id` ASC) USING BTREE,
                                    INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                    INDEX `idx_follow_user_id`(`follow_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_follow
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `user_id` bigint NOT NULL COMMENT '用户ID',
                                  `role_id` bigint NOT NULL COMMENT '角色ID',
                                  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
                                  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);
INSERT INTO `sys_user_role` VALUES (2, 2, 2, '2025-11-27 17:36:53', '2025-11-27 17:36:53', 0);

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `branch_id` bigint NOT NULL COMMENT '分支事务ID',
                             `xid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '全局事务ID',
                             `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上下文',
                             `rollback_info` longblob NOT NULL COMMENT '回滚信息',
                             `log_status` int NOT NULL COMMENT '状态：0-正常，1-已回滚',
                             `log_created` datetime NOT NULL COMMENT '创建时间',
                             `log_modified` datetime NOT NULL COMMENT '修改时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_rollback`(`xid` ASC, `branch_id` ASC) USING BTREE,
                             INDEX `idx_log_created`(`log_created` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Seata 分布式事务回滚日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
