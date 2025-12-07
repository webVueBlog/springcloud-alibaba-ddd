package com.example.content.application.service;

import com.example.common.exception.BusinessException;
import com.example.common.permission.DataPermissionService;
import com.example.content.domain.model.Article;
import com.example.content.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章应用服务
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    
    private final ArticleRepository articleRepository;
    private final DataPermissionService dataPermissionService;
    
    /**
     * 创建文章
     */
    @Transactional
    public Article createArticle(Article article) {
        Assert.notNull(article, "文章不能为空");
        Assert.notNull(article.getUserId(), "作者ID不能为空");
        Assert.hasText(article.getTitle(), "文章标题不能为空");
        Assert.hasText(article.getContent(), "文章内容不能为空");
        
        try {
            // 设置默认值
            if (article.getPublishStatus() == null) {
                article.setPublishStatus("DRAFT");
            }
            if (article.getContentType() == null) {
                article.setContentType("MARKDOWN");
            }
            if (article.getAccessLevel() == null) {
                article.setAccessLevel("PUBLIC");
            }
            if (article.getViewCount() == null) {
                article.setViewCount(0);
            }
            if (article.getLikeCount() == null) {
                article.setLikeCount(0);
            }
            if (article.getCommentCount() == null) {
                article.setCommentCount(0);
            }
            if (article.getCollectCount() == null) {
                article.setCollectCount(0);
            }
            if (article.getShareCount() == null) {
                article.setShareCount(0);
            }
            if (article.getVersion() == null) {
                article.setVersion(1);
            }
            if (article.getStatus() == null) {
                article.setStatus(1);
            }
            
            return articleRepository.save(article);
        } catch (Exception e) {
            log.error("创建文章失败: userId={}, title={}", article.getUserId(), article.getTitle(), e);
            throw new BusinessException("创建文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新文章
     */
    @Transactional
    public Article updateArticle(Long id, Article article) {
        Assert.notNull(id, "文章ID不能为空");
        Assert.notNull(article, "文章不能为空");
        
        try {
            Article existing = articleRepository.findById(id);
            if (existing == null) {
                throw new BusinessException("文章不存在");
            }
            
            // 更新字段
            if (StringUtils.hasText(article.getTitle())) {
                existing.setTitle(article.getTitle());
            }
            if (StringUtils.hasText(article.getSummary())) {
                existing.setSummary(article.getSummary());
            }
            if (StringUtils.hasText(article.getCoverImage())) {
                existing.setCoverImage(article.getCoverImage());
            }
            if (StringUtils.hasText(article.getContent())) {
                existing.setContent(article.getContent());
                // 更新版本号
                existing.setVersion(existing.getVersion() + 1);
            }
            if (article.getCategoryId() != null) {
                existing.setCategoryId(article.getCategoryId());
            }
            if (StringUtils.hasText(article.getContentType())) {
                existing.setContentType(article.getContentType());
            }
            if (StringUtils.hasText(article.getAccessLevel())) {
                existing.setAccessLevel(article.getAccessLevel());
            }
            
            return articleRepository.save(existing);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新文章失败: id={}", id, e);
            throw new BusinessException("更新文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 发布文章
     */
    @Transactional
    public Article publishArticle(Long id) {
        Assert.notNull(id, "文章ID不能为空");
        
        try {
            Article article = articleRepository.findById(id);
            if (article == null) {
                throw new BusinessException("文章不存在");
            }
            
            article.setPublishStatus("PUBLISHED");
            article.setPublishTime(LocalDateTime.now());
            
            return articleRepository.save(article);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发布文章失败: id={}", id, e);
            throw new BusinessException("发布文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 下架文章
     */
    @Transactional
    public Article offlineArticle(Long id) {
        Assert.notNull(id, "文章ID不能为空");
        
        try {
            Article article = articleRepository.findById(id);
            if (article == null) {
                throw new BusinessException("文章不存在");
            }
            
            article.setPublishStatus("OFFLINE");
            
            return articleRepository.save(article);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("下架文章失败: id={}", id, e);
            throw new BusinessException("下架文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文章
     */
    @Transactional
    public void deleteArticle(Long id) {
        Assert.notNull(id, "文章ID不能为空");
        
        try {
            Article article = articleRepository.findById(id);
            if (article == null) {
                throw new BusinessException("文章不存在");
            }
            
            articleRepository.delete(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除文章失败: id={}", id, e);
            throw new BusinessException("删除文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取文章
     * 
     * @param id 文章ID
     * @param currentUserId 当前用户ID（可为null，表示未登录用户）
     * @param userPermissions 用户权限列表（可为null）
     * @return 文章
     */
    public Article getArticleById(Long id, Long currentUserId, List<String> userPermissions) {
        Assert.notNull(id, "文章ID不能为空");
        
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 检查访问权限
        dataPermissionService.checkContentAccessOrThrow(
            article.getAccessLevel(),
            article.getUserId(),
            currentUserId,
            userPermissions != null ? userPermissions : new ArrayList<>()
        );
        
        // 增加阅读数
        articleRepository.incrementViewCount(id);
        
        return article;
    }
    
    /**
     * 根据ID获取文章（兼容旧方法）
     */
    public Article getArticleById(Long id) {
        return getArticleById(id, null, null);
    }
    
    /**
     * 查询文章列表
     * 
     * @param userId 用户ID（可选）
     * @param categoryId 分类ID（可选）
     * @param publishStatus 发布状态（可选）
     * @param title 标题（可选）
     * @param status 状态（可选）
     * @param orderBy 排序字段（可选：viewCount, publishTime, likeCount）
     * @param order 排序方向（可选：asc, desc）
     * @param currentUserId 当前用户ID（可为null，用于访问级别过滤）
     * @param userPermissions 用户权限列表（可为null，用于访问级别过滤）
     * @return 文章列表
     */
    public List<Article> getArticleList(Long userId, Long categoryId, String publishStatus, String title, Integer status, String orderBy, String order, Long currentUserId, List<String> userPermissions) {
        try {
            List<Article> articles = articleRepository.findByCondition(userId, categoryId, publishStatus, title, status, orderBy, order);
            
            // 根据访问级别过滤
            if (articles != null && !articles.isEmpty()) {
                articles = articles.stream()
                    .filter(article -> {
                        try {
                            dataPermissionService.checkContentAccess(
                                article.getAccessLevel(),
                                article.getUserId(),
                                currentUserId,
                                userPermissions != null ? userPermissions : new ArrayList<>()
                            );
                            return true;
                        } catch (Exception e) {
                            log.debug("文章访问权限检查失败: articleId={}, accessLevel={}, currentUserId={}", 
                                article.getId(), article.getAccessLevel(), currentUserId);
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            }
            
            return articles;
        } catch (Exception e) {
            log.error("查询文章列表失败", e);
            throw new BusinessException("查询文章列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询文章列表（兼容旧方法）
     */
    public List<Article> getArticleList(Long userId, Long categoryId, String publishStatus, String title, Integer status) {
        return getArticleList(userId, categoryId, publishStatus, title, status, null, null, null, null);
    }
    
    /**
     * 查询文章列表（兼容旧方法，带权限）
     */
    public List<Article> getArticleList(Long userId, Long categoryId, String publishStatus, String title, Integer status, Long currentUserId, List<String> userPermissions) {
        return getArticleList(userId, categoryId, publishStatus, title, status, null, null, currentUserId, userPermissions);
    }
    
    /**
     * 点赞文章
     */
    @Transactional
    public void likeArticle(Long id) {
        Assert.notNull(id, "文章ID不能为空");
        
        try {
            Article article = articleRepository.findById(id);
            if (article == null) {
                throw new BusinessException("文章不存在");
            }
            
            articleRepository.incrementLikeCount(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("点赞文章失败: id={}", id, e);
            throw new BusinessException("点赞文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消点赞文章
     */
    @Transactional
    public void unlikeArticle(Long id) {
        Assert.notNull(id, "文章ID不能为空");
        
        try {
            Article article = articleRepository.findById(id);
            if (article == null) {
                throw new BusinessException("文章不存在");
            }
            
            articleRepository.decrementLikeCount(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消点赞文章失败: id={}", id, e);
            throw new BusinessException("取消点赞文章失败: " + e.getMessage());
        }
    }
}

