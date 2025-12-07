/*
 内容管理数据库设计
 包含：文章、专题、分类、评论、标签等核心内容表
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_content` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_content`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for content_category
-- ----------------------------
DROP TABLE IF EXISTS `content_category`;
CREATE TABLE `content_category` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `parent_id` bigint NULL DEFAULT NULL COMMENT '父分类ID，NULL或0表示顶级分类',
                                    `category_code` varchar(50) NOT NULL COMMENT '分类编码',
                                    `category_name` varchar(100) NOT NULL COMMENT '分类名称',
                                    `icon` varchar(100) NULL DEFAULT NULL COMMENT '分类图标',
                                    `description` varchar(500) NULL DEFAULT NULL COMMENT '分类描述',
                                    `sort` int NULL DEFAULT 0 COMMENT '排序号',
                                    `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_category_code`(`category_code` ASC) USING BTREE,
                                    INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_category
-- ----------------------------
INSERT INTO `content_category` VALUES (1, NULL, 'FRONTEND', '前端', 'Frontend', '前端开发相关', 1, 1, NOW(), NOW(), 0);
INSERT INTO `content_category` VALUES (2, NULL, 'BACKEND', '后端', 'Backend', '后端开发相关', 2, 1, NOW(), NOW(), 0);
INSERT INTO `content_category` VALUES (3, NULL, 'MOBILE', '移动端', 'Mobile', '移动端开发相关', 3, 1, NOW(), NOW(), 0);
INSERT INTO `content_category` VALUES (4, NULL, 'AI', '人工智能', 'AI', 'AI相关', 4, 1, NOW(), NOW(), 0);

-- ----------------------------
-- Table structure for content_tag
-- ----------------------------
DROP TABLE IF EXISTS `content_tag`;
CREATE TABLE `content_tag` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
                               `tag_code` varchar(50) NOT NULL COMMENT '标签编码',
                               `description` varchar(255) NULL DEFAULT NULL COMMENT '标签描述',
                               `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
                               `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                               `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `uk_tag_code`(`tag_code` ASC) USING BTREE,
                               INDEX `idx_tag_name`(`tag_name` ASC) USING BTREE,
                               INDEX `idx_use_count`(`use_count` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_article
-- ----------------------------
DROP TABLE IF EXISTS `content_article`;
CREATE TABLE `content_article` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `user_id` bigint NOT NULL COMMENT '作者ID',
                                   `title` varchar(200) NOT NULL COMMENT '文章标题',
                                   `summary` varchar(500) NULL DEFAULT NULL COMMENT '文章摘要',
                                   `cover_image` varchar(500) NULL DEFAULT NULL COMMENT '封面图片URL',
                                   `content` longtext NOT NULL COMMENT '文章内容（Markdown/HTML）',
                                   `content_type` varchar(20) NULL DEFAULT 'MARKDOWN' COMMENT '内容类型：MARKDOWN- Markdown，RICH_TEXT-富文本',
                                   `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
                                   `view_count` int NULL DEFAULT 0 COMMENT '阅读数',
                                   `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                   `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
                                   `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
                                   `share_count` int NULL DEFAULT 0 COMMENT '分享数',
                                   `publish_status` varchar(20) NULL DEFAULT 'DRAFT' COMMENT '发布状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下架',
                                   `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
                                   `access_level` varchar(20) NULL DEFAULT 'PUBLIC' COMMENT '访问级别：PUBLIC-公开，PRIVATE-私有，PAID-付费可见，SPECIFIED-指定用户可见',
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `content_article_tag`;
CREATE TABLE `content_article_tag` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `article_id` bigint NOT NULL COMMENT '文章ID',
                                       `tag_id` bigint NOT NULL COMMENT '标签ID',
                                       `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `uk_article_tag`(`article_id` ASC, `tag_id` ASC) USING BTREE,
                                       INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
                                       INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_article_version
-- ----------------------------
DROP TABLE IF EXISTS `content_article_version`;
CREATE TABLE `content_article_version` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `article_id` bigint NOT NULL COMMENT '文章ID',
                                           `version` int NOT NULL COMMENT '版本号',
                                           `title` varchar(200) NOT NULL COMMENT '文章标题',
                                           `content` longtext NOT NULL COMMENT '文章内容',
                                           `summary` varchar(500) NULL DEFAULT NULL COMMENT '文章摘要',
                                           `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建版本的用户ID',
                                           `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
                                           INDEX `idx_version`(`version` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章历史版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_topic
-- ----------------------------
DROP TABLE IF EXISTS `content_topic`;
CREATE TABLE `content_topic` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `topic_name` varchar(100) NOT NULL COMMENT '专题名称',
                                 `topic_code` varchar(50) NOT NULL COMMENT '专题编码',
                                 `cover_image` varchar(500) NULL DEFAULT NULL COMMENT '封面图片URL',
                                 `description` text NULL DEFAULT NULL COMMENT '专题描述',
                                 `article_count` int NULL DEFAULT 0 COMMENT '文章数量',
                                 `follower_count` int NULL DEFAULT 0 COMMENT '关注数',
                                 `access_level` varchar(20) NULL DEFAULT 'PUBLIC' COMMENT '访问级别：PUBLIC-公开，PRIVATE-私有，PAID-付费可见，SPECIFIED-指定用户可见',
                                 `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                 `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `uk_topic_code`(`topic_code` ASC) USING BTREE,
                                 INDEX `idx_topic_name`(`topic_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_topic_article
-- ----------------------------
DROP TABLE IF EXISTS `content_topic_article`;
CREATE TABLE `content_topic_article` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `topic_id` bigint NOT NULL COMMENT '专题ID',
                                         `article_id` bigint NOT NULL COMMENT '文章ID',
                                         `sort` int NULL DEFAULT 0 COMMENT '排序号',
                                         `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE INDEX `uk_topic_article`(`topic_id` ASC, `article_id` ASC) USING BTREE,
                                         INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE,
                                         INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专题文章关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_comment
-- ----------------------------
DROP TABLE IF EXISTS `content_comment`;
CREATE TABLE `content_comment` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `user_id` bigint NOT NULL COMMENT '评论用户ID',
                                   `target_type` varchar(20) NOT NULL COMMENT '目标类型：ARTICLE-文章，COMMENT-评论（回复）',
                                   `target_id` bigint NOT NULL COMMENT '目标ID（文章ID或评论ID）',
                                   `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID，NULL表示顶级评论',
                                   `content` text NOT NULL COMMENT '评论内容',
                                   `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                   `reply_count` int NULL DEFAULT 0 COMMENT '回复数',
                                   `audit_status` varchar(20) NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
                                   `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
                                   `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
                                   `audit_reason` varchar(500) NULL DEFAULT NULL COMMENT '审核原因',
                                   `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                   `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                   INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                   INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
                                   INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `content_comment_like`;
CREATE TABLE `content_comment_like` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `user_id` bigint NOT NULL COMMENT '用户ID',
                                        `comment_id` bigint NOT NULL COMMENT '评论ID',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_user_comment`(`user_id` ASC, `comment_id` ASC) USING BTREE,
                                        INDEX `idx_comment_id`(`comment_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_article_like
-- ----------------------------
DROP TABLE IF EXISTS `content_article_like`;
CREATE TABLE `content_article_like` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `user_id` bigint NOT NULL COMMENT '用户ID',
                                        `article_id` bigint NOT NULL COMMENT '文章ID',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_user_article`(`user_id` ASC, `article_id` ASC) USING BTREE,
                                        INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_article_collect
-- ----------------------------
DROP TABLE IF EXISTS `content_article_collect`;
CREATE TABLE `content_article_collect` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `user_id` bigint NOT NULL COMMENT '用户ID',
                                           `article_id` bigint NOT NULL COMMENT '文章ID',
                                           `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE INDEX `uk_user_article`(`user_id` ASC, `article_id` ASC) USING BTREE,
                                           INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for content_boiling_point
-- ----------------------------
DROP TABLE IF EXISTS `content_boiling_point`;
CREATE TABLE `content_boiling_point` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `user_id` bigint NOT NULL COMMENT '发布用户ID',
                                         `content` text NOT NULL COMMENT '沸点内容',
                                         `image_urls` varchar(1000) NULL DEFAULT NULL COMMENT '图片URLs，JSON数组格式',
                                         `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                         `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
                                         `share_count` int NULL DEFAULT 0 COMMENT '分享数',
                                         `audit_status` varchar(20) NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
                                         `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                                         `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                         INDEX `idx_create_time`(`create_time` DESC) USING BTREE,
                                         FULLTEXT INDEX `ft_content`(`content`) COMMENT '全文索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '沸点表（短内容）' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

