package com.example.sensitive.impl;

import com.example.sensitive.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 敏感词服务实现
 * <p>
 * 基于 DFA（确定有限状态自动机）算法实现敏感词检测
 * </p>
 * <p>
 * DFA 算法原理：
 * <ul>
 *   <li>将敏感词构建成树形结构（Trie 树）</li>
 *   <li>每个节点代表一个字符，从根节点到叶子节点构成一个敏感词</li>
 *   <li>检测时从文本的每个字符开始，在树中查找匹配的路径</li>
 *   <li>时间复杂度：O(n*m)，n 为文本长度，m 为敏感词平均长度</li>
 * </ul>
 * </p>
 * <p>
 * 优势：
 * <ul>
 *   <li>检测效率高，适合大量敏感词场景</li>
 *   <li>支持中文、英文、数字等多种字符</li>
 *   <li>支持部分匹配和完全匹配</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {

    /** 敏感词树（DFA 数据结构），使用 Map 实现 Trie 树 */
    private final Map<String, Object> sensitiveWordMap = new HashMap<>();
    
    /** 默认替换字符串 */
    private static final String DEFAULT_REPLACEMENT = "***";

    /**
     * 初始化敏感词库
     * <p>
     * 使用 DFA 算法构建敏感词树
     * </p>
     * 
     * @param words 敏感词列表
     */
    @Override
    public void initSensitiveWords(List<String> words) {
        if (words == null) {
            throw new IllegalArgumentException("敏感词列表不能为空");
        }
        
        try {
            sensitiveWordMap.clear();
            int count = 0;
            for (String word : words) {
                if (word != null && !word.trim().isEmpty()) {
                    addWord(word.trim());
                    count++;
                }
            }
            log.info("初始化敏感词库成功: count={}", count);
        } catch (Exception e) {
            log.error("初始化敏感词库失败", e);
            throw new RuntimeException("初始化敏感词库失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加敏感词到树中
     * <p>
     * 将敏感词添加到 DFA 树中，构建 Trie 树结构
     * </p>
     * 
     * @param word 敏感词
     */
    @SuppressWarnings("unchecked")
    private void addWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        
        Map<String, Object> nowMap = sensitiveWordMap;
        
        // 遍历敏感词的每个字符
        for (int i = 0; i < word.length(); i++) {
            String charWord = String.valueOf(word.charAt(i));
            
            // 获取当前字符对应的子节点
            Object tempMap = nowMap.get(charWord);
            
            if (tempMap != null) {
                // 如果子节点已存在，继续向下查找
                nowMap = (Map<String, Object>) tempMap;
            } else {
                // 如果子节点不存在，创建新节点
                Map<String, Object> newMap = new HashMap<>();
                newMap.put("isEnd", "0");  // 标记为非结束节点
                nowMap.put(charWord, newMap);
                nowMap = newMap;
            }
            
            // 如果是最后一个字符，标记为结束节点
            if (i == word.length() - 1) {
                nowMap.put("isEnd", "1");  // 标记为结束节点
            }
        }
    }

    /**
     * 检查文本是否包含敏感词
     * 
     * @param text 待检查的文本
     * @return true 表示包含敏感词，false 表示不包含
     */
    @Override
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        if (sensitiveWordMap.isEmpty()) {
            log.warn("敏感词库未初始化，无法检测敏感词");
            return false;
        }
        
        return !findSensitiveWords(text).isEmpty();
    }

    /**
     * 获取文本中的所有敏感词
     * 
     * @param text 待检查的文本
     * @return 敏感词集合
     */
    @Override
    public Set<String> findSensitiveWords(String text) {
        Set<String> sensitiveWordSet = new HashSet<>();
        
        if (text == null || text.isEmpty()) {
            return sensitiveWordSet;
        }
        
        if (sensitiveWordMap.isEmpty()) {
            log.warn("敏感词库未初始化，无法查找敏感词");
            return sensitiveWordSet;
        }
        
        try {
            // 从文本的每个字符开始检查
            for (int i = 0; i < text.length(); i++) {
                int length = checkSensitiveWord(text, i);
                if (length > 0) {
                    // 找到敏感词，添加到集合中
                    String sensitiveWord = text.substring(i, i + length);
                    sensitiveWordSet.add(sensitiveWord);
                    // 跳过已匹配的字符，避免重复检测
                    i = i + length - 1;
                }
            }
            
            if (!sensitiveWordSet.isEmpty()) {
                log.debug("检测到敏感词: text={}, words={}", text, sensitiveWordSet);
            }
        } catch (Exception e) {
            log.error("查找敏感词失败: text={}", text, e);
        }
        
        return sensitiveWordSet;
    }

    /**
     * 替换敏感词
     * 
     * @param text 待处理的文本
     * @param replacement 替换字符串
     * @return 替换后的文本
     */
    @Override
    public String replaceSensitiveWords(String text, String replacement) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        if (sensitiveWordMap.isEmpty()) {
            log.warn("敏感词库未初始化，无法替换敏感词");
            return text;
        }
        
        // 如果替换字符串为 null，使用默认值
        if (replacement == null) {
            replacement = DEFAULT_REPLACEMENT;
        }
        
        try {
            String result = text;
            Set<String> sensitiveWords = findSensitiveWords(text);
            
            // 替换所有敏感词
            for (String word : sensitiveWords) {
                // 使用正则表达式替换，需要转义特殊字符
                String escapedWord = escapeRegex(word);
                result = result.replaceAll(escapedWord, replacement);
            }
            
            if (!sensitiveWords.isEmpty()) {
                log.debug("替换敏感词: original={}, replaced={}, words={}", 
                        text, result, sensitiveWords);
            }
            
            return result;
        } catch (Exception e) {
            log.error("替换敏感词失败: text={}", text, e);
            return text;
        }
    }

    /**
     * 检查从指定位置开始的敏感词
     * <p>
     * 从文本的 beginIndex 位置开始，在敏感词树中查找匹配的敏感词
     * </p>
     * 
     * @param text 待检查的文本
     * @param beginIndex 开始检查的位置
     * @return 匹配的敏感词长度，如果没有匹配返回 0
     */
    @SuppressWarnings("unchecked")
    private int checkSensitiveWord(String text, int beginIndex) {
        boolean flag = false;  // 是否匹配到完整的敏感词
        int matchFlag = 0;     // 匹配的字符数
        Map<String, Object> nowMap = sensitiveWordMap;
        
        // 从 beginIndex 开始逐个字符匹配
        for (int i = beginIndex; i < text.length(); i++) {
            String word = String.valueOf(text.charAt(i));
            
            // 在树中查找当前字符
            nowMap = (Map<String, Object>) nowMap.get(word);
            
            if (nowMap != null) {
                // 找到匹配的字符，继续向下查找
                matchFlag++;
                
                // 检查是否是结束节点（完整匹配到敏感词）
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    break;  // 找到完整的敏感词，退出循环
                }
            } else {
                // 没有找到匹配的字符，退出循环
                break;
            }
        }
        
        // 如果没有匹配到完整的敏感词，返回 0
        if (!flag) {
            matchFlag = 0;
        }
        
        return matchFlag;
    }

    /**
     * 转义正则表达式特殊字符
     * <p>
     * 将字符串中的正则表达式特殊字符进行转义，以便在 replaceAll 中使用
     * </p>
     * 
     * @param word 待转义的字符串
     * @return 转义后的字符串
     */
    private String escapeRegex(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        
        // 转义正则表达式特殊字符
        return word.replaceAll("([\\\\*+\\[\\](){}^$|?.])", "\\\\$1");
    }
}

