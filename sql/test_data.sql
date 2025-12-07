/*
 测试数据脚本
 用于初始化用户端首页展示所需的数据
 包括：用户、文章、专题、标签等
*/

-- 使用内容管理数据库
USE `ddd_content`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 用户数据（如果用户表在 ddd_auth 数据库中）
-- ============================================
-- 注意：用户数据需要在 ddd_auth 数据库中执行
-- 这里假设已经有 admin 和 test 用户（ID 为 1 和 2）

-- ============================================
-- 2. 标签数据
-- ============================================
-- 插入标签数据（如果已存在则忽略）
INSERT IGNORE INTO `content_tag` (`id`, `tag_name`, `tag_code`, `description`, `use_count`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'Vue', 'VUE', 'Vue.js 前端框架', 10, 1, NOW(), NOW(), 0),
(2, 'React', 'REACT', 'React 前端框架', 8, 1, NOW(), NOW(), 0),
(3, 'Java', 'JAVA', 'Java 编程语言', 15, 1, NOW(), NOW(), 0),
(4, 'Python', 'PYTHON', 'Python 编程语言', 12, 1, NOW(), NOW(), 0),
(5, 'Spring Boot', 'SPRING_BOOT', 'Spring Boot 框架', 9, 1, NOW(), NOW(), 0),
(6, '微服务', 'MICROSERVICE', '微服务架构', 7, 1, NOW(), NOW(), 0),
(7, 'Docker', 'DOCKER', 'Docker 容器技术', 5, 1, NOW(), NOW(), 0),
(8, 'Kubernetes', 'KUBERNETES', 'K8s 容器编排', 4, 1, NOW(), NOW(), 0);

-- ============================================
-- 3. 专题数据
-- ============================================
-- 插入专题数据（如果已存在则忽略）
INSERT IGNORE INTO `content_topic` (`id`, `topic_name`, `topic_code`, `cover_image`, `description`, `article_count`, `follower_count`, `access_level`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'Vue.js 实战', 'VUE_PRACTICE', NULL, 'Vue.js 实战开发经验分享', 5, 120, 'PUBLIC', 1, NOW(), NOW(), 0),
(2, 'Spring Boot 进阶', 'SPRING_BOOT_ADVANCED', NULL, 'Spring Boot 高级特性与最佳实践', 8, 200, 'PUBLIC', 1, NOW(), NOW(), 0),
(3, '微服务架构', 'MICROSERVICE_ARCH', NULL, '微服务架构设计与实践', 6, 150, 'PUBLIC', 1, NOW(), NOW(), 0),
(4, '前端工程化', 'FRONTEND_ENGINEERING', NULL, '前端工程化实践与工具链', 4, 80, 'PUBLIC', 1, NOW(), NOW(), 0),
(5, '数据库优化', 'DATABASE_OPTIMIZATION', NULL, '数据库性能优化与调优技巧', 7, 100, 'PUBLIC', 1, NOW(), NOW(), 0);

-- ============================================
-- 4. 文章数据
-- ============================================
-- 假设用户ID 1 和 2 存在
-- 插入文章数据（如果已存在则忽略）
INSERT IGNORE INTO `content_article` (`id`, `user_id`, `title`, `summary`, `cover_image`, `content`, `content_type`, `category_id`, `view_count`, `like_count`, `comment_count`, `collect_count`, `share_count`, `publish_status`, `publish_time`, `access_level`, `is_top`, `is_recommend`, `is_hot`, `version`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, 'Vue 3 Composition API 完全指南', '深入理解 Vue 3 的 Composition API，掌握响应式系统的核心原理', NULL,
'# Vue 3 Composition API 完全指南\n\nVue 3 引入了 Composition API，这是一个全新的 API 设计，提供了更好的逻辑复用和代码组织方式。\n\n## 什么是 Composition API\n\nComposition API 是一组基于函数的 API，允许你更灵活地组织组件代码。\n\n## 核心概念\n\n### ref 和 reactive\n\n- `ref`: 用于创建响应式的原始值\n- `reactive`: 用于创建响应式的对象\n\n### computed 和 watch\n\n- `computed`: 计算属性\n- `watch`: 监听器\n\n## 最佳实践\n\n1. 使用 Composition API 组织逻辑\n2. 提取可复用的逻辑到 composables\n3. 保持代码的清晰和可维护性',
'MARKDOWN', 1, 1250, 89, 23, 45, 12, 'PUBLISHED', NOW(), 'PUBLIC', 1, 1, 1, 1, 1, NOW(), NOW(), 0),

(2, 1, 'Spring Boot 微服务实践', '从零开始构建 Spring Boot 微服务应用，包含服务注册、配置中心、网关等', NULL,
'# Spring Boot 微服务实践\n\n微服务架构是现代应用开发的主流模式，Spring Boot 提供了完善的微服务支持。\n\n## 微服务架构设计\n\n### 服务拆分原则\n\n1. 单一职责原则\n2. 高内聚低耦合\n3. 数据独立\n\n### 服务通信\n\n- RESTful API\n- 消息队列\n- gRPC\n\n## Spring Cloud 组件\n\n- Eureka/Nacos: 服务注册与发现\n- Config: 配置中心\n- Gateway: API 网关\n- Feign: 服务调用',
'MARKDOWN', 2, 980, 67, 18, 32, 8, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 1 DAY), 'PUBLIC', 0, 1, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),

(3, 2, 'React Hooks 深入理解', '全面解析 React Hooks 的使用场景和最佳实践', NULL,
'# React Hooks 深入理解\n\nReact Hooks 是 React 16.8 引入的新特性，它允许你在函数组件中使用状态和其他 React 特性。\n\n## 常用 Hooks\n\n### useState\n\n用于在函数组件中添加状态。\n\n### useEffect\n\n用于处理副作用，如数据获取、订阅等。\n\n### useContext\n\n用于在组件树中共享数据。\n\n## 自定义 Hooks\n\n可以创建自定义 Hooks 来复用逻辑。',
'MARKDOWN', 1, 756, 45, 12, 20, 5, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 2 DAY), 'PUBLIC', 0, 0, 0, 1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),

(4, 1, 'Docker 容器化部署指南', '使用 Docker 容器化应用，实现快速部署和扩展', NULL,
'# Docker 容器化部署指南\n\nDocker 是一个开源的容器化平台，可以帮助我们快速构建、部署和运行应用。\n\n## Docker 基础\n\n### 镜像和容器\n\n- 镜像：应用的模板\n- 容器：镜像的运行实例\n\n### Dockerfile\n\n用于构建镜像的配置文件。\n\n## 最佳实践\n\n1. 使用多阶段构建\n2. 优化镜像大小\n3. 合理使用缓存\n\n## 部署流程\n\n1. 编写 Dockerfile\n2. 构建镜像\n3. 运行容器',
'MARKDOWN', 2, 542, 32, 8, 15, 3, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 3 DAY), 'PUBLIC', 0, 0, 0, 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0),

(5, 2, 'TypeScript 类型系统详解', '深入理解 TypeScript 的类型系统，提升代码质量', NULL,
'# TypeScript 类型系统详解\n\nTypeScript 是 JavaScript 的超集，添加了静态类型检查。\n\n## 基础类型\n\n- number\n- string\n- boolean\n- array\n- object\n\n## 高级类型\n\n- 联合类型\n- 交叉类型\n- 泛型\n- 条件类型\n\n## 类型推断\n\nTypeScript 可以自动推断类型，减少类型注解。',
'MARKDOWN', 1, 432, 28, 6, 12, 2, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 4 DAY), 'PUBLIC', 0, 0, 0, 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0),

(6, 1, 'Redis 缓存策略与实践', 'Redis 在微服务架构中的应用和最佳实践', NULL,
'# Redis 缓存策略与实践\n\nRedis 是一个高性能的内存数据库，广泛应用于缓存、消息队列等场景。\n\n## 缓存策略\n\n### 缓存穿透\n\n解决缓存穿透的方法：\n1. 布隆过滤器\n2. 缓存空值\n\n### 缓存击穿\n\n解决缓存击穿的方法：\n1. 互斥锁\n2. 热点数据永不过期\n\n### 缓存雪崩\n\n解决缓存雪崩的方法：\n1. 过期时间随机化\n2. 多级缓存\n\n## 实践建议\n\n1. 合理设置过期时间\n2. 监控缓存命中率\n3. 使用合适的序列化方式',
'MARKDOWN', 2, 389, 24, 5, 10, 1, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 5 DAY), 'PUBLIC', 0, 0, 0, 1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0),

(7, 2, '前端性能优化实战', '前端性能优化的各种技巧和工具', NULL,
'# 前端性能优化实战\n\n前端性能优化是提升用户体验的关键。\n\n## 优化策略\n\n### 资源加载优化\n\n1. 代码分割\n2. 懒加载\n3. 预加载\n\n### 渲染优化\n\n1. 虚拟滚动\n2. 防抖节流\n3. 使用 Web Workers\n\n### 网络优化\n\n1. HTTP/2\n2. CDN\n3. 压缩资源\n\n## 工具推荐\n\n- Lighthouse\n- WebPageTest\n- Chrome DevTools',
'MARKDOWN', 1, 321, 19, 4, 8, 1, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 6 DAY), 'PUBLIC', 0, 0, 0, 1, 1, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), 0),

(8, 1, 'MySQL 索引优化技巧', '深入理解 MySQL 索引原理，提升查询性能', NULL,
'# MySQL 索引优化技巧\n\n索引是数据库性能优化的关键。\n\n## 索引类型\n\n- B-Tree 索引\n- 哈希索引\n- 全文索引\n\n## 索引设计原则\n\n1. 选择性高的列\n2. 经常用于 WHERE 的列\n3. 避免过多索引\n\n## 优化技巧\n\n1. 使用 EXPLAIN 分析\n2. 避免索引失效\n3. 合理使用覆盖索引',
'MARKDOWN', 2, 278, 16, 3, 6, 0, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 7 DAY), 'PUBLIC', 0, 0, 0, 1, 1, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW(), 0);

-- ============================================
-- 5. 文章标签关联
-- ============================================
-- 插入文章标签关联（如果已存在则忽略）
INSERT IGNORE INTO `content_article_tag` (`article_id`, `tag_id`, `create_time`) VALUES
(1, 1, NOW()),  -- Vue 3 文章 -> Vue 标签
(1, 2, NOW()),  -- Vue 3 文章 -> React 标签（对比）
(2, 3, NOW()),  -- Spring Boot 文章 -> Java 标签
(2, 5, NOW()),  -- Spring Boot 文章 -> Spring Boot 标签
(2, 6, NOW()),  -- Spring Boot 文章 -> 微服务 标签
(3, 2, NOW()),  -- React Hooks 文章 -> React 标签
(4, 7, NOW()),  -- Docker 文章 -> Docker 标签
(5, 1, NOW()),  -- TypeScript 文章 -> Vue 标签（前端相关）
(6, 3, NOW()),  -- Redis 文章 -> Java 标签
(7, 1, NOW()),  -- 前端性能优化 -> Vue 标签
(8, 3, NOW());  -- MySQL 文章 -> Java 标签

-- ============================================
-- 6. 专题文章关联
-- ============================================
-- 插入专题文章关联（如果已存在则忽略）
INSERT IGNORE INTO `content_topic_article` (`topic_id`, `article_id`, `sort`, `create_time`) VALUES
(1, 1, 1, NOW()),  -- Vue.js 实战专题 -> Vue 3 文章
(1, 3, 2, NOW()),  -- Vue.js 实战专题 -> React Hooks 文章
(2, 2, 1, NOW()),  -- Spring Boot 进阶专题 -> Spring Boot 文章
(2, 6, 2, NOW()),  -- Spring Boot 进阶专题 -> Redis 文章
(3, 2, 1, NOW()),  -- 微服务架构专题 -> Spring Boot 文章
(3, 4, 2, NOW()); -- 微服务架构专题 -> Docker 文章

-- ============================================
-- 7. 更新专题的文章数量
-- ============================================
UPDATE `content_topic` SET `article_count` = (
    SELECT COUNT(*) FROM `content_topic_article` WHERE `topic_id` = `content_topic`.`id`
) WHERE `id` IN (1, 2, 3, 4, 5);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 执行说明
-- ============================================
-- 1. 确保已执行 ddd_content.sql 创建表结构
-- 2. 确保用户表（ddd_auth）中已有用户数据（至少 user_id = 1 和 2）
-- 3. 执行此脚本：mysql -u root -p ddd_content < sql/test_data.sql
-- 4. 执行后，用户端首页应该能看到文章和专题数据

