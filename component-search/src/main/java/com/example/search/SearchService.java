package com.example.search;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 * <p>
 * 提供全文搜索功能，基于 Elasticsearch 实现
 * 支持索引创建、文档增删改查、全文搜索等操作
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入搜索服务
 * @Autowired
 * private SearchService searchService;
 * 
 * // 创建索引
 * Map&lt;String, Object&gt; mapping = new HashMap&lt;&gt;();
 * searchService.createIndex("product", mapping);
 * 
 * // 添加文档
 * Product product = new Product();
 * searchService.addDocument("product", "1", product);
 * 
 * // 搜索文档
 * List&lt;Product&gt; results = searchService.search("product", "手机", Product.class);
 * 
 * // 更新文档
 * searchService.updateDocument("product", "1", product);
 * 
 * // 删除文档
 * searchService.deleteDocument("product", "1");
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SearchService {
    
    /**
     * 创建索引
     * <p>
     * 在 Elasticsearch 中创建新的索引，并设置字段映射
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>索引名称必须小写，不能包含空格和特殊字符</li>
     *   <li>如果索引已存在，不会重复创建</li>
     *   <li>mapping 参数用于定义字段类型和属性</li>
     * </ul>
     * </p>
     * 
     * @param indexName 索引名称，必须小写
     * @param mapping 字段映射配置，定义字段类型和属性
     * @return true 表示创建成功，false 表示创建失败
     */
    boolean createIndex(String indexName, Map<String, Object> mapping);

    /**
     * 添加文档
     * <p>
     * 向指定索引添加文档，如果文档已存在则会被覆盖
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>文档对象会被序列化为 JSON 存储</li>
     *   <li>如果索引不存在，会自动创建</li>
     *   <li>如果 id 已存在，会覆盖原有文档</li>
     * </ul>
     * </p>
     * 
     * @param indexName 索引名称
     * @param id 文档ID，如果为 null 则自动生成
     * @param document 文档对象
     * @return true 表示添加成功，false 表示添加失败
     */
    boolean addDocument(String indexName, String id, Object document);

    /**
     * 搜索文档
     * <p>
     * 在指定索引中搜索包含关键词的文档
     * </p>
     * <p>
     * 搜索说明：
     * <ul>
     *   <li>支持全文搜索，会对所有文本字段进行匹配</li>
     *   <li>支持模糊匹配和分词搜索</li>
     *   <li>返回结果按相关性排序</li>
     * </ul>
     * </p>
     * 
     * @param <T> 返回文档的类型
     * @param indexName 索引名称
     * @param keyword 搜索关键词
     * @param clazz 文档类型
     * @return 搜索结果列表，按相关性排序
     */
    <T> List<T> search(String indexName, String keyword, Class<T> clazz);

    /**
     * 删除文档
     * <p>
     * 从指定索引中删除指定ID的文档
     * </p>
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     * @return true 表示删除成功，false 表示删除失败
     */
    boolean deleteDocument(String indexName, String id);

    /**
     * 更新文档
     * <p>
     * 更新指定索引中指定ID的文档
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>如果文档不存在，会创建新文档</li>
     *   <li>更新操作会完全替换原有文档</li>
     *   <li>文档对象会被序列化为 JSON 存储</li>
     * </ul>
     * </p>
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     * @param document 新的文档对象
     * @return true 表示更新成功，false 表示更新失败
     */
    boolean updateDocument(String indexName, String id, Object document);
}

