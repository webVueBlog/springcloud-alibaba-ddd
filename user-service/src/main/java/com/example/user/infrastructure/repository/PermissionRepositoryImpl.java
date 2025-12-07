package com.example.user.infrastructure.repository;

import com.example.user.domain.model.Permission;
import com.example.user.domain.repository.PermissionRepository;
import com.example.user.infrastructure.mapper.PermissionMapper;
import com.example.user.infrastructure.po.PermissionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限仓储实现
 * <p>
 * 实现权限仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public Permission findById(Long id) {
        Assert.notNull(id, "权限ID不能为空");
        
        try {
            PermissionPO po = permissionMapper.selectById(id);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询权限失败: permissionId={}", id, e);
            throw new RuntimeException("查询权限失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Permission findByPermissionCode(String permissionCode) {
        Assert.hasText(permissionCode, "权限编码不能为空");
        
        try {
            PermissionPO po = permissionMapper.selectByPermissionCode(permissionCode);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询权限失败: permissionCode={}", permissionCode, e);
            throw new RuntimeException("查询权限失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Permission> findAll() {
        try {
            List<PermissionPO> pos = permissionMapper.selectList(null);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有权限失败", e);
            throw new RuntimeException("查询所有权限失败: " + e.getMessage(), e);
        }
    }

    private Permission convertToDomain(PermissionPO po) {
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
}

