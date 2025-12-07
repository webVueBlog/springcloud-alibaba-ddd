package com.example.user.application.service;

import com.example.common.exception.BusinessException;
import com.example.user.domain.model.Permission;
import com.example.user.domain.model.Role;
import com.example.user.domain.repository.PermissionRepository;
import com.example.user.domain.repository.RolePermissionRepository;
import com.example.user.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色应用服务
 * <p>
 * 负责协调领域对象完成角色管理的业务用例
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    /** 角色仓储 */
    private final RoleRepository roleRepository;
    
    /** 权限仓储 */
    private final PermissionRepository permissionRepository;
    
    /** 角色权限关联仓储 */
    private final RolePermissionRepository rolePermissionRepository;

    /**
     * 获取角色列表
     * 
     * @param roleCode 角色编码（可选）
     * @param roleName 角色名称（可选）
     * @param status 状态（可选）
     * @return 角色列表
     */
    public List<Role> getRoleList(String roleCode, String roleName, Integer status) {
        try {
            List<Role> allRoles = roleRepository.findAll();
            
            // 应用查询条件过滤
            return allRoles.stream()
                    .filter(role -> {
                        if (StringUtils.hasText(roleCode)) {
                            return role.getRoleCode() != null && 
                                   role.getRoleCode().toLowerCase().contains(roleCode.toLowerCase());
                        }
                        return true;
                    })
                    .filter(role -> {
                        if (StringUtils.hasText(roleName)) {
                            return role.getRoleName() != null && 
                                   role.getRoleName().contains(roleName);
                        }
                        return true;
                    })
                    .filter(role -> {
                        if (status != null) {
                            return role.getStatus() != null && role.getStatus().equals(status);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取角色列表失败", e);
            throw new BusinessException("获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取角色
     * 
     * @param id 角色ID
     * @return 角色
     */
    public Role getRoleById(Long id) {
        Assert.notNull(id, "角色ID不能为空");
        
        try {
            Role role = roleRepository.findById(id);
            if (role == null) {
                throw new BusinessException("角色不存在");
            }
            return role;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取角色失败: roleId={}", id, e);
            throw new BusinessException("获取角色失败: " + e.getMessage());
        }
    }

    /**
     * 创建角色
     * 
     * @param role 角色信息
     * @return 创建的角色
     */
    public Role createRole(Role role) {
        Assert.notNull(role, "角色不能为空");
        Assert.hasText(role.getRoleCode(), "角色编码不能为空");
        Assert.hasText(role.getRoleName(), "角色名称不能为空");
        
        try {
            // 检查角色编码是否已存在
            Role existingRole = roleRepository.findByRoleCode(role.getRoleCode());
            if (existingRole != null) {
                throw new BusinessException("角色编码已存在: " + role.getRoleCode());
            }
            
            // 设置默认值
            if (role.getStatus() == null) {
                role.setStatus(1);
            }
            
            return roleRepository.save(role);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建角色失败: roleCode={}", role.getRoleCode(), e);
            throw new BusinessException("创建角色失败: " + e.getMessage());
        }
    }

    /**
     * 更新角色
     * 
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新后的角色
     */
    public Role updateRole(Long id, Role role) {
        Assert.notNull(id, "角色ID不能为空");
        Assert.notNull(role, "角色不能为空");
        
        try {
            Role existingRole = roleRepository.findById(id);
            if (existingRole == null) {
                throw new BusinessException("角色不存在");
            }
            
            // 如果修改了角色编码，检查是否重复
            if (StringUtils.hasText(role.getRoleCode()) && 
                !role.getRoleCode().equals(existingRole.getRoleCode())) {
                Role roleByCode = roleRepository.findByRoleCode(role.getRoleCode());
                if (roleByCode != null && !roleByCode.getId().equals(id)) {
                    throw new BusinessException("角色编码已存在: " + role.getRoleCode());
                }
            }
            
            // 更新字段
            if (StringUtils.hasText(role.getRoleCode())) {
                existingRole.setRoleCode(role.getRoleCode());
            }
            if (StringUtils.hasText(role.getRoleName())) {
                existingRole.setRoleName(role.getRoleName());
            }
            if (role.getDescription() != null) {
                existingRole.setDescription(role.getDescription());
            }
            if (role.getStatus() != null) {
                existingRole.setStatus(role.getStatus());
            }
            
            return roleRepository.save(existingRole);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新角色失败: roleId={}", id, e);
            throw new BusinessException("更新角色失败: " + e.getMessage());
        }
    }

    /**
     * 删除角色
     * 
     * @param id 角色ID
     */
    public void deleteRole(Long id) {
        Assert.notNull(id, "角色ID不能为空");
        
        try {
            // 直接执行删除操作，不依赖findById（避免逻辑删除拦截器影响）
            roleRepository.delete(id);
            log.info("删除角色操作完成: roleId={}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除角色失败: roleId={}", id, e);
            throw new BusinessException("删除角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    public List<Permission> getRolePermissions(Long roleId) {
        Assert.notNull(roleId, "角色ID不能为空");
        
        try {
            return rolePermissionRepository.findPermissionsByRoleId(roleId);
        } catch (Exception e) {
            log.error("获取角色权限失败: roleId={}", roleId, e);
            throw new BusinessException("获取角色权限失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有权限（按类型分类的树形结构）
     * 按钮权限按功能模块组织，数据权限平铺展示
     * 
     * @return 权限树形结构，按类型分类
     */
    public List<PermissionTreeNode> getPermissionTree() {
        try {
            List<Permission> allPermissions = permissionRepository.findAll();
            
            // 按类型分组
            Map<String, List<Permission>> permissionsByType = allPermissions.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == 1) // 只返回正常状态的权限
                    .collect(Collectors.groupingBy(
                            p -> p.getPermissionType() != null ? p.getPermissionType() : "BUTTON",
                            Collectors.toList()
                    ));
            
            List<PermissionTreeNode> tree = new ArrayList<>();
            
            // 按钮权限节点（按功能模块组织）
            if (permissionsByType.containsKey("BUTTON")) {
                PermissionTreeNode buttonNode = new PermissionTreeNode();
                buttonNode.setId(-1L); // 使用负数作为类型节点的ID
                buttonNode.setPermissionCode("BUTTON_TYPE");
                buttonNode.setPermissionName("按钮权限");
                buttonNode.setPermissionType("BUTTON");
                
                // 按功能模块组织按钮权限
                List<Permission> buttonPermissions = permissionsByType.get("BUTTON");
                List<PermissionTreeNode> moduleNodes = buildButtonPermissionTree(buttonPermissions);
                buttonNode.setChildren(moduleNodes);
                
                tree.add(buttonNode);
            }
            
            // 数据权限节点（平铺展示）
            if (permissionsByType.containsKey("DATA")) {
                PermissionTreeNode dataNode = new PermissionTreeNode();
                dataNode.setId(-2L);
                dataNode.setPermissionCode("DATA_TYPE");
                dataNode.setPermissionName("数据权限");
                dataNode.setPermissionType("DATA");
                dataNode.setChildren(permissionsByType.get("DATA").stream()
                        .map(this::convertToTreeNode)
                        .collect(Collectors.toList()));
                tree.add(dataNode);
            }
            
            return tree;
        } catch (Exception e) {
            log.error("获取权限树失败", e);
            throw new BusinessException("获取权限树失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建按钮权限树（按功能模块组织）
     * 
     * @param buttonPermissions 按钮权限列表
     * @return 模块节点列表
     */
    private List<PermissionTreeNode> buildButtonPermissionTree(List<Permission> buttonPermissions) {
        // 定义功能模块映射（Java 8 兼容写法）
        Map<String, String> moduleMap = new java.util.HashMap<>();
        moduleMap.put("USER_", "用户管理");
        moduleMap.put("ROLE_", "角色管理");
        moduleMap.put("ORDER_", "订单管理");
        moduleMap.put("SECKILL_", "秒杀管理");
        
        // 按模块分组
        Map<String, List<Permission>> permissionsByModule = new java.util.HashMap<>();
        
        for (Permission permission : buttonPermissions) {
            String permissionCode = permission.getPermissionCode();
            String moduleName = null;
            
            // 查找所属模块
            for (Map.Entry<String, String> entry : moduleMap.entrySet()) {
                if (permissionCode != null && permissionCode.startsWith(entry.getKey())) {
                    moduleName = entry.getValue();
                    break;
                }
            }
            
            // 如果找不到模块，使用"其他"
            if (moduleName == null) {
                moduleName = "其他";
            }
            
            permissionsByModule.computeIfAbsent(moduleName, k -> new ArrayList<>()).add(permission);
        }
        
        // 构建模块节点
        List<PermissionTreeNode> moduleNodes = new ArrayList<>();
        long moduleId = -100L; // 模块节点使用负数ID
        
        for (Map.Entry<String, List<Permission>> entry : permissionsByModule.entrySet()) {
            PermissionTreeNode moduleNode = new PermissionTreeNode();
            moduleNode.setId(moduleId--);
            moduleNode.setPermissionCode("MODULE_" + entry.getKey());
            moduleNode.setPermissionName(entry.getKey());
            moduleNode.setPermissionType("BUTTON");
            
            // 将权限转换为树节点
            List<PermissionTreeNode> permissionNodes = entry.getValue().stream()
                    .map(this::convertToTreeNode)
                    .collect(Collectors.toList());
            moduleNode.setChildren(permissionNodes);
            
            moduleNodes.add(moduleNode);
        }
        
        // 按模块名称排序
        moduleNodes.sort((a, b) -> a.getPermissionName().compareTo(b.getPermissionName()));
        
        return moduleNodes;
    }
    
    /**
     * 获取角色权限（返回树形结构和已选权限）
     * 
     * @param roleId 角色ID
     * @return 包含权限树和已选权限ID的对象
     */
    public RolePermissionTreeDTO getRolePermissionTree(Long roleId) {
        Assert.notNull(roleId, "角色ID不能为空");
        
        try {
            // 获取所有权限树
            List<PermissionTreeNode> tree = getPermissionTree();
            
            // 获取角色已选权限
            List<Permission> rolePermissions = getRolePermissions(roleId);
            List<Long> checkedIds = rolePermissions.stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());
            
            RolePermissionTreeDTO dto = new RolePermissionTreeDTO();
            dto.setTree(tree);
            dto.setChecked(checkedIds);
            return dto;
        } catch (Exception e) {
            log.error("获取角色权限树失败: roleId={}", roleId, e);
            throw new BusinessException("获取角色权限树失败: " + e.getMessage());
        }
    }
    
    /**
     * 将权限转换为树节点
     */
    private PermissionTreeNode convertToTreeNode(Permission permission) {
        PermissionTreeNode node = new PermissionTreeNode();
        node.setId(permission.getId());
        node.setPermissionCode(permission.getPermissionCode());
        node.setPermissionName(permission.getPermissionName());
        node.setPermissionType(permission.getPermissionType());
        node.setResource(permission.getResource());
        node.setMethod(permission.getMethod());
        node.setDescription(permission.getDescription());
        node.setStatus(permission.getStatus());
        return node;
    }
    
    /**
     * 权限树节点DTO
     */
    public static class PermissionTreeNode {
        private Long id;
        private String permissionCode;
        private String permissionName;
        private String permissionType;
        private String resource;
        private String method;
        private String description;
        private Integer status;
        private List<PermissionTreeNode> children;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getPermissionCode() { return permissionCode; }
        public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
        public String getPermissionName() { return permissionName; }
        public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
        public String getPermissionType() { return permissionType; }
        public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public List<PermissionTreeNode> getChildren() { return children; }
        public void setChildren(List<PermissionTreeNode> children) { this.children = children; }
    }
    
    /**
     * 角色权限树DTO
     */
    public static class RolePermissionTreeDTO {
        private List<PermissionTreeNode> tree;
        private List<Long> checked;
        
        public List<PermissionTreeNode> getTree() { return tree; }
        public void setTree(List<PermissionTreeNode> tree) { this.tree = tree; }
        public List<Long> getChecked() { return checked; }
        public void setChecked(List<Long> checked) { this.checked = checked; }
    }

    /**
     * 分配角色权限
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    public void assignRolePermissions(Long roleId, List<Long> permissionIds) {
        Assert.notNull(roleId, "角色ID不能为空");
        Assert.notNull(permissionIds, "权限ID列表不能为空");
        
        try {
            // 删除现有权限关联
            List<com.example.user.domain.model.RolePermission> existingPermissions = 
                rolePermissionRepository.findByRoleId(roleId);
            for (com.example.user.domain.model.RolePermission rp : existingPermissions) {
                rolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, rp.getPermissionId());
            }
            
            // 添加新权限关联
            for (Long permissionId : permissionIds) {
                com.example.user.domain.model.RolePermission rp = 
                    new com.example.user.domain.model.RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rolePermissionRepository.save(rp);
            }
        } catch (Exception e) {
            log.error("分配角色权限失败: roleId={}", roleId, e);
            throw new BusinessException("分配角色权限失败: " + e.getMessage());
        }
    }
}

