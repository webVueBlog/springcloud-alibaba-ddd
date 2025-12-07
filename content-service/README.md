# Content Service

内容服务模块，负责管理文章、专题、分类、评论、沸点等内容。

## 功能特性

### 文章管理
- ✅ 文章CRUD
- ✅ 文章发布/下架
- ✅ 文章统计（阅读数、点赞数、评论数）
- ⚠️ 待实现：文章历史版本管理
- ⚠️ 待实现：文章标签关联

### 专题管理
- ⚠️ 待实现：专题CRUD
- ⚠️ 待实现：专题文章关联

### 分类管理
- ⚠️ 待实现：分类CRUD
- ⚠️ 待实现：分类树形结构

### 标签管理
- ⚠️ 待实现：标签CRUD
- ⚠️ 待实现：标签热度统计

### 评论管理
- ⚠️ 待实现：评论CRUD
- ⚠️ 待实现：多级回复
- ⚠️ 待实现：评论审核

### 沸点管理
- ⚠️ 待实现：沸点CRUD
- ⚠️ 待实现：沸点审核

## API接口

### 文章接口

- `POST /api/content/article` - 创建文章
- `PUT /api/content/article/{id}` - 更新文章
- `POST /api/content/article/{id}/publish` - 发布文章
- `POST /api/content/article/{id}/offline` - 下架文章
- `DELETE /api/content/article/{id}` - 删除文章
- `GET /api/content/article/{id}` - 获取文章详情
- `GET /api/content/article` - 查询文章列表
- `POST /api/content/article/{id}/like` - 点赞文章
- `POST /api/content/article/{id}/unlike` - 取消点赞

## 数据库表

- `content_article` - 文章表
- `content_category` - 分类表
- `content_tag` - 标签表
- `content_topic` - 专题表
- `content_comment` - 评论表
- `content_boiling_point` - 沸点表

## 技术栈

- Spring Boot 2.7.18
- MyBatis Plus
- MySQL
- Redis（缓存）
- Elasticsearch（全文搜索，待集成）

## 后续计划

1. 完善专题、分类、标签、评论、沸点功能
2. 集成Elasticsearch全文搜索
3. 实现内容审核机制
4. 实现内容推荐算法
5. 实现内容统计分析

