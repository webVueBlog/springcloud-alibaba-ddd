package com.example.user.infrastructure.repository;

import com.example.user.domain.model.Role;
import com.example.user.domain.model.UserRole;
import com.example.user.domain.repository.UserRoleRepository;
import com.example.user.infrastructure.mapper.RoleMapper;
import com.example.user.infrastructure.mapper.UserRoleMapper;
import com.example.user.infrastructure.po.RolePO;
import com.example.user.infrastructure.po.UserRolePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联仓储实现
 * <p>
 * 实现用户角色关联仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleMapper userRoleMapper;

    @Override
    public List<UserRole> findByUserId(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            List<UserRolePO> pos = userRoleMapper.selectByUserId(userId);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户角色关联失败: userId={}", userId, e);
            throw new RuntimeException("查询用户角色关联失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            List<RolePO> pos = userRoleMapper.selectRolesByUserId(userId);
            return pos.stream()
                    .map(this::convertRoleToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户角色失败: userId={}", userId, e);
            throw new RuntimeException("查询用户角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public UserRole save(UserRole userRole) {
        Assert.notNull(userRole, "用户角色关联不能为空");
        
        try {
            // 先检查是否已存在（包括已删除的记录）
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRolePO> wrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(UserRolePO::getUserId, userRole.getUserId())
                   .eq(UserRolePO::getRoleId, userRole.getRoleId());
            
            UserRolePO existingPO = userRoleMapper.selectList(wrapper).stream().findFirst().orElse(null);
            
            UserRolePO po = convertToPO(userRole);
            
            if (existingPO != null) {
                // 如果已存在（包括已删除的），则恢复并更新
                existingPO.setDeleted(0);
                if (userRole.getId() != null) {
                    existingPO.setId(userRole.getId());
                }
                userRoleMapper.updateById(existingPO);
                po = existingPO;
                userRole.setId(existingPO.getId());
            } else if (userRole.getId() != null) {
                // 如果指定了ID，直接更新
                userRoleMapper.updateById(po);
            } else {
                // 如果不存在，插入新记录
                userRoleMapper.insert(po);
                userRole.setId(po.getId());
            }
            
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("保存用户角色关联失败: userId={}, roleId={}", 
                    userRole.getUserId(), userRole.getRoleId(), e);
            throw new RuntimeException("保存用户角色关联失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByUserIdAndRoleId(Long userId, Long roleId) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.notNull(roleId, "角色ID不能为空");
        
        try {
            // 逻辑删除：更新 deleted 字段
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRolePO> wrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(UserRolePO::getUserId, userId)
                   .eq(UserRolePO::getRoleId, roleId)
                   .eq(UserRolePO::getDeleted, 0);
            
            UserRolePO po = userRoleMapper.selectList(wrapper).stream().findFirst().orElse(null);
            
            if (po != null) {
                po.setDeleted(1);
                userRoleMapper.updateById(po);
                log.debug("删除用户角色关联成功: userId={}, roleId={}", userId, roleId);
            }
        } catch (Exception e) {
            log.error("删除用户角色关联失败: userId={}, roleId={}", userId, roleId, e);
            throw new RuntimeException("删除用户角色关联失败: " + e.getMessage(), e);
        }
    }

    private UserRole convertToDomain(UserRolePO po) {
        if (po == null) {
            return null;
        }
        
        UserRole userRole = new UserRole();
        userRole.setId(po.getId());
        userRole.setUserId(po.getUserId());
        userRole.setRoleId(po.getRoleId());
        userRole.setCreateTime(po.getCreateTime());
        userRole.setUpdateTime(po.getUpdateTime());
        userRole.setDeleted(po.getDeleted());
        return userRole;
    }

    private Role convertRoleToDomain(RolePO po) {
        if (po == null) {
            return null;
        }
        
        Role role = new Role();
        role.setId(po.getId());
        role.setRoleCode(po.getRoleCode());
        role.setRoleName(po.getRoleName());
        role.setDescription(po.getDescription());
        role.setStatus(po.getStatus());
        role.setCreateTime(po.getCreateTime());
        role.setUpdateTime(po.getUpdateTime());
        role.setDeleted(po.getDeleted());
        return role;
    }

    private UserRolePO convertToPO(UserRole userRole) {
        if (userRole == null) {
            return null;
        }
        
        UserRolePO po = new UserRolePO();
        po.setId(userRole.getId());
        po.setUserId(userRole.getUserId());
        po.setRoleId(userRole.getRoleId());
        po.setCreateTime(userRole.getCreateTime());
        po.setUpdateTime(userRole.getUpdateTime());
        po.setDeleted(userRole.getDeleted());
        return po;
    }
}

