package com.example.user.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.UserManagementRepository;
import com.example.user.infrastructure.mapper.UserMapper;
import com.example.user.infrastructure.po.UserPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理仓储实现
 * <p>
 * 实现用户管理仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserManagementRepositoryImpl implements UserManagementRepository {

    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        try {
            // 显式添加逻辑删除过滤条件，确保只查询未删除的记录
            QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("deleted", 0);
            List<UserPO> pos = userMapper.selectList(queryWrapper);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有用户失败", e);
            throw new RuntimeException("查询所有用户失败: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(Long id) {
        Assert.notNull(id, "用户ID不能为空");
        
        try {
            UserPO po = userMapper.selectById(id);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询用户失败: userId={}", id, e);
            throw new RuntimeException("查询用户失败: " + e.getMessage(), e);
        }
    }

    @Override
    public User save(User user) {
        Assert.notNull(user, "用户不能为空");
        
        try {
            UserPO po = convertToPO(user);
            if (user.getId() == null) {
                userMapper.insert(po);
                user.setId(po.getId());
            } else {
                userMapper.updateById(po);
            }
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("保存用户失败: username={}", user.getUsername(), e);
            throw new RuntimeException("保存用户失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "用户ID不能为空");
        
        try {
            // 使用自定义SQL直接更新deleted字段，绕过逻辑删除拦截器
            int updateCount = userMapper.logicalDeleteById(id);
            if (updateCount > 0) {
                log.info("删除用户成功: userId={}", id);
            } else {
                log.warn("删除用户失败，用户不存在或已被删除: userId={}", id);
            }
        } catch (Exception e) {
            log.error("删除用户失败: userId={}", id, e);
            throw new RuntimeException("删除用户失败: " + e.getMessage(), e);
        }
    }

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
        user.setDeleted(po.getDeleted());
        return user;
    }
    
    private UserPO convertToPO(User user) {
        if (user == null) {
            return null;
        }
        
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
        po.setDeleted(user.getDeleted());
        return po;
    }
}

