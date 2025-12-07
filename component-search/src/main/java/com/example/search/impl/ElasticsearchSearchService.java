package com.example.search.impl;

import com.example.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 搜索服务实现
 * <p>
 * 基于 Spring Data Elasticsearch 实现全文搜索功能
 * 提供索引创建、文档增删改查、全文搜索等操作
 * </p>
 * <p>
 * 实现说明：
 * <ul>
 *   <li>使用 ElasticsearchOperations 进行文档操作</li>
 *   <li>使用 RestHighLevelClient 进行索引操作</li>
 *   <li>支持全文搜索和模糊匹配</li>
 *   <li>完善的异常处理和日志记录</li>
 * </ul>
 * </p>
 * <p>
 * 注意：只有当 ElasticsearchOperations 存在时才会创建此 Bean
 * 如果 Elasticsearch 未配置，此服务不会被创建
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(ElasticsearchOperations.class)
public class ElasticsearchSearchService implements SearchService {

    /** Elasticsearch 操作接口 */
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 创建索引
     * 
     * @param indexName 索引名称
     * @param mapping 字段映射配置
     * @return true 表示创建成功，false 表示创建失败
     */
    @Override
    public boolean createIndex(String indexName, Map<String, Object> mapping) {
        try {
            if (indexName == null || indexName.trim().isEmpty()) {
                throw new IllegalArgumentException("索引名称不能为空");
            }
            
            // 检查索引是否已存在
            if (elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).exists()) {
                log.info("索引已存在: {}", indexName);
                return true;
            }
            
            // 创建索引
            boolean created = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).create();
            
            if (created) {
                log.info("创建索引成功: {}", indexName);
                
                // 如果提供了 mapping，可以通过 ElasticsearchOperations 设置
                // 注意：Spring Data Elasticsearch 的 mapping 设置需要在实体类上使用注解
                if (mapping != null && !mapping.isEmpty()) {
                    log.debug("索引映射配置: indexName={}, mapping={}", indexName, mapping);
                }
            } else {
                log.warn("创建索引失败: {}", indexName);
            }
            
            return created;
        } catch (Exception e) {
            log.error("创建索引失败: indexName={}", indexName, e);
            return false;
        }
    }

    /**
     * 添加文档
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     * @param document 文档对象
     * @return true 表示添加成功，false 表示添加失败
     */
    @Override
    public boolean addDocument(String indexName, String id, Object document) {
        try {
            if (indexName == null || indexName.trim().isEmpty()) {
                throw new IllegalArgumentException("索引名称不能为空");
            }
            if (document == null) {
                throw new IllegalArgumentException("文档对象不能为空");
            }
            
            // 设置索引名称
            IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
            
            // 保存文档
            Object saved = elasticsearchOperations.save(document, indexCoordinates);
            
            if (saved != null) {
                log.debug("添加文档成功: indexName={}, id={}", indexName, id);
                return true;
            } else {
                log.warn("添加文档失败: indexName={}, id={}", indexName, id);
                return false;
            }
        } catch (Exception e) {
            log.error("添加文档失败: indexName={}, id={}", indexName, id, e);
            return false;
        }
    }

    /**
     * 搜索文档
     * 
     * @param <T> 返回文档的类型
     * @param indexName 索引名称
     * @param keyword 搜索关键词
     * @param clazz 文档类型
     * @return 搜索结果列表
     */
    @Override
    public <T> List<T> search(String indexName, String keyword, Class<T> clazz) {
        try {
            if (indexName == null || indexName.trim().isEmpty()) {
                throw new IllegalArgumentException("索引名称不能为空");
            }
            if (keyword == null || keyword.trim().isEmpty()) {
                // 如果关键词为空，返回所有文档
                return searchAll(indexName, clazz);
            }
            
            IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
            
            // 构建查询：在所有字段中搜索关键词
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.multiMatchQuery(keyword, "*")
                            .type(org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS)
                            .fuzziness(org.elasticsearch.common.unit.Fuzziness.AUTO))
                    .build();
            
            // 执行搜索
            SearchHits<T> searchHits = elasticsearchOperations.search(query, clazz, indexCoordinates);
            
            // 提取结果
            List<T> results = new ArrayList<>();
            for (SearchHit<T> hit : searchHits) {
                results.add(hit.getContent());
            }
            
            log.debug("搜索成功: indexName={}, keyword={}, resultCount={}", 
                    indexName, keyword, results.size());
            return results;
        } catch (Exception e) {
            log.error("搜索失败: indexName={}, keyword={}", indexName, keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 搜索所有文档
     * 
     * @param <T> 返回文档的类型
     * @param indexName 索引名称
     * @param clazz 文档类型
     * @return 所有文档列表
     */
    private <T> List<T> searchAll(String indexName, Class<T> clazz) {
        try {
            IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .build();
            
            SearchHits<T> searchHits = elasticsearchOperations.search(query, clazz, indexCoordinates);
            
            List<T> results = new ArrayList<>();
            for (SearchHit<T> hit : searchHits) {
                results.add(hit.getContent());
            }
            
            return results;
        } catch (Exception e) {
            log.error("搜索所有文档失败: indexName={}", indexName, e);
            return new ArrayList<>();
        }
    }

    /**
     * 删除文档
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     * @return true 表示删除成功，false 表示删除失败
     */
    @Override
    public boolean deleteDocument(String indexName, String id) {
        try {
            if (indexName == null || indexName.trim().isEmpty()) {
                throw new IllegalArgumentException("索引名称不能为空");
            }
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("文档ID不能为空");
            }
            
            IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
            
            // 删除文档
            String deleted = elasticsearchOperations.delete(id, indexCoordinates);
            
            if (deleted != null) {
                log.debug("删除文档成功: indexName={}, id={}", indexName, id);
                return true;
            } else {
                log.warn("删除文档失败: indexName={}, id={}", indexName, id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除文档失败: indexName={}, id={}", indexName, id, e);
            return false;
        }
    }

    /**
     * 更新文档
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     * @param document 新的文档对象
     * @return true 表示更新成功，false 表示更新失败
     */
    @Override
    public boolean updateDocument(String indexName, String id, Object document) {
        try {
            if (indexName == null || indexName.trim().isEmpty()) {
                throw new IllegalArgumentException("索引名称不能为空");
            }
            if (document == null) {
                throw new IllegalArgumentException("文档对象不能为空");
            }
            
            // 更新文档实际上就是保存文档（如果ID相同会覆盖）
            return addDocument(indexName, id, document);
        } catch (Exception e) {
            log.error("更新文档失败: indexName={}, id={}", indexName, id, e);
            return false;
        }
    }
}

