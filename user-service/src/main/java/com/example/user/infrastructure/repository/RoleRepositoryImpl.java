package com.example.user.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.domain.model.Role;
import com.example.user.domain.repository.RoleRepository;
import com.example.user.infrastructure.mapper.RoleMapper;
import com.example.user.infrastructure.po.RolePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色仓储实现
 * <p>
 * 实现角色仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public Role findById(Long id) {
        Assert.notNull(id, "角色ID不能为空");
        
        try {
            RolePO po = roleMapper.selectById(id);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询角色失败: roleId={}", id, e);
            throw new RuntimeException("查询角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Role findByRoleCode(String roleCode) {
        Assert.hasText(roleCode, "角色编码不能为空");
        
        try {
            RolePO po = roleMapper.selectByRoleCode(roleCode);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询角色失败: roleCode={}", roleCode, e);
            throw new RuntimeException("查询角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Role> findAll() {
        try {
            // 显式添加逻辑删除过滤条件，确保只查询未删除的记录
            QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("deleted", 0);
            List<RolePO> pos = roleMapper.selectList(queryWrapper);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有角色失败", e);
            throw new RuntimeException("查询所有角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Role save(Role role) {
        Assert.notNull(role, "角色不能为空");
        
        try {
            RolePO po = convertToPO(role);
            if (role.getId() == null) {
                roleMapper.insert(po);
                role.setId(po.getId());
            } else {
                roleMapper.updateById(po);
            }
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("保存角色失败: roleCode={}", role.getRoleCode(), e);
            throw new RuntimeException("保存角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "角色ID不能为空");
        
        try {
            // 使用自定义SQL直接更新deleted字段，绕过逻辑删除拦截器
            int updateCount = roleMapper.logicalDeleteById(id);
            if (updateCount > 0) {
                log.info("删除角色成功: roleId={}", id);
            } else {
                log.warn("删除角色失败，角色不存在或已被删除: roleId={}", id);
            }
        } catch (Exception e) {
            log.error("删除角色失败: roleId={}", id, e);
            throw new RuntimeException("删除角色失败: " + e.getMessage(), e);
        }
    }

    private Role convertToDomain(RolePO po) {
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
    
    private RolePO convertToPO(Role role) {
        if (role == null) {
            return null;
        }
        
        RolePO po = new RolePO();
        po.setId(role.getId());
        po.setRoleCode(role.getRoleCode());
        po.setRoleName(role.getRoleName());
        po.setDescription(role.getDescription());
        po.setStatus(role.getStatus());
        po.setCreateTime(role.getCreateTime());
        po.setUpdateTime(role.getUpdateTime());
        po.setDeleted(role.getDeleted());
        return po;
    }
}

