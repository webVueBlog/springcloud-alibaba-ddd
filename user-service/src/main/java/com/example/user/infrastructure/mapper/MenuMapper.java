package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.po.MenuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 菜单 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuPO> {
    
    /**
     * 根据菜单编码查询菜单
     * 
     * @param menuCode 菜单编码
     * @return 菜单持久化对象
     */
    @Select("SELECT * FROM sys_menu WHERE menu_code = #{menuCode} AND deleted = 0")
    MenuPO selectByMenuCode(@Param("menuCode") String menuCode);
    
    /**
     * 根据父菜单ID查询子菜单列表
     * 
     * @param parentId 父菜单ID，如果为 null 则查询顶级菜单
     * @return 子菜单列表
     */
    @Select("SELECT * FROM sys_menu WHERE (parent_id = #{parentId} OR (#{parentId} IS NULL AND parent_id IS NULL)) AND deleted = 0 ORDER BY sort ASC")
    List<MenuPO> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 逻辑删除菜单（直接更新deleted字段，绕过逻辑删除拦截器）
     * 使用原生SQL，不经过MyBatis Plus的逻辑删除拦截器
     * 
     * @param id 菜单ID
     * @return 更新记录数
     */
    @Update("UPDATE sys_menu SET deleted = 1 WHERE id = #{id} AND (deleted = 0 OR deleted IS NULL)")
    int logicalDeleteById(@Param("id") Long id);
}

