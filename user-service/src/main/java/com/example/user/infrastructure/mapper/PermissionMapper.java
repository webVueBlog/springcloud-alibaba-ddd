package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 权限 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionPO> {
    
    /**
     * 根据权限编码查询权限
     * 
     * @param permissionCode 权限编码
     * @return 权限持久化对象
     */
    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode} AND deleted = 0")
    PermissionPO selectByPermissionCode(@Param("permissionCode") String permissionCode);
}

