package com.example.content.interfaces.controller;

import com.example.common.result.Result;
import com.example.content.application.service.ArticleService;
import com.example.content.domain.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文章控制器
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/content/article")
@RequiredArgsConstructor
@Validated
public class ArticleController {
    
    private final ArticleService articleService;
    
    /**
     * 创建文章
     */
    @PostMapping
    public Result<Article> createArticle(@Valid @RequestBody Article article) {
        log.info("创建文章: userId={}, title={}", article.getUserId(), article.getTitle());
        Article result = articleService.createArticle(article);
        return Result.success(result);
    }
    
    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public Result<Article> updateArticle(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id,
            @Valid @RequestBody Article article) {
        log.info("更新文章: id={}", id);
        Article result = articleService.updateArticle(id, article);
        return Result.success(result);
    }
    
    /**
     * 发布文章
     */
    @PostMapping("/{id}/publish")
    public Result<Article> publishArticle(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        log.info("发布文章: id={}", id);
        Article result = articleService.publishArticle(id);
        return Result.success(result);
    }
    
    /**
     * 下架文章
     */
    @PostMapping("/{id}/offline")
    public Result<Article> offlineArticle(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        log.info("下架文章: id={}", id);
        Article result = articleService.offlineArticle(id);
        return Result.success(result);
    }
    
    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        log.info("删除文章: id={}", id);
        articleService.deleteArticle(id);
        return Result.success();
    }
    
    /**
     * 根据ID获取文章
     */
    @GetMapping("/{id}")
    public Result<Article> getArticleById(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @RequestHeader(value = "X-User-Permissions", required = false) String userPermissionsStr) {
        // 解析权限列表
        List<String> userPermissions = parsePermissions(userPermissionsStr);
        Article article = articleService.getArticleById(id, currentUserId, userPermissions);
        return Result.success(article);
    }
    
    /**
     * 查询文章列表
     */
    @GetMapping
    public Result<List<Article>> getArticleList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String publishStatus,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String order,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @RequestHeader(value = "X-User-Permissions", required = false) String userPermissionsStr) {
        // 解析权限列表
        List<String> userPermissions = parsePermissions(userPermissionsStr);
        List<Article> articles = articleService.getArticleList(userId, categoryId, publishStatus, title, status, orderBy, order, currentUserId, userPermissions);
        return Result.success(articles);
    }
    
    /**
     * 解析权限字符串
     */
    private List<String> parsePermissions(String permissionsStr) {
        if (permissionsStr == null || permissionsStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(permissionsStr.split(","));
    }
    
    /**
     * 点赞文章
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        articleService.likeArticle(id);
        return Result.success();
    }
    
    /**
     * 取消点赞文章
     */
    @PostMapping("/{id}/unlike")
    public Result<Void> unlikeArticle(
            @PathVariable @NotNull(message = "文章ID不能为空") Long id) {
        articleService.unlikeArticle(id);
        return Result.success();
    }
}

