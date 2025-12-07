package com.example.auth.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.auth.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 * <p>
 * MyBatis Mapper 接口，定义用户数据的数据库操作方法
 * 继承 MyBatis-Plus 的 BaseMapper 提供基础的 CRUD 操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
    
    /**
     * 根据用户名查询用户
     * <p>只查询未删除的用户（deleted = 0）</p>
     * 
     * @param username 用户名
     * @return 用户持久化对象，如果不存在返回 null
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    UserPO selectByUsername(String username);

    /**
     * 根据手机号查询用户
     * <p>只查询未删除的用户（deleted = 0）</p>
     * 
     * @param phone 手机号
     * @return 用户持久化对象，如果不存在返回 null
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    UserPO selectByPhone(String phone);

    /**
     * 根据邮箱查询用户
     * <p>只查询未删除的用户（deleted = 0）</p>
     * 
     * @param email 邮箱地址
     * @return 用户持久化对象，如果不存在返回 null
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    UserPO selectByEmail(String email);

    /**
     * 根据微信OpenID查询用户
     * <p>只查询未删除的用户（deleted = 0）</p>
     * 
     * @param openId 微信OpenID
     * @return 用户持久化对象，如果不存在返回 null
     */
    @Select("SELECT * FROM sys_user WHERE wechat_open_id = #{openId} AND deleted = 0")
    UserPO selectByWechatOpenId(String openId);
}

