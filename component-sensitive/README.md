# 敏感词组件 (Component Sensitive)

## 概述

Component Sensitive 是一个敏感词过滤组件，提供敏感词检测和过滤功能。基于 DFA（确定有限状态自动机）算法实现，支持敏感词检测、查找、替换等操作，适用于用户输入内容审核、评论过滤、聊天消息过滤等场景。

## 功能特性

### 核心功能

- **敏感词检测**：快速检测文本中是否包含敏感词
- **敏感词查找**：查找文本中所有匹配的敏感词
- **敏感词替换**：将敏感词替换为指定字符串
- **DFA 算法**：基于 DFA 算法实现，检测效率高

### 技术特性

- 基于 DFA（确定有限状态自动机）算法
- 支持中文、英文、数字等多种字符
- 支持部分匹配和完全匹配
- 自动初始化敏感词库
- 完善的异常处理和日志记录

## 技术栈

- Spring Boot 2.7.18
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用敏感词组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-sensitive</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用组件

确保 Spring Boot 能够扫描到敏感词组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.sensitive"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. 使用敏感词服务

```java
@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final SensitiveWordService sensitiveWordService;
    
    public Comment createComment(CommentDTO commentDTO) {
        // 检查是否包含敏感词
        if (sensitiveWordService.containsSensitiveWord(commentDTO.getContent())) {
            throw new BusinessException("评论内容包含敏感词，请修改后重试");
        }
        
        // 或者自动过滤敏感词
        String filteredContent = sensitiveWordService.replaceSensitiveWords(
            commentDTO.getContent(), 
            "***"
        );
        
        Comment comment = new Comment();
        comment.setContent(filteredContent);
        return commentRepository.save(comment);
    }
}
```

## 使用指南

### 初始化敏感词库

#### 方式1：使用默认敏感词（自动初始化）

组件会在应用启动时自动初始化默认敏感词库：

```java
// 无需手动初始化，组件会自动初始化
```

#### 方式2：从代码中初始化

```java
@Service
@RequiredArgsConstructor
public class SensitiveWordInitService {
    
    private final SensitiveWordService sensitiveWordService;
    
    @PostConstruct
    public void init() {
        List<String> words = Arrays.asList(
            "敏感词1",
            "敏感词2",
            "敏感词3"
        );
        sensitiveWordService.initSensitiveWords(words);
    }
}
```

#### 方式3：从配置文件加载

```java
@Service
@RequiredArgsConstructor
public class SensitiveWordInitService {
    
    private final SensitiveWordService sensitiveWordService;
    
    @Value("${sensitive.words:}")
    private List<String> sensitiveWords;
    
    @PostConstruct
    public void init() {
        if (sensitiveWords != null && !sensitiveWords.isEmpty()) {
            sensitiveWordService.initSensitiveWords(sensitiveWords);
        }
    }
}
```

#### 方式4：从数据库加载

```java
@Service
@RequiredArgsConstructor
public class SensitiveWordInitService {
    
    private final SensitiveWordService sensitiveWordService;
    private final SensitiveWordRepository sensitiveWordRepository;
    
    @PostConstruct
    public void init() {
        List<String> words = sensitiveWordRepository.findAllWords();
        sensitiveWordService.initSensitiveWords(words);
    }
}
```

#### 方式5：从文件加载

```java
@Service
@RequiredArgsConstructor
public class SensitiveWordInitService {
    
    private final SensitiveWordService sensitiveWordService;
    
    @PostConstruct
    public void init() {
        try {
            List<String> words = Files.readAllLines(
                Paths.get("sensitive-words.txt"),
                StandardCharsets.UTF_8
            );
            sensitiveWordService.initSensitiveWords(words);
        } catch (IOException e) {
            log.error("加载敏感词文件失败", e);
        }
    }
}
```

### 检测敏感词

#### 检查是否包含敏感词

```java
@Service
@RequiredArgsConstructor
public class ContentService {
    
    private final SensitiveWordService sensitiveWordService;
    
    public void validateContent(String content) {
        if (sensitiveWordService.containsSensitiveWord(content)) {
            throw new BusinessException("内容包含敏感词，请修改后重试");
        }
    }
}
```

#### 查找所有敏感词

```java
@Service
@RequiredArgsConstructor
public class ContentService {
    
    private final SensitiveWordService sensitiveWordService;
    
    public ValidationResult validateContent(String content) {
        Set<String> sensitiveWords = sensitiveWordService.findSensitiveWords(content);
        
        ValidationResult result = new ValidationResult();
        result.setValid(sensitiveWords.isEmpty());
        result.setSensitiveWords(sensitiveWords);
        
        if (!sensitiveWords.isEmpty()) {
            result.setMessage("内容包含敏感词: " + String.join(", ", sensitiveWords));
        }
        
        return result;
    }
}
```

### 替换敏感词

#### 基本替换

```java
@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final SensitiveWordService sensitiveWordService;
    
    public Comment createComment(CommentDTO commentDTO) {
        // 自动过滤敏感词
        String filteredContent = sensitiveWordService.replaceSensitiveWords(
            commentDTO.getContent(),
            "***"
        );
        
        Comment comment = new Comment();
        comment.setContent(filteredContent);
        return commentRepository.save(comment);
    }
}
```

#### 自定义替换字符串

```java
@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final SensitiveWordService sensitiveWordService;
    
    public Comment createComment(CommentDTO commentDTO) {
        // 使用自定义替换字符串
        String filteredContent = sensitiveWordService.replaceSensitiveWords(
            commentDTO.getContent(),
            "【已过滤】"
        );
        
        Comment comment = new Comment();
        comment.setContent(filteredContent);
        return comment;
    }
}
```

## 完整示例

### 示例1：评论内容审核

```java
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    
    private final CommentService commentService;
    
    /**
     * 创建评论（自动过滤敏感词）
     */
    @PostMapping
    public Result<Comment> createComment(@RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.createComment(commentDTO);
        return Result.success(comment);
    }
}

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final SensitiveWordService sensitiveWordService;
    
    /**
     * 创建评论（自动过滤敏感词）
     */
    public Comment createComment(CommentDTO commentDTO) {
        // 检查是否包含敏感词
        if (sensitiveWordService.containsSensitiveWord(commentDTO.getContent())) {
            // 自动过滤敏感词
            String filteredContent = sensitiveWordService.replaceSensitiveWords(
                commentDTO.getContent(),
                "***"
            );
            
            log.info("评论内容包含敏感词，已自动过滤: original={}, filtered={}", 
                    commentDTO.getContent(), filteredContent);
            
            commentDTO.setContent(filteredContent);
        }
        
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUserId(commentDTO.getUserId());
        return commentRepository.save(comment);
    }
}
```

### 示例2：用户输入验证

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final SensitiveWordService sensitiveWordService;
    
    /**
     * 创建用户（验证用户名和昵称）
     */
    public User createUser(UserDTO userDTO) {
        // 验证用户名
        if (sensitiveWordService.containsSensitiveWord(userDTO.getUsername())) {
            throw new BusinessException("用户名包含敏感词，请修改后重试");
        }
        
        // 验证昵称
        if (userDTO.getNickname() != null 
                && sensitiveWordService.containsSensitiveWord(userDTO.getNickname())) {
            // 自动过滤敏感词
            String filteredNickname = sensitiveWordService.replaceSensitiveWords(
                userDTO.getNickname(),
                "***"
            );
            userDTO.setNickname(filteredNickname);
            log.info("用户昵称包含敏感词，已自动过滤: original={}, filtered={}", 
                    userDTO.getNickname(), filteredNickname);
        }
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setNickname(userDTO.getNickname());
        return userRepository.save(user);
    }
}
```

### 示例3：动态更新敏感词库

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordManageService {
    
    private final SensitiveWordService sensitiveWordService;
    private final SensitiveWordRepository sensitiveWordRepository;
    
    /**
     * 添加敏感词
     */
    public void addSensitiveWord(String word) {
        // 保存到数据库
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWord(word);
        sensitiveWordRepository.save(sensitiveWord);
        
        // 重新加载敏感词库
        reloadSensitiveWords();
    }
    
    /**
     * 删除敏感词
     */
    public void removeSensitiveWord(String word) {
        // 从数据库删除
        sensitiveWordRepository.deleteByWord(word);
        
        // 重新加载敏感词库
        reloadSensitiveWords();
    }
    
    /**
     * 重新加载敏感词库
     */
    public void reloadSensitiveWords() {
        List<String> words = sensitiveWordRepository.findAllWords();
        sensitiveWordService.initSensitiveWords(words);
        log.info("敏感词库重新加载完成: count={}", words.size());
    }
}
```

### 示例4：批量内容审核

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentAuditService {
    
    private final SensitiveWordService sensitiveWordService;
    
    /**
     * 批量审核内容
     */
    public List<AuditResult> batchAudit(List<String> contents) {
        List<AuditResult> results = new ArrayList<>();
        
        for (String content : contents) {
            AuditResult result = new AuditResult();
            result.setContent(content);
            
            // 检查是否包含敏感词
            boolean contains = sensitiveWordService.containsSensitiveWord(content);
            result.setContainsSensitiveWord(contains);
            
            if (contains) {
                // 查找所有敏感词
                Set<String> sensitiveWords = sensitiveWordService.findSensitiveWords(content);
                result.setSensitiveWords(sensitiveWords);
                
                // 生成过滤后的内容
                String filtered = sensitiveWordService.replaceSensitiveWords(content, "***");
                result.setFilteredContent(filtered);
            }
            
            results.add(result);
        }
        
        return results;
    }
}
```

## DFA 算法说明

### 算法原理

DFA（确定有限状态自动机）是一种高效的字符串匹配算法：

1. **构建敏感词树**：将敏感词构建成树形结构（Trie 树）
2. **节点表示字符**：每个节点代表一个字符，从根节点到叶子节点构成一个敏感词
3. **状态转换**：根据输入字符在树中进行状态转换
4. **匹配检测**：如果到达结束节点，说明匹配到敏感词

### 算法优势

- **高效**：时间复杂度 O(n*m)，n 为文本长度，m 为敏感词平均长度
- **支持大量敏感词**：适合敏感词数量较多的场景
- **支持部分匹配**：可以检测部分匹配的敏感词
- **内存占用小**：使用 Trie 树结构，内存占用相对较小

### 示例

假设敏感词库包含：["敏感", "敏感词", "测试"]

构建的 DFA 树结构：
```
根节点
├── 敏
│   └── 感
│       ├── (isEnd=1)  // "敏感" 是一个完整的敏感词
│       └── 词 (isEnd=1)  // "敏感词" 是一个完整的敏感词
└── 测
    └── 试 (isEnd=1)  // "测试" 是一个完整的敏感词
```

检测文本 "这是一个敏感词测试"：
- 从 "敏" 开始匹配，找到 "敏感" 和 "敏感词"
- 从 "测" 开始匹配，找到 "测试"

## 配置说明

### 默认配置

组件会在应用启动时自动初始化默认敏感词库。可以通过以下方式自定义：

### 从配置文件加载

在 `application.yml` 中配置：

```yaml
sensitive:
  words:
    - 敏感词1
    - 敏感词2
    - 敏感词3
```

然后在代码中加载：

```java
@Service
@RequiredArgsConstructor
public class SensitiveWordInitService {
    
    private final SensitiveWordService sensitiveWordService;
    
    @Value("${sensitive.words:}")
    private List<String> sensitiveWords;
    
    @PostConstruct
    public void init() {
        if (sensitiveWords != null && !sensitiveWords.isEmpty()) {
            sensitiveWordService.initSensitiveWords(sensitiveWords);
        }
    }
}
```

## 最佳实践

### 1. 初始化敏感词库

在应用启动时初始化敏感词库：

```java
@PostConstruct
public void init() {
    List<String> words = loadSensitiveWords();
    sensitiveWordService.initSensitiveWords(words);
}
```

### 2. 选择合适的处理策略

根据业务需求选择合适的处理策略：

```java
// 策略1：拒绝包含敏感词的内容
if (sensitiveWordService.containsSensitiveWord(content)) {
    throw new BusinessException("内容包含敏感词");
}

// 策略2：自动过滤敏感词
String filtered = sensitiveWordService.replaceSensitiveWords(content, "***");

// 策略3：记录敏感词并审核
Set<String> words = sensitiveWordService.findSensitiveWords(content);
if (!words.isEmpty()) {
    // 记录审核日志
    auditService.recordAudit(content, words);
    // 标记为待审核
    content.setStatus(AuditStatus.PENDING);
}
```

### 3. 动态更新敏感词库

支持动态更新敏感词库，无需重启应用：

```java
@Service
@RequiredArgsConstructor
public class SensitiveWordManageService {
    
    private final SensitiveWordService sensitiveWordService;
    
    public void updateSensitiveWords(List<String> words) {
        sensitiveWordService.initSensitiveWords(words);
    }
}
```

### 4. 性能优化

对于大量文本处理，考虑批量处理：

```java
public List<String> batchFilter(List<String> contents) {
    return contents.stream()
        .map(content -> sensitiveWordService.replaceSensitiveWords(content, "***"))
        .collect(Collectors.toList());
}
```

### 5. 日志记录

记录敏感词检测和过滤的日志，便于审计：

```java
if (sensitiveWordService.containsSensitiveWord(content)) {
    Set<String> words = sensitiveWordService.findSensitiveWords(content);
    log.warn("检测到敏感词: content={}, words={}", content, words);
    // 处理逻辑...
}
```

## 常见问题

### 1. 敏感词库未初始化？

**答**：检查以下几点：
- Spring Boot 是否能够扫描到 `com.example.sensitive` 包
- `SensitiveWordService` 是否已注入
- 是否手动调用了 `initSensitiveWords` 方法

### 2. 检测不到敏感词？

**答**：检查以下几点：
- 敏感词库是否已初始化
- 敏感词是否正确添加到词库
- 文本中的敏感词是否与词库中的完全匹配（包括大小写、空格等）

### 3. 如何支持忽略大小写？

**答**：在初始化敏感词库时，将所有敏感词转换为小写：

```java
List<String> words = sensitiveWords.stream()
    .map(String::toLowerCase)
    .collect(Collectors.toList());
sensitiveWordService.initSensitiveWords(words);
```

检测时也将文本转换为小写：

```java
boolean contains = sensitiveWordService.containsSensitiveWord(text.toLowerCase());
```

### 4. 如何支持模糊匹配？

**答**：当前实现支持部分匹配，如果需要更复杂的模糊匹配（如拼音匹配、同音字匹配），需要扩展算法或使用第三方库。

### 5. 性能如何？

**答**：
- DFA 算法时间复杂度为 O(n*m)，n 为文本长度，m 为敏感词平均长度
- 对于一般场景（文本长度 < 10000，敏感词数量 < 10000），性能足够
- 如果敏感词数量很大（> 100000），建议优化算法或使用更高效的实现

### 6. 如何从文件加载敏感词？

**答**：

```java
@PostConstruct
public void init() {
    try {
        List<String> words = Files.readAllLines(
            Paths.get("sensitive-words.txt"),
            StandardCharsets.UTF_8
        );
        sensitiveWordService.initSensitiveWords(words);
    } catch (IOException e) {
        log.error("加载敏感词文件失败", e);
    }
}
```

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供敏感词检测功能
  - 提供敏感词查找功能
  - 提供敏感词替换功能
  - 基于 DFA 算法实现

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

