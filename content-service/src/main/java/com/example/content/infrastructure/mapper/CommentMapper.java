package com.example.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.content.infrastructure.po.CommentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {
    
    /**
     * 根据目标类型和目标ID查询评论列表（只查询顶级评论）
     */
    List<CommentPO> findTopLevelComments(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    
    /**
     * 根据父评论ID查询回复列表
     */
    List<CommentPO> findRepliesByParentId(@Param("parentId") Long parentId);
    
    /**
     * 增加点赞数
     */
    void incrementLikeCount(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    void decrementLikeCount(@Param("id") Long id);
    
    /**
     * 增加回复数
     */
    void incrementReplyCount(@Param("id") Long id);
    
    /**
     * 减少回复数
     */
    void decrementReplyCount(@Param("id") Long id);
}

