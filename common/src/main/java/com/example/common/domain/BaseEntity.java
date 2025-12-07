package com.example.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * <p>
 * 所有领域实体类的基类，提供通用的字段：
 * <ul>
 *   <li>id: 主键ID，使用雪花算法自动生成</li>
 *   <li>createTime: 创建时间</li>
 *   <li>updateTime: 更新时间</li>
 *   <li>deleted: 逻辑删除标记（0-未删除，1-已删除）</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * public class User extends BaseEntity {
 *     private String username;
 *     private String password;
 *     // ... 其他字段
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID，使用雪花算法自动生成 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    private Integer deleted;
}

