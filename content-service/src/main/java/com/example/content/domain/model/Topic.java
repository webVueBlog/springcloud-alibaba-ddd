package com.example.content.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专题领域模型
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Topic extends BaseEntity {
    
    /** 专题名称 */
    private String topicName;
    
    /** 专题编码 */
    private String topicCode;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 专题描述 */
    private String description;
    
    /** 文章数量 */
    private Integer articleCount;
    
    /** 关注数 */
    private Integer followerCount;
    
    /** 访问级别：PUBLIC-公开，PRIVATE-私有，PAID-付费可见，SPECIFIED-指定用户可见 */
    private String accessLevel;
    
    /** 状态：1-正常，0-禁用 */
    private Integer status;
}

