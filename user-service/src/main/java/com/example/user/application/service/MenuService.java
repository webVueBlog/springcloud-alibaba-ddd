package com.example.user.application.service;

import com.example.common.exception.BusinessException;
import com.example.user.domain.model.Menu;
import com.example.user.domain.model.Permission;
import com.example.user.domain.model.Role;
import com.example.user.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单应用服务
 * <p>
 * 负责协调领域对象完成菜单管理的业务用例
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    /** 菜单仓储 */
    private final MenuRepository menuRepository;
    
    /** 用户服务（用于获取用户角色和权限） */
    private final UserService userService;

    /**
     * 获取菜单列表
     * 
     * @param menuName 菜单名称（可选）
     * @param menuCode 菜单编码（可选）
     * @param status 状态（可选）
     * @return 菜单列表
     */
    public List<Menu> getMenuList(String menuName, String menuCode, Integer status) {
        try {
            return menuRepository.findByCondition(menuName, menuCode, status);
        } catch (Exception e) {
            log.error("获取菜单列表失败", e);
            throw new BusinessException("获取菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单树
     * 
     * @return 菜单树
     */
    public List<Menu> getMenuTree() {
        try {
            List<Menu> allMenus = menuRepository.findAll();
            return buildMenuTree(allMenus, null);
        } catch (Exception e) {
            log.error("获取菜单树失败", e);
            throw new BusinessException("获取菜单树失败: " + e.getMessage());
        }
    }

    /**
     * 构建菜单树
     * 
     * @param allMenus 所有菜单
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<Menu> buildMenuTree(List<Menu> allMenus, Long parentId) {
        List<Menu> tree = new ArrayList<>();
        for (Menu menu : allMenus) {
            Long menuParentId = menu.getParentId();
            if ((parentId == null && (menuParentId == null || menuParentId == 0)) ||
                (parentId != null && parentId.equals(menuParentId))) {
                List<Menu> children = buildMenuTree(allMenus, menu.getId());
                menu.setChildren(children);
                tree.add(menu);
            }
        }
        // 按排序号排序
        tree.sort((a, b) -> {
            Integer sortA = a.getSort() != null ? a.getSort() : 0;
            Integer sortB = b.getSort() != null ? b.getSort() : 0;
            return sortA.compareTo(sortB);
        });
        return tree;
    }

    /**
     * 根据ID获取菜单
     * 
     * @param id 菜单ID
     * @return 菜单
     */
    public Menu getMenuById(Long id) {
        Assert.notNull(id, "菜单ID不能为空");
        
        try {
            Menu menu = menuRepository.findById(id);
            if (menu == null) {
                throw new BusinessException("菜单不存在");
            }
            return menu;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取菜单失败: menuId={}", id, e);
            throw new BusinessException("获取菜单失败: " + e.getMessage());
        }
    }

    /**
     * 创建菜单
     * 
     * @param menu 菜单信息
     * @return 创建的菜单
     */
    public Menu createMenu(Menu menu) {
        Assert.notNull(menu, "菜单不能为空");
        Assert.hasText(menu.getMenuCode(), "菜单编码不能为空");
        Assert.hasText(menu.getMenuName(), "菜单名称不能为空");
        
        try {
            // 检查菜单编码是否已存在
            Menu existingMenu = menuRepository.findByMenuCode(menu.getMenuCode());
            if (existingMenu != null) {
                throw new BusinessException("菜单编码已存在: " + menu.getMenuCode());
            }
            
            // 设置默认值
            if (menu.getStatus() == null) {
                menu.setStatus(1);
            }
            if (menu.getSort() == null) {
                menu.setSort(0);
            }
            if (menu.getParentId() != null && menu.getParentId() == 0) {
                menu.setParentId(null);
            }
            
            return menuRepository.save(menu);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建菜单失败: menuCode={}", menu.getMenuCode(), e);
            throw new BusinessException("创建菜单失败: " + e.getMessage());
        }
    }

    /**
     * 更新菜单
     * 
     * @param id 菜单ID
     * @param menu 菜单信息
     * @return 更新后的菜单
     */
    public Menu updateMenu(Long id, Menu menu) {
        Assert.notNull(id, "菜单ID不能为空");
        Assert.notNull(menu, "菜单不能为空");
        
        try {
            Menu existingMenu = menuRepository.findById(id);
            if (existingMenu == null) {
                throw new BusinessException("菜单不存在");
            }
            
            // 如果修改了菜单编码，检查是否重复
            if (StringUtils.hasText(menu.getMenuCode()) && 
                !menu.getMenuCode().equals(existingMenu.getMenuCode())) {
                Menu menuByCode = menuRepository.findByMenuCode(menu.getMenuCode());
                if (menuByCode != null && !menuByCode.getId().equals(id)) {
                    throw new BusinessException("菜单编码已存在: " + menu.getMenuCode());
                }
            }
            
            // 更新字段
            if (StringUtils.hasText(menu.getMenuCode())) {
                existingMenu.setMenuCode(menu.getMenuCode());
            }
            if (StringUtils.hasText(menu.getMenuName())) {
                existingMenu.setMenuName(menu.getMenuName());
            }
            if (menu.getParentId() != null) {
                if (menu.getParentId() == 0) {
                    existingMenu.setParentId(null);
                } else {
                    existingMenu.setParentId(menu.getParentId());
                }
            }
            if (StringUtils.hasText(menu.getPath())) {
                existingMenu.setPath(menu.getPath());
            }
            if (menu.getIcon() != null) {
                existingMenu.setIcon(menu.getIcon());
            }
            if (menu.getSort() != null) {
                existingMenu.setSort(menu.getSort());
            }
            if (menu.getDescription() != null) {
                existingMenu.setDescription(menu.getDescription());
            }
            if (menu.getStatus() != null) {
                existingMenu.setStatus(menu.getStatus());
            }
            
            return menuRepository.save(existingMenu);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新菜单失败: menuId={}", id, e);
            throw new BusinessException("更新菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单
     * 
     * @param id 菜单ID
     */
    public void deleteMenu(Long id) {
        Assert.notNull(id, "菜单ID不能为空");
        
        try {
            // 检查是否有子菜单（使用findByParentId，该方法已包含deleted=0过滤）
            List<Menu> children = menuRepository.findByParentId(id);
            if (children != null && !children.isEmpty()) {
                throw new BusinessException("该菜单下存在子菜单，无法删除");
            }
            
            // 直接执行删除操作，不依赖findById（避免逻辑删除拦截器影响）
            menuRepository.delete(id);
            log.info("删除菜单操作完成: menuId={}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除菜单失败: menuId={}", id, e);
            throw new BusinessException("删除菜单失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取用户菜单树
     * <p>
     * 如果用户是admin超级管理员，返回所有菜单
     * 否则根据用户的权限过滤菜单
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return 用户菜单树
     */
    public List<Menu> getUserMenuTree(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            // 检查用户是否为admin超级管理员
            boolean isAdmin = isAdminUser(userId);
            
            // 获取所有菜单
            List<Menu> allMenus = menuRepository.findAll();
            
            // 如果是admin，返回所有菜单
            if (isAdmin) {
                log.info("用户是admin超级管理员，返回所有菜单: userId={}", userId);
                return buildMenuTree(allMenus, null);
            }
            
            // 否则根据用户权限过滤菜单
            List<Permission> userPermissions = userService.getUserPermissions(userId);
            List<String> permissionCodes = userPermissions.stream()
                    .map(Permission::getPermissionCode)
                    .collect(Collectors.toList());
            
            // 根据权限过滤菜单（这里简化处理，实际可以根据菜单编码和权限编码的映射关系）
            // 目前菜单和权限没有直接关联，所以返回所有菜单
            // 后续可以根据业务需求添加菜单权限关联表
            log.info("根据用户权限过滤菜单: userId={}, permissionCount={}", userId, permissionCodes.size());
            
            // 过滤掉禁用的菜单
            List<Menu> enabledMenus = allMenus.stream()
                    .filter(menu -> menu.getStatus() != null && menu.getStatus() == 1)
                    .collect(Collectors.toList());
            
            return buildMenuTree(enabledMenus, null);
        } catch (Exception e) {
            log.error("获取用户菜单失败: userId={}", userId, e);
            throw new BusinessException("获取用户菜单失败: " + e.getMessage());
        }
    }

    /**
     * 判断用户是否为admin超级管理员
     * <p>
     * 通过检查用户是否拥有ADMIN角色来判断
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return true 表示是admin超级管理员，false 表示不是
     */
    private boolean isAdminUser(Long userId) {
        try {
            List<Role> roles = userService.getUserRoles(userId);
            return roles.stream()
                    .anyMatch(role -> "ADMIN".equals(role.getRoleCode()));
        } catch (Exception e) {
            log.warn("判断用户是否为admin失败: userId={}", userId, e);
            return false;
        }
    }
}

