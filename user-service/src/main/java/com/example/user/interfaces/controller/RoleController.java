package com.example.user.interfaces.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.user.application.service.RoleService;
import com.example.user.domain.model.Permission;
import com.example.user.domain.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 角色控制器
 * <p>
 * 提供角色管理相关的 REST API 接口
 * </p>
 * <p>
 * 请求路径：/api/role
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Validated
public class RoleController {

    /** 角色应用服务 */
    private final RoleService roleService;

    /**
     * 获取角色列表
     * 
     * @param roleCode 角色编码（可选）
     * @param roleName 角色名称（可选）
     * @param status 状态（可选）
     * @param page 页码（可选，暂未实现分页）
     * @param size 每页大小（可选，暂未实现分页）
     * @return 角色列表
     */
    @GetMapping("/list")
    public Result<List<Role>> getRoleList(
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            log.info("查询角色列表: roleCode={}, roleName={}, status={}, page={}, size={}", 
                    roleCode, roleName, status, page, size);
            
            List<Role> roles = roleService.getRoleList(roleCode, roleName, status);
            
            log.info("查询角色列表成功: count={}", roles.size());
            return Result.success(roles);
        } catch (BusinessException e) {
            log.warn("查询角色列表失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询角色列表异常", e);
            return Result.error("查询角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色详情
     * 
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    public Result<Role> getRoleById(
            @PathVariable @NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须大于0") Long id) {
        try {
            log.info("查询角色详情: roleId={}", id);
            
            Role role = roleService.getRoleById(id);
            
            log.info("查询角色详情成功: roleId={}", id);
            return Result.success(role);
        } catch (BusinessException e) {
            log.warn("查询角色详情失败: roleId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询角色详情异常: roleId={}", id, e);
            return Result.error("查询角色详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建角色
     * 
     * @param role 角色信息
     * @return 创建的角色
     */
    @PostMapping
    public Result<Role> createRole(@RequestBody Role role) {
        try {
            log.info("创建角色: roleCode={}, roleName={}", role.getRoleCode(), role.getRoleName());
            
            Role createdRole = roleService.createRole(role);
            
            log.info("创建角色成功: roleId={}", createdRole.getId());
            return Result.success(createdRole);
        } catch (BusinessException e) {
            log.warn("创建角色失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建角色异常", e);
            return Result.error("创建角色失败: " + e.getMessage());
        }
    }

    /**
     * 更新角色
     * 
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新后的角色
     */
    @PutMapping("/{id}")
    public Result<Role> updateRole(
            @PathVariable @NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须大于0") Long id,
            @RequestBody Role role) {
        try {
            log.info("更新角色: roleId={}", id);
            
            Role updatedRole = roleService.updateRole(id, role);
            
            log.info("更新角色成功: roleId={}", id);
            return Result.success(updatedRole);
        } catch (BusinessException e) {
            log.warn("更新角色失败: roleId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新角色异常: roleId={}", id, e);
            return Result.error("更新角色失败: " + e.getMessage());
        }
    }

    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(
            @PathVariable @NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须大于0") Long id) {
        try {
            log.info("删除角色: roleId={}", id);
            
            roleService.deleteRole(id);
            
            log.info("删除角色成功: roleId={}", id);
            return Result.success(null);
        } catch (BusinessException e) {
            log.warn("删除角色失败: roleId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除角色异常: roleId={}", id, e);
            return Result.error("删除角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @GetMapping("/{roleId}/permissions")
    public Result<List<Permission>> getRolePermissions(
            @PathVariable @NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须大于0") Long roleId) {
        try {
            log.info("查询角色权限: roleId={}", roleId);
            
            List<Permission> permissions = roleService.getRolePermissions(roleId);
            
            log.info("查询角色权限成功: roleId={}, count={}", roleId, permissions.size());
            return Result.success(permissions);
        } catch (BusinessException e) {
            log.warn("查询角色权限失败: roleId={}, message={}", roleId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询角色权限异常: roleId={}", roleId, e);
            return Result.error("查询角色权限失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取角色权限树（按类型分类）
     * 
     * @param roleId 角色ID
     * @return 权限树形结构和已选权限
     */
    @GetMapping("/{roleId}/permissions/tree")
    public Result<RoleService.RolePermissionTreeDTO> getRolePermissionTree(
            @PathVariable @NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须大于0") Long roleId) {
        try {
            log.info("查询角色权限树: roleId={}", roleId);
            
            RoleService.RolePermissionTreeDTO tree = roleService.getRolePermissionTree(roleId);
            
            log.info("查询角色权限树成功: roleId={}", roleId);
            return Result.success(tree);
        } catch (BusinessException e) {
            log.warn("查询角色权限树失败: roleId={}, message={}", roleId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询角色权限树异常: roleId={}", roleId, e);
            return Result.error("查询角色权限树失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有权限树（按类型分类）
     * 
     * @return 权限树形结构
     */
    @GetMapping("/permissions/tree")
    public Result<List<RoleService.PermissionTreeNode>> getPermissionTree() {
        try {
            log.info("查询权限树");
            
            List<RoleService.PermissionTreeNode> tree = roleService.getPermissionTree();
            
            log.info("查询权限树成功: count={}", tree.size());
            return Result.success(tree);
        } catch (BusinessException e) {
            log.warn("查询权限树失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询权限树异常", e);
            return Result.error("查询权限树失败: " + e.getMessage());
        }
    }

    /**
     * 分配角色权限
     * 
     * @param roleId 角色ID
     * @param request 权限ID列表
     * @return 操作结果
     */
    @PostMapping("/{roleId}/permissions")
    public Result<Void> assignRolePermissions(
            @PathVariable @NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须大于0") Long roleId,
            @RequestBody AssignPermissionRequest request) {
        try {
            log.info("分配角色权限: roleId={}, permissionIds={}", roleId, request.getPermissionIds());
            
            roleService.assignRolePermissions(roleId, request.getPermissionIds());
            
            log.info("分配角色权限成功: roleId={}", roleId);
            return Result.success(null);
        } catch (BusinessException e) {
            log.warn("分配角色权限失败: roleId={}, message={}", roleId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("分配角色权限异常: roleId={}", roleId, e);
            return Result.error("分配角色权限失败: " + e.getMessage());
        }
    }

    /**
     * 分配权限请求对象
     */
    public static class AssignPermissionRequest {
        private List<Long> permissionIds;

        public List<Long> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(List<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }
}

