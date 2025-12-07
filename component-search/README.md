# 搜索组件 (Component Search)

## 概述

Component Search 是一个全文搜索组件，基于 Elasticsearch 实现。提供索引创建、文档增删改查、全文搜索等功能，适用于商品搜索、内容搜索、日志搜索等场景。

## 功能特性

### 核心功能

- **索引管理**：支持创建索引和设置字段映射
- **文档操作**：支持文档的增删改查
- **全文搜索**：支持关键词搜索、模糊匹配、多字段搜索
- **自动映射**：支持自动创建索引和字段映射

### 技术特性

- 基于 Spring Data Elasticsearch
- 支持 Elasticsearch 7.x
- 完善的异常处理和日志记录
- 自动配置，条件加载（Elasticsearch 可用时才加载）

## 技术栈

- Spring Boot 2.7.18
- Spring Data Elasticsearch
- Elasticsearch 7.x
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用搜索组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-search</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Elasticsearch

在 `application.yml` 中配置 Elasticsearch 连接信息：

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200  # Elasticsearch 地址
    username: elastic  # 用户名（可选）
    password: password  # 密码（可选）
```

### 3. 启用组件

确保 Spring Boot 能够扫描到搜索组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.search"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 创建文档实体

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product")
public class Product {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
    
    @Field(type = FieldType.Double)
    private Double price;
    
    // getter/setter
}
```

### 5. 使用搜索服务

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final SearchService searchService;
    
    public void addProduct(Product product) {
        searchService.addDocument("product", product.getId(), product);
    }
    
    public List<Product> searchProducts(String keyword) {
        return searchService.search("product", keyword, Product.class);
    }
}
```

## 使用指南

### 索引管理

#### 创建索引

```java
@Service
@RequiredArgsConstructor
public class IndexService {
    
    private final SearchService searchService;
    
    public void createProductIndex() {
        Map<String, Object> mapping = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        
        // 定义字段映射
        Map<String, Object> nameField = new HashMap<>();
        nameField.put("type", "text");
        nameField.put("analyzer", "ik_max_word");
        properties.put("name", nameField);
        
        Map<String, Object> priceField = new HashMap<>();
        priceField.put("type", "double");
        properties.put("price", priceField);
        
        Map<String, Object> mappings = new HashMap<>();
        mappings.put("properties", properties);
        mapping.put("mappings", mappings);
        
        // 创建索引
        boolean created = searchService.createIndex("product", mapping);
        if (created) {
            log.info("创建索引成功: product");
        }
    }
}
```

### 文档操作

#### 添加文档

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final SearchService searchService;
    
    public void addProduct(Product product) {
        boolean success = searchService.addDocument("product", product.getId(), product);
        if (success) {
            log.info("添加商品文档成功: productId={}", product.getId());
        } else {
            log.error("添加商品文档失败: productId={}", product.getId());
        }
    }
}
```

#### 更新文档

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final SearchService searchService;
    
    public void updateProduct(Product product) {
        boolean success = searchService.updateDocument("product", product.getId(), product);
        if (success) {
            log.info("更新商品文档成功: productId={}", product.getId());
        }
    }
}
```

#### 删除文档

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final SearchService searchService;
    
    public void deleteProduct(String productId) {
        boolean success = searchService.deleteDocument("product", productId);
        if (success) {
            log.info("删除商品文档成功: productId={}", productId);
        }
    }
}
```

### 全文搜索

#### 基本搜索

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final SearchService searchService;
    
    public List<Product> searchProducts(String keyword) {
        // 搜索商品
        List<Product> products = searchService.search("product", keyword, Product.class);
        return products;
    }
}
```

#### 在 Controller 中使用

```java
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final SearchService searchService;
    
    @GetMapping("/search")
    public Result<List<Product>> search(@RequestParam String keyword) {
        List<Product> products = searchService.search("product", keyword, Product.class);
        return Result.success(products);
    }
}
```

## 完整示例

### 示例1：商品搜索

```java
/**
 * 商品实体
 */
@Document(indexName = "product")
public class Product {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
    
    @Field(type = FieldType.Double)
    private Double price;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    // getter/setter
}

/**
 * 商品服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final SearchService searchService;
    
    /**
     * 添加商品到搜索索引
     */
    public void addProduct(Product product) {
        boolean success = searchService.addDocument("product", product.getId(), product);
        if (!success) {
            throw new BusinessException("添加商品到搜索索引失败");
        }
        log.info("添加商品到搜索索引成功: productId={}", product.getId());
    }
    
    /**
     * 更新商品搜索索引
     */
    public void updateProduct(Product product) {
        boolean success = searchService.updateDocument("product", product.getId(), product);
        if (!success) {
            throw new BusinessException("更新商品搜索索引失败");
        }
        log.info("更新商品搜索索引成功: productId={}", product.getId());
    }
    
    /**
     * 从搜索索引删除商品
     */
    public void deleteProduct(String productId) {
        boolean success = searchService.deleteDocument("product", productId);
        if (!success) {
            throw new BusinessException("从搜索索引删除商品失败");
        }
        log.info("从搜索索引删除商品成功: productId={}", productId);
    }
    
    /**
     * 搜索商品
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = searchService.search("product", keyword, Product.class);
        log.info("搜索商品: keyword={}, resultCount={}", keyword, products.size());
        return products;
    }
}

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public Result<Product> createProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return Result.success(product);
    }
    
    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        productService.updateProduct(product);
        return Result.success(product);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return Result.success();
    }
    
    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return Result.success(products);
    }
}
```

### 示例2：文章搜索

```java
/**
 * 文章实体
 */
@Document(indexName = "article")
public class Article {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String author;
    
    @Field(type = FieldType.Date)
    private Date publishTime;
    
    // getter/setter
}

/**
 * 文章服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    
    private final SearchService searchService;
    private final ArticleRepository articleRepository;
    
    /**
     * 发布文章（同步到搜索索引）
     */
    public Article publishArticle(Article article) {
        // 保存到数据库
        article = articleRepository.save(article);
        
        // 同步到搜索索引
        searchService.addDocument("article", article.getId(), article);
        
        log.info("发布文章成功: articleId={}", article.getId());
        return article;
    }
    
    /**
     * 搜索文章
     */
    public List<Article> searchArticles(String keyword) {
        List<Article> articles = searchService.search("article", keyword, Article.class);
        log.info("搜索文章: keyword={}, resultCount={}", keyword, articles.size());
        return articles;
    }
}
```

### 示例3：日志搜索

```java
/**
 * 日志实体
 */
@Document(indexName = "log")
public class LogDocument {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String level;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String message;
    
    @Field(type = FieldType.Keyword)
    private String serviceName;
    
    @Field(type = FieldType.Date)
    private Date timestamp;
    
    // getter/setter
}

/**
 * 日志服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    
    private final SearchService searchService;
    
    /**
     * 记录日志到搜索索引
     */
    public void log(LogDocument logDoc) {
        logDoc.setId(UUID.randomUUID().toString());
        searchService.addDocument("log", logDoc.getId(), logDoc);
    }
    
    /**
     * 搜索日志
     */
    public List<LogDocument> searchLogs(String keyword) {
        return searchService.search("log", keyword, LogDocument.class);
    }
}
```

## 配置说明

### 必需配置

- **Elasticsearch 连接**：必须配置 Elasticsearch 连接信息

### 可选配置

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `spring.elasticsearch.uris` | Elasticsearch 地址 | http://localhost:9200 | 是 |
| `spring.elasticsearch.username` | 用户名 | - | 否 |
| `spring.elasticsearch.password` | 密码 | - | 否 |
| `spring.elasticsearch.connection-timeout` | 连接超时时间 | 1s | 否 |
| `spring.elasticsearch.socket-timeout` | Socket 超时时间 | 30s | 否 |

### 配置示例

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: password
    connection-timeout: 1s
    socket-timeout: 30s
```

## 最佳实践

### 1. 使用实体类注解

推荐使用 `@Document` 和 `@Field` 注解定义索引和字段：

```java
@Document(indexName = "product")
public class Product {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    
    @Field(type = FieldType.Double)
    private Double price;
}
```

### 2. 选择合适的分析器

根据业务需求选择合适的分析器：

```java
// 中文分词：使用 ik_max_word
@Field(type = FieldType.Text, analyzer = "ik_max_word")
private String content;

// 英文分词：使用 standard
@Field(type = FieldType.Text, analyzer = "standard")
private String title;

// 不分词：使用 keyword
@Field(type = FieldType.Keyword)
private String category;
```

### 3. 同步数据到搜索索引

在数据变更时同步更新搜索索引：

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final SearchService searchService;
    
    public Product createProduct(Product product) {
        // 保存到数据库
        product = productRepository.save(product);
        
        // 同步到搜索索引
        searchService.addDocument("product", product.getId(), product);
        
        return product;
    }
    
    public void updateProduct(Product product) {
        // 更新数据库
        productRepository.save(product);
        
        // 更新搜索索引
        searchService.updateDocument("product", product.getId(), product);
    }
    
    public void deleteProduct(String id) {
        // 删除数据库记录
        productRepository.deleteById(id);
        
        // 删除搜索索引
        searchService.deleteDocument("product", id);
    }
}
```

### 4. 处理搜索异常

妥善处理搜索异常，避免影响业务：

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final SearchService searchService;
    
    public List<Product> searchProducts(String keyword) {
        try {
            return searchService.search("product", keyword, Product.class);
        } catch (Exception e) {
            log.error("搜索商品失败: keyword={}", keyword, e);
            // 返回空列表或从数据库查询
            return new ArrayList<>();
        }
    }
}
```

### 5. 索引命名规范

使用有意义的索引名称：

```java
// ✅ 推荐
@Document(indexName = "product")
@Document(indexName = "user")
@Document(indexName = "order")

// ❌ 不推荐
@Document(indexName = "p")
@Document(indexName = "data")
```

### 6. 定期重建索引

定期重建索引以优化性能：

```java
@Service
@RequiredArgsConstructor
public class IndexService {
    
    private final SearchService searchService;
    private final ProductRepository productRepository;
    
    @Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点执行
    public void rebuildProductIndex() {
        log.info("开始重建商品索引");
        
        // 删除旧索引
        // searchService.deleteIndex("product");
        
        // 创建新索引
        searchService.createIndex("product", null);
        
        // 重新导入数据
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            searchService.addDocument("product", product.getId(), product);
        }
        
        log.info("重建商品索引完成: count={}", products.size());
    }
}
```

## 常见问题

### 1. 搜索服务注入为 null？

**答**：检查以下几点：
- Elasticsearch 是否已配置并启动
- `spring.elasticsearch.uris` 是否配置
- Spring Boot 是否能够扫描到 `com.example.search` 包
- ElasticsearchOperations 是否可用

### 2. 搜索不到数据？

**答**：检查以下几点：
- 索引是否存在
- 文档是否已添加到索引
- 搜索关键词是否正确
- 字段映射是否正确

### 3. 中文搜索不准确？

**答**：
- 安装 IK 分词器插件
- 在字段上使用 `analyzer = "ik_max_word"`
- 重新索引数据

### 4. 如何安装 IK 分词器？

**答**：
```bash
# 进入 Elasticsearch 安装目录
cd /path/to/elasticsearch

# 安装 IK 分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.17.0/elasticsearch-analysis-ik-7.17.0.zip

# 重启 Elasticsearch
```

### 5. 如何提高搜索性能？

**答**：
- 使用合适的字段类型（Text vs Keyword）
- 使用合适的分析器
- 定期优化索引
- 使用分页查询
- 使用缓存

### 6. 如何禁用搜索组件？

**答**：不配置 `spring.elasticsearch.uris`，搜索服务将不会被创建。

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供索引创建功能
  - 提供文档增删改查功能
  - 提供全文搜索功能

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

