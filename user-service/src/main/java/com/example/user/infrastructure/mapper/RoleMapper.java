package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.po.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 角色 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {
    
    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码
     * @return 角色持久化对象
     */
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND deleted = 0")
    RolePO selectByRoleCode(@Param("roleCode") String roleCode);
    
    /**
     * 逻辑删除角色（直接更新deleted字段，绕过逻辑删除拦截器）
     * 使用原生SQL，不经过MyBatis Plus的逻辑删除拦截器
     * 
     * @param id 角色ID
     * @return 更新记录数
     */
    @Update("UPDATE sys_role SET deleted = 1 WHERE id = #{id} AND (deleted = 0 OR deleted IS NULL)")
    int logicalDeleteById(@Param("id") Long id);
}

