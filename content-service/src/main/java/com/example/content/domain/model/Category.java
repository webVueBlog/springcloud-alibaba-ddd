package com.example.content.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分类领域模型
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    
    /** 父分类ID */
    private Long parentId;
    
    /** 分类编码 */
    private String categoryCode;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 分类图标 */
    private String icon;
    
    /** 分类描述 */
    private String description;
    
    /** 排序号 */
    private Integer sort;
    
    /** 状态：1-正常，0-禁用 */
    private Integer status;
    
    /** 子分类列表（不持久化） */
    private List<Category> children;
}

