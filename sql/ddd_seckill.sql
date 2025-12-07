/*
 Navicat Premium Data Transfer

 Source Server         : 海外测试
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Schema         : ddd_seckill

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 29/11/2025 17:49:33
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_seckill` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_seckill`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_seckill_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_activity`;
CREATE TABLE `t_seckill_activity`  (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `activity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动名称',
                                       `product_id` bigint NOT NULL COMMENT '商品ID',
                                       `stock` int NOT NULL COMMENT '活动库存数量',
                                       `sold` int NULL DEFAULT 0 COMMENT '已售数量',
                                       `start_time` datetime NOT NULL COMMENT '活动开始时间',
                                       `end_time` datetime NOT NULL COMMENT '活动结束时间',
                                       `status` int NULL DEFAULT 0 COMMENT '活动状态：0-未开始，1-进行中，2-已结束，3-已取消',
                                       `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` int NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
                                       INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
                                       INDEX `idx_end_time`(`end_time` ASC) USING BTREE,
                                       INDEX `idx_status`(`status` ASC) USING BTREE,
                                       INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '秒杀活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_seckill_activity
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
