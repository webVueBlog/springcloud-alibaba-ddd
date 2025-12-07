package com.example.content.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.content.domain.model.Article;
import com.example.content.domain.repository.ArticleRepository;
import com.example.content.infrastructure.mapper.ArticleMapper;
import com.example.content.infrastructure.po.ArticlePO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章仓储实现
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {
    
    private final ArticleMapper articleMapper;
    
    @Override
    public Article findById(Long id) {
        ArticlePO po = articleMapper.selectById(id);
        return poToDomain(po);
    }
    
    @Override
    public List<Article> findByCondition(Long userId, Long categoryId, String publishStatus, String title, Integer status, String orderBy, String order) {
        List<ArticlePO> pos = articleMapper.findByCondition(userId, categoryId, publishStatus, title, status, orderBy, order);
        return pos.stream().map(this::poToDomain).collect(Collectors.toList());
    }
    
    @Override
    public Article save(Article article) {
        ArticlePO po = domainToPo(article);
        if (article.getId() == null) {
            articleMapper.insert(po);
        } else {
            articleMapper.updateById(po);
        }
        return poToDomain(po);
    }
    
    @Override
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        articleMapper.incrementViewCount(id);
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        articleMapper.incrementLikeCount(id);
    }
    
    @Override
    public void decrementLikeCount(Long id) {
        articleMapper.decrementLikeCount(id);
    }
    
    @Override
    public void incrementCommentCount(Long id) {
        articleMapper.incrementCommentCount(id);
    }
    
    @Override
    public void decrementCommentCount(Long id) {
        articleMapper.decrementCommentCount(id);
    }
    
    private Article poToDomain(ArticlePO po) {
        if (po == null) {
            return null;
        }
        Article article = new Article();
        BeanUtils.copyProperties(po, article);
        return article;
    }
    
    private ArticlePO domainToPo(Article article) {
        if (article == null) {
            return null;
        }
        ArticlePO po = new ArticlePO();
        BeanUtils.copyProperties(article, po);
        return po;
    }
}

