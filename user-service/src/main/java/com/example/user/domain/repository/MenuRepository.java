package com.example.user.domain.repository;

import com.example.user.domain.model.Menu;

import java.util.List;

/**
 * 菜单仓储接口
 * <p>
 * 定义菜单的持久化操作，属于领域层，不依赖具体的技术实现
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MenuRepository {
    
    /**
     * 根据ID查询菜单
     * 
     * @param id 菜单ID，不能为 null
     * @return 菜单领域模型，如果不存在返回 null
     */
    Menu findById(Long id);
    
    /**
     * 根据菜单编码查询菜单
     * 
     * @param menuCode 菜单编码，不能为 null 或空字符串
     * @return 菜单领域模型，如果不存在返回 null
     */
    Menu findByMenuCode(String menuCode);
    
    /**
     * 查询所有菜单
     * 
     * @return 菜单列表，如果不存在返回空列表
     */
    List<Menu> findAll();
    
    /**
     * 根据父菜单ID查询子菜单列表
     * 
     * @param parentId 父菜单ID，如果为 null 或 0 则查询顶级菜单
     * @return 子菜单列表
     */
    List<Menu> findByParentId(Long parentId);
    
    /**
     * 根据条件查询菜单列表
     * 
     * @param menuName 菜单名称（可选，模糊匹配）
     * @param menuCode 菜单编码（可选，模糊匹配）
     * @param status 状态（可选）
     * @return 菜单列表
     */
    List<Menu> findByCondition(String menuName, String menuCode, Integer status);
    
    /**
     * 保存菜单
     * 
     * @param menu 菜单领域模型，不能为 null
     * @return 保存后的菜单领域模型
     */
    Menu save(Menu menu);
    
    /**
     * 删除菜单（逻辑删除）
     * 
     * @param id 菜单ID，不能为 null
     */
    void delete(Long id);
}

