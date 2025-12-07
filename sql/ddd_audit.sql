/*
 审计日志数据库设计
 记录所有操作审计日志（操作人、IP、时间、变更字段）
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_audit` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_audit`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
                                 `username` varchar(50) NULL DEFAULT NULL COMMENT '操作人用户名',
                                 `operation_type` varchar(50) NOT NULL COMMENT '操作类型：CREATE-新增，UPDATE-修改，DELETE-删除，VIEW-查看，LOGIN-登录，LOGOUT-登出等',
                                 `module` varchar(50) NOT NULL COMMENT '操作模块：USER-用户，ROLE-角色，PERMISSION-权限，ARTICLE-文章，TOPIC-专题等',
                                 `target_type` varchar(50) NULL DEFAULT NULL COMMENT '目标类型',
                                 `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID',
                                 `target_name` varchar(200) NULL DEFAULT NULL COMMENT '目标名称',
                                 `operation_desc` varchar(500) NULL DEFAULT NULL COMMENT '操作描述',
                                 `ip_address` varchar(50) NULL DEFAULT NULL COMMENT 'IP地址',
                                 `user_agent` varchar(500) NULL DEFAULT NULL COMMENT '用户代理',
                                 `request_method` varchar(10) NULL DEFAULT NULL COMMENT '请求方法：GET，POST，PUT，DELETE等',
                                 `request_url` varchar(500) NULL DEFAULT NULL COMMENT '请求URL',
                                 `request_params` text NULL DEFAULT NULL COMMENT '请求参数，JSON格式',
                                 `old_value` text NULL DEFAULT NULL COMMENT '变更前值，JSON格式',
                                 `new_value` text NULL DEFAULT NULL COMMENT '变更后值，JSON格式',
                                 `changed_fields` varchar(500) NULL DEFAULT NULL COMMENT '变更字段，逗号分隔',
                                 `operation_status` varchar(20) NULL DEFAULT 'SUCCESS' COMMENT '操作状态：SUCCESS-成功，FAILED-失败',
                                 `error_message` text NULL DEFAULT NULL COMMENT '错误信息',
                                 `execution_time` int NULL DEFAULT NULL COMMENT '执行时间（毫秒）',
                                 `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                 INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
                                 INDEX `idx_module`(`module` ASC) USING BTREE,
                                 INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                 INDEX `idx_create_time`(`create_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统审计日志表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

