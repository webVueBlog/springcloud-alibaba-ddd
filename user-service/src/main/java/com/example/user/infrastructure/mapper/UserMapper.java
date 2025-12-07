package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
    
    /**
     * 逻辑删除用户（直接更新deleted字段，绕过逻辑删除拦截器）
     * 使用原生SQL，不经过MyBatis Plus的逻辑删除拦截器
     * 
     * @param id 用户ID
     * @return 更新记录数
     */
    @Update("UPDATE sys_user SET deleted = 1 WHERE id = #{id} AND (deleted = 0 OR deleted IS NULL)")
    int logicalDeleteById(@Param("id") Long id);
}

