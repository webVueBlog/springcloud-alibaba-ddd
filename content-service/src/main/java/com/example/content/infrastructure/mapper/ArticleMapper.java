package com.example.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.content.infrastructure.po.ArticlePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticlePO> {
    
    /**
     * 根据条件查询文章列表
     */
    List<ArticlePO> findByCondition(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("publishStatus") String publishStatus,
            @Param("title") String title,
            @Param("status") Integer status,
            @Param("orderBy") String orderBy,
            @Param("order") String order
    );
    
    /**
     * 增加阅读数
     */
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 增加点赞数
     */
    void incrementLikeCount(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    void decrementLikeCount(@Param("id") Long id);
    
    /**
     * 增加评论数
     */
    void incrementCommentCount(@Param("id") Long id);
    
    /**
     * 减少评论数
     */
    void decrementCommentCount(@Param("id") Long id);
}

