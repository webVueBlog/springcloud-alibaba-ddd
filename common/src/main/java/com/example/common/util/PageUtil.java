package com.example.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页工具类
 * <p>
 * 提供分页相关的工具方法，包括创建 MyBatis-Plus 分页对象、分页请求参数、分页响应结果等
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 创建分页对象
 * Page&lt;User&gt; page = PageUtil.createPage(1, 10);
 * 
 * // 使用分页请求参数
 * PageRequest request = new PageRequest();
 * request.setCurrent(1);
 * request.setSize(20);
 * 
 * // 使用分页响应结果
 * PageResult&lt;User&gt; result = new PageResult&lt;&gt;();
 * result.setTotal(100L);
 * result.setCurrent(1);
 * result.setSize(10);
 * result.setRecords(userList);
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public class PageUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private PageUtil() {
        throw new UnsupportedOperationException("PageUtil class cannot be instantiated");
    }

    /**
     * 创建 MyBatis-Plus 分页对象
     * <p>
     * 如果 current 或 size 为 null 或小于1，则使用默认值（current=1, size=10）
     * </p>
     * 
     * @param <T> 分页数据类型
     * @param current 当前页码，从1开始
     * @param size 每页大小
     * @return MyBatis-Plus 分页对象
     */
    public static <T> Page<T> createPage(Integer current, Integer size) {
        if (current == null || current < 1) {
            current = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        return new Page<>(current, size);
    }

    /**
     * 分页请求参数
     * <p>
     * 用于接收前端的分页请求参数
     * </p>
     */
    @Data
    public static class PageRequest {
        /** 当前页码，从1开始，默认1 */
        private Integer current = 1;
        
        /** 每页大小，默认10 */
        private Integer size = 10;
    }

    /**
     * 分页响应结果
     * <p>
     * 用于封装分页查询的响应结果
     * </p>
     * 
     * @param <T> 数据类型
     */
    @Data
    public static class PageResult<T> {
        /** 总记录数 */
        private Long total;
        
        /** 当前页码 */
        private Integer current;
        
        /** 每页大小 */
        private Integer size;
        
        /** 数据列表 */
        private List<T> records;
    }
}

