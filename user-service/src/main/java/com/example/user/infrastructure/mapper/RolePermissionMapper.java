package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.po.PermissionPO;
import com.example.user.infrastructure.po.RolePermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionPO> {
    
    /**
     * 根据角色ID查询角色权限关联列表
     * 
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId} AND deleted = 0")
    List<RolePermissionPO> selectByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0 AND rp.deleted = 0")
    List<PermissionPO> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}

