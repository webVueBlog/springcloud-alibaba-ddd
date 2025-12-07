package com.example.user.infrastructure.repository;

import com.example.user.domain.model.Permission;
import com.example.user.domain.model.RolePermission;
import com.example.user.domain.repository.RolePermissionRepository;
import com.example.user.infrastructure.mapper.PermissionMapper;
import com.example.user.infrastructure.mapper.RolePermissionMapper;
import com.example.user.infrastructure.po.PermissionPO;
import com.example.user.infrastructure.po.RolePermissionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限关联仓储实现
 * <p>
 * 实现角色权限关联仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        Assert.notNull(roleId, "角色ID不能为空");
        
        try {
            List<RolePermissionPO> pos = rolePermissionMapper.selectByRoleId(roleId);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询角色权限关联失败: roleId={}", roleId, e);
            throw new RuntimeException("查询角色权限关联失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        Assert.notNull(roleId, "角色ID不能为空");
        
        try {
            List<PermissionPO> pos = rolePermissionMapper.selectPermissionsByRoleId(roleId);
            return pos.stream()
                    .map(this::convertPermissionToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询角色权限失败: roleId={}", roleId, e);
            throw new RuntimeException("查询角色权限失败: " + e.getMessage(), e);
        }
    }

    @Override
    public RolePermission save(RolePermission rolePermission) {
        Assert.notNull(rolePermission, "角色权限关联不能为空");
        
        try {
            RolePermissionPO po = convertToPO(rolePermission);
            if (rolePermission.getId() == null) {
                rolePermissionMapper.insert(po);
                rolePermission.setId(po.getId());
            } else {
                rolePermissionMapper.updateById(po);
            }
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("保存角色权限关联失败: roleId={}, permissionId={}", 
                    rolePermission.getRoleId(), rolePermission.getPermissionId(), e);
            throw new RuntimeException("保存角色权限关联失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        Assert.notNull(roleId, "角色ID不能为空");
        Assert.notNull(permissionId, "权限ID不能为空");
        
        try {
            // 逻辑删除：更新 deleted 字段
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RolePermissionPO> wrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(RolePermissionPO::getRoleId, roleId)
                   .eq(RolePermissionPO::getPermissionId, permissionId)
                   .eq(RolePermissionPO::getDeleted, 0);
            
            RolePermissionPO po = rolePermissionMapper.selectList(wrapper).stream().findFirst().orElse(null);
            
            if (po != null) {
                po.setDeleted(1);
                rolePermissionMapper.updateById(po);
                log.debug("删除角色权限关联成功: roleId={}, permissionId={}", roleId, permissionId);
            }
        } catch (Exception e) {
            log.error("删除角色权限关联失败: roleId={}, permissionId={}", roleId, permissionId, e);
            throw new RuntimeException("删除角色权限关联失败: " + e.getMessage(), e);
        }
    }

    private RolePermission convertToDomain(RolePermissionPO po) {
        if (po == null) {
            return null;
        }
        
        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(po.getId());
        rolePermission.setRoleId(po.getRoleId());
        rolePermission.setPermissionId(po.getPermissionId());
        rolePermission.setCreateTime(po.getCreateTime());
        rolePermission.setUpdateTime(po.getUpdateTime());
        rolePermission.setDeleted(po.getDeleted());
        return rolePermission;
    }

    private Permission convertPermissionToDomain(PermissionPO po) {
        if (po == null) {
            return null;
        }
        
        Permission permission = new Permission();
        permission.setId(po.getId());
        permission.setPermissionCode(po.getPermissionCode());
        permission.setPermissionName(po.getPermissionName());
        permission.setPermissionType(po.getPermissionType());
        permission.setResource(po.getResource());
        permission.setMethod(po.getMethod());
        permission.setDescription(po.getDescription());
        permission.setStatus(po.getStatus());
        permission.setCreateTime(po.getCreateTime());
        permission.setUpdateTime(po.getUpdateTime());
        permission.setDeleted(po.getDeleted());
        return permission;
    }

    private RolePermissionPO convertToPO(RolePermission rolePermission) {
        if (rolePermission == null) {
            return null;
        }
        
        RolePermissionPO po = new RolePermissionPO();
        po.setId(rolePermission.getId());
        po.setRoleId(rolePermission.getRoleId());
        po.setPermissionId(rolePermission.getPermissionId());
        po.setCreateTime(rolePermission.getCreateTime());
        po.setUpdateTime(rolePermission.getUpdateTime());
        po.setDeleted(rolePermission.getDeleted());
        return po;
    }
}

