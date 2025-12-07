/*
 统计分析数据库设计
 包含：访问统计、事件日志、用户行为分析等
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_statistics` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_statistics`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for statistics_event_log
-- ----------------------------
DROP TABLE IF EXISTS `statistics_event_log`;
CREATE TABLE `statistics_event_log` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID，未登录用户为NULL',
                                        `event_type` varchar(50) NOT NULL COMMENT '事件类型：VIEW-浏览，CLICK-点击，LIKE-点赞，COMMENT-评论，SHARE-分享，SEARCH-搜索等',
                                        `event_category` varchar(50) NULL DEFAULT NULL COMMENT '事件分类：ARTICLE-文章，TOPIC-专题，BOILING_POINT-沸点，COURSE-课程等',
                                        `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID（文章ID、专题ID等）',
                                        `target_type` varchar(50) NULL DEFAULT NULL COMMENT '目标类型',
                                        `ip_address` varchar(50) NULL DEFAULT NULL COMMENT 'IP地址',
                                        `user_agent` varchar(500) NULL DEFAULT NULL COMMENT '用户代理（浏览器信息）',
                                        `referer` varchar(500) NULL DEFAULT NULL COMMENT '来源页面',
                                        `device_type` varchar(20) NULL DEFAULT NULL COMMENT '设备类型：PC，MOBILE，TABLET',
                                        `os_type` varchar(50) NULL DEFAULT NULL COMMENT '操作系统类型',
                                        `browser_type` varchar(50) NULL DEFAULT NULL COMMENT '浏览器类型',
                                        `session_id` varchar(100) NULL DEFAULT NULL COMMENT '会话ID',
                                        `extra_data` text NULL DEFAULT NULL COMMENT '扩展数据，JSON格式',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                        INDEX `idx_event_type`(`event_type` ASC) USING BTREE,
                                        INDEX `idx_event_category`(`event_category` ASC) USING BTREE,
                                        INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                        INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
                                        INDEX `idx_session_id`(`session_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '事件日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for statistics_visit_daily
-- ----------------------------
DROP TABLE IF EXISTS `statistics_visit_daily`;
CREATE TABLE `statistics_visit_daily` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `stat_date` date NOT NULL COMMENT '统计日期',
                                          `pv` int NULL DEFAULT 0 COMMENT '页面访问量（PV）',
                                          `uv` int NULL DEFAULT 0 COMMENT '独立访客数（UV）',
                                          `ip_count` int NULL DEFAULT 0 COMMENT '独立IP数',
                                          `new_user_count` int NULL DEFAULT 0 COMMENT '新用户数',
                                          `bounce_rate` decimal(5,2) NULL DEFAULT 0.00 COMMENT '跳出率（百分比）',
                                          `avg_visit_duration` int NULL DEFAULT 0 COMMENT '平均访问时长（秒）',
                                          `page_count` int NULL DEFAULT 0 COMMENT '平均访问页数',
                                          `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE INDEX `uk_stat_date`(`stat_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日访问统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for statistics_source_channel
-- ----------------------------
DROP TABLE IF EXISTS `statistics_source_channel`;
CREATE TABLE `statistics_source_channel` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `stat_date` date NOT NULL COMMENT '统计日期',
                                             `channel_type` varchar(50) NOT NULL COMMENT '渠道类型：NATURAL-自然流量，PAID-付费投放，SOCIAL-社交分享，SEARCH-搜索引擎，DIRECT-直接访问',
                                             `channel_name` varchar(100) NULL DEFAULT NULL COMMENT '渠道名称（如：百度、微信、微博等）',
                                             `visit_count` int NULL DEFAULT 0 COMMENT '访问次数',
                                             `user_count` int NULL DEFAULT 0 COMMENT '用户数',
                                             `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             UNIQUE INDEX `uk_stat_channel`(`stat_date` ASC, `channel_type` ASC, `channel_name` ASC) USING BTREE,
                                             INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '来源渠道统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for statistics_content_ranking
-- ----------------------------
DROP TABLE IF EXISTS `statistics_content_ranking`;
CREATE TABLE `statistics_content_ranking` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                              `stat_date` date NOT NULL COMMENT '统计日期',
                                              `content_type` varchar(50) NOT NULL COMMENT '内容类型：ARTICLE-文章，TOPIC-专题，BOILING_POINT-沸点，COURSE-课程',
                                              `content_id` bigint NOT NULL COMMENT '内容ID',
                                              `content_title` varchar(200) NULL DEFAULT NULL COMMENT '内容标题',
                                              `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
                                              `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
                                              `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
                                              `share_count` int NULL DEFAULT 0 COMMENT '分享数',
                                              `hot_score` decimal(10,2) NULL DEFAULT 0.00 COMMENT '热度分数（综合计算）',
                                              `ranking` int NULL DEFAULT 0 COMMENT '排名',
                                              `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              UNIQUE INDEX `uk_stat_content`(`stat_date` ASC, `content_type` ASC, `content_id` ASC) USING BTREE,
                                              INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
                                              INDEX `idx_hot_score`(`hot_score` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容排行统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for statistics_user_profile
-- ----------------------------
DROP TABLE IF EXISTS `statistics_user_profile`;
CREATE TABLE `statistics_user_profile` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `user_id` bigint NOT NULL COMMENT '用户ID',
                                           `visit_count` int NULL DEFAULT 0 COMMENT '总访问次数',
                                           `last_visit_time` datetime NULL DEFAULT NULL COMMENT '最后访问时间',
                                           `favorite_tags` varchar(500) NULL DEFAULT NULL COMMENT '关注标签，JSON数组格式',
                                           `favorite_categories` varchar(500) NULL DEFAULT NULL COMMENT '关注分类，JSON数组格式',
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户画像统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for statistics_tag_hot
-- ----------------------------
DROP TABLE IF EXISTS `statistics_tag_hot`;
CREATE TABLE `statistics_tag_hot` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `stat_date` date NOT NULL COMMENT '统计日期',
                                      `tag_id` bigint NOT NULL COMMENT '标签ID',
                                      `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
                                      `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
                                      `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
                                      `hot_score` decimal(10,2) NULL DEFAULT 0.00 COMMENT '热度分数',
                                      `ranking` int NULL DEFAULT 0 COMMENT '排名',
                                      `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `uk_stat_tag`(`stat_date` ASC, `tag_id` ASC) USING BTREE,
                                      INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
                                      INDEX `idx_hot_score`(`hot_score` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签热度统计表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

