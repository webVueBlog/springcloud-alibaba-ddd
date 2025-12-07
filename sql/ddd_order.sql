/*
 Navicat Premium Data Transfer

 Source Server         : 海外测试
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Schema         : ddd_order

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 29/11/2025 17:49:19
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_order`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
                            `user_id` bigint NOT NULL COMMENT '用户ID',
                            `product_id` bigint NOT NULL COMMENT '商品ID',
                            `quantity` int NOT NULL COMMENT '数量',
                            `amount` decimal(10, 2) NOT NULL COMMENT '金额',
                            `status` int NULL DEFAULT 1 COMMENT '状态：1-待支付，2-已支付，3-已取消',
                            `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
                            `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` int NULL DEFAULT 0 COMMENT '删除标识',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
                            INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Seata 分布式事务回滚日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
