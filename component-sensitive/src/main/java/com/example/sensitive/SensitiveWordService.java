package com.example.sensitive;

import java.util.List;
import java.util.Set;

/**
 * 敏感词服务接口
 * <p>
 * 提供敏感词检测和过滤功能，基于 DFA（确定有限状态自动机）算法实现
 * 支持敏感词检测、查找、替换等操作
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>用户输入内容审核</li>
 *   <li>评论、帖子内容过滤</li>
 *   <li>聊天消息过滤</li>
 *   <li>搜索关键词过滤</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入敏感词服务
 * @Autowired
 * private SensitiveWordService sensitiveWordService;
 * 
 * // 初始化敏感词库
 * List&lt;String&gt; words = Arrays.asList("敏感词1", "敏感词2");
 * sensitiveWordService.initSensitiveWords(words);
 * 
 * // 检查是否包含敏感词
 * boolean contains = sensitiveWordService.containsSensitiveWord("这是一段包含敏感词的文本");
 * 
 * // 查找所有敏感词
 * Set&lt;String&gt; words = sensitiveWordService.findSensitiveWords("这是一段包含敏感词的文本");
 * 
 * // 替换敏感词
 * String filtered = sensitiveWordService.replaceSensitiveWords("这是一段包含敏感词的文本", "***");
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SensitiveWordService {
    
    /**
     * 初始化敏感词库
     * <p>
     * 使用 DFA 算法构建敏感词树，提高检测效率
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>会清空原有的敏感词库</li>
     *   <li>建议在应用启动时初始化</li>
     *   <li>可以从配置文件、数据库或文件加载敏感词</li>
     * </ul>
     * </p>
     * 
     * @param words 敏感词列表，不能为 null
     * @throws IllegalArgumentException 如果 words 为 null
     */
    void initSensitiveWords(List<String> words);

    /**
     * 检查文本是否包含敏感词
     * <p>
     * 快速检查文本中是否包含任何敏感词
     * </p>
     * 
     * @param text 待检查的文本，如果为 null 或空字符串，返回 false
     * @return true 表示包含敏感词，false 表示不包含
     */
    boolean containsSensitiveWord(String text);

    /**
     * 获取文本中的所有敏感词
     * <p>
     * 查找文本中所有匹配的敏感词，返回敏感词集合
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>如果文本中包含多个相同的敏感词，集合中只会包含一个</li>
     *   <li>返回的集合是去重后的结果</li>
     * </ul>
     * </p>
     * 
     * @param text 待检查的文本，如果为 null 或空字符串，返回空集合
     * @return 敏感词集合，如果没有找到敏感词，返回空集合
     */
    Set<String> findSensitiveWords(String text);

    /**
     * 替换敏感词
     * <p>
     * 将文本中的所有敏感词替换为指定的替换字符串
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>使用正则表达式替换，可能会替换部分匹配的内容</li>
     *   <li>如果 replacement 为 null，会使用默认替换字符串 "***"</li>
     * </ul>
     * </p>
     * 
     * @param text 待处理的文本，如果为 null 或空字符串，直接返回
     * @param replacement 替换字符串，如果为 null，使用默认值 "***"
     * @return 替换后的文本
     */
    String replaceSensitiveWords(String text, String replacement);
}

