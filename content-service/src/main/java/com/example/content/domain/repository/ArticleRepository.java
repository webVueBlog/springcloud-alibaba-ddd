package com.example.content.domain.repository;

import com.example.content.domain.model.Article;

import java.util.List;

/**
 * 文章仓储接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ArticleRepository {
    
    /**
     * 根据ID查找文章
     */
    Article findById(Long id);
    
    /**
     * 根据条件查询文章列表
     */
    List<Article> findByCondition(Long userId, Long categoryId, String publishStatus, String title, Integer status, String orderBy, String order);
    
    /**
     * 保存文章（新增或更新）
     */
    Article save(Article article);
    
    /**
     * 删除文章
     */
    void delete(Long id);
    
    /**
     * 增加阅读数
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少点赞数
     */
    void decrementLikeCount(Long id);
    
    /**
     * 增加评论数
     */
    void incrementCommentCount(Long id);
    
    /**
     * 减少评论数
     */
    void decrementCommentCount(Long id);
}

