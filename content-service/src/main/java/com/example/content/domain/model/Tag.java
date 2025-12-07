package com.example.content.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签领域模型
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseEntity {
    
    /** 标签名称 */
    private String tagName;
    
    /** 标签编码 */
    private String tagCode;
    
    /** 标签描述 */
    private String description;
    
    /** 使用次数 */
    private Integer useCount;
    
    /** 状态：1-正常，0-禁用 */
    private Integer status;
}

