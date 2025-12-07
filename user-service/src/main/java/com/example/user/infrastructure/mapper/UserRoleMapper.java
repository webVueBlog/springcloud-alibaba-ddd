package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.po.RolePO;
import com.example.user.infrastructure.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePO> {
    
    /**
     * 根据用户ID查询用户角色关联列表
     * 
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId} AND deleted = 0")
    List<UserRolePO> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND ur.deleted = 0")
    List<RolePO> selectRolesByUserId(@Param("userId") Long userId);
}

