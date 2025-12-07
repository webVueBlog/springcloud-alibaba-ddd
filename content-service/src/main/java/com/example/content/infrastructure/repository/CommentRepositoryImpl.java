package com.example.content.infrastructure.repository;

import com.example.content.domain.model.Comment;
import com.example.content.domain.repository.CommentRepository;
import com.example.content.infrastructure.mapper.CommentMapper;
import com.example.content.infrastructure.po.CommentPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论仓储实现
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    
    private final CommentMapper commentMapper;
    
    @Override
    public Comment save(Comment comment) {
        CommentPO po = toPO(comment);
        if (comment.getId() == null) {
            commentMapper.insert(po);
        } else {
            commentMapper.updateById(po);
        }
        return toDomain(po);
    }
    
    @Override
    public Comment findById(Long id) {
        CommentPO po = commentMapper.selectById(id);
        return po != null ? toDomain(po) : null;
    }
    
    @Override
    public List<Comment> findTopLevelComments(String targetType, Long targetId) {
        List<CommentPO> pos = commentMapper.findTopLevelComments(targetType, targetId);
        return pos.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Comment> findRepliesByParentId(Long parentId) {
        List<CommentPO> pos = commentMapper.findRepliesByParentId(parentId);
        return pos.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void delete(Long id) {
        CommentPO po = commentMapper.selectById(id);
        if (po != null) {
            po.setDeleted(1);
            commentMapper.updateById(po);
        }
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        commentMapper.incrementLikeCount(id);
    }
    
    @Override
    public void decrementLikeCount(Long id) {
        commentMapper.decrementLikeCount(id);
    }
    
    @Override
    public void incrementReplyCount(Long id) {
        commentMapper.incrementReplyCount(id);
    }
    
    @Override
    public void decrementReplyCount(Long id) {
        commentMapper.decrementReplyCount(id);
    }
    
    private CommentPO toPO(Comment comment) {
        CommentPO po = new CommentPO();
        BeanUtils.copyProperties(comment, po);
        return po;
    }
    
    private Comment toDomain(CommentPO po) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(po, comment);
        return comment;
    }
}

