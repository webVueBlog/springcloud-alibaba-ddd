package com.example.auth.infrastructure.repository;

import com.example.auth.domain.model.User;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.infrastructure.mapper.UserMapper;
import com.example.auth.infrastructure.po.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 用户仓储实现
 * <p>
 * 实现用户仓储接口，负责用户领域模型与持久化对象之间的转换
 * 使用 MyBatis-Plus 进行数据库操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        UserPO po = userMapper.selectByUsername(username);
        return convertToDomain(po);
    }

    @Override
    public User findByPhone(String phone) {
        UserPO po = userMapper.selectByPhone(phone);
        return convertToDomain(po);
    }

    @Override
    public User findByEmail(String email) {
        UserPO po = userMapper.selectByEmail(email);
        return convertToDomain(po);
    }

    @Override
    public User findByWechatOpenId(String openId) {
        UserPO po = userMapper.selectByWechatOpenId(openId);
        return convertToDomain(po);
    }

    @Override
    public User save(User user) {
        UserPO po = convertToPO(user);
        if (user.getId() == null) {
            userMapper.insert(po);
            user.setId(po.getId());
        } else {
            userMapper.updateById(po);
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return convertToDomain(po);
    }

    /**
     * 将持久化对象转换为领域模型
     * 
     * @param po 用户持久化对象
     * @return 用户领域模型，如果 po 为 null 则返回 null
     */
    private User convertToDomain(UserPO po) {
        if (po == null) {
            return null;
        }
        User user = new User();
        user.setId(po.getId());
        user.setUsername(po.getUsername());
        user.setPassword(po.getPassword());
        user.setPhone(po.getPhone());
        user.setEmail(po.getEmail());
        user.setWechatOpenId(po.getWechatOpenId());
        user.setWechatUnionId(po.getWechatUnionId());
        user.setStatus(po.getStatus());
        user.setSalt(po.getSalt());
        user.setCreateTime(po.getCreateTime());
        user.setUpdateTime(po.getUpdateTime());
        return user;
    }

    /**
     * 将领域模型转换为持久化对象
     * 
     * @param user 用户领域模型
     * @return 用户持久化对象
     */
    private UserPO convertToPO(User user) {
        UserPO po = new UserPO();
        po.setId(user.getId());
        po.setUsername(user.getUsername());
        po.setPassword(user.getPassword());
        po.setPhone(user.getPhone());
        po.setEmail(user.getEmail());
        po.setWechatOpenId(user.getWechatOpenId());
        po.setWechatUnionId(user.getWechatUnionId());
        po.setStatus(user.getStatus());
        po.setSalt(user.getSalt());
        po.setCreateTime(user.getCreateTime());
        po.setUpdateTime(user.getUpdateTime());
        return po;
    }
}

