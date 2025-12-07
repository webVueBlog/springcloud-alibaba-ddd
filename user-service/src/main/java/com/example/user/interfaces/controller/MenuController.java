package com.example.user.interfaces.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.user.application.service.MenuService;
import com.example.user.domain.model.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 菜单控制器
 * <p>
 * 提供菜单管理相关的 REST API 接口
 * </p>
 * <p>
 * 请求路径：/api/menu
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Validated
public class MenuController {

    /** 菜单应用服务 */
    private final MenuService menuService;

    /**
     * 获取菜单列表
     * 
     * @param menuName 菜单名称（可选）
     * @param menuCode 菜单编码（可选）
     * @param status 状态（可选）
     * @return 菜单列表
     */
    @GetMapping("/list")
    public Result<List<Menu>> getMenuList(
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) String menuCode,
            @RequestParam(required = false) Integer status) {
        try {
            log.info("查询菜单列表: menuName={}, menuCode={}, status={}", menuName, menuCode, status);
            
            List<Menu> menus = menuService.getMenuList(menuName, menuCode, status);
            
            log.info("查询菜单列表成功: count={}", menus.size());
            return Result.success(menus);
        } catch (BusinessException e) {
            log.warn("查询菜单列表失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询菜单列表异常", e);
            return Result.error("查询菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单树
     * 
     * @return 菜单树
     */
    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        try {
            log.info("查询菜单树");
            
            List<Menu> menuTree = menuService.getMenuTree();
            
            log.info("查询菜单树成功: count={}", menuTree.size());
            return Result.success(menuTree);
        } catch (BusinessException e) {
            log.warn("查询菜单树失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询菜单树异常", e);
            return Result.error("查询菜单树失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户菜单树
     * <p>
     * 根据用户ID获取用户可访问的菜单树
     * admin用户返回所有菜单，其他用户根据权限过滤
     * </p>
     * 
     * @param userId 用户ID（可选，如果不提供则从请求头获取）
     * @param request HTTP请求（用于从请求头获取用户ID）
     * @return 用户菜单树
     */
    @GetMapping("/user-menu")
    public Result<List<Menu>> getUserMenuTree(
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        try {
            // 如果未提供userId，尝试从请求头获取
            if (userId == null) {
                // 尝试从请求头获取用户ID（由网关传递）
                String userIdHeader = request.getHeader("X-User-Id");
                if (userIdHeader != null && !userIdHeader.isEmpty()) {
                    try {
                        userId = Long.parseLong(userIdHeader);
                        log.debug("从请求头获取用户ID: userId={}", userId);
                    } catch (NumberFormatException e) {
                        log.warn("请求头中的用户ID格式错误: {}", userIdHeader);
                    }
                }
                
                // 如果还是没有，尝试从Token中解析
                if (userId == null) {
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        try {
                            // 简单解析JWT Token获取用户ID（不验证签名，仅用于获取用户ID）
                            String[] parts = token.split("\\.");
                            if (parts.length == 3) {
                                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                                com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSON.parseObject(payload);
                                Object userIdObj = jsonObject.get("userId");
                                if (userIdObj != null) {
                                    userId = Long.valueOf(userIdObj.toString());
                                    log.debug("从Token解析用户ID成功: userId={}", userId);
                                }
                            }
                        } catch (Exception e) {
                            log.debug("无法从Token解析用户ID: {}", e.getMessage());
                        }
                    }
                }
            }
            
            if (userId == null) {
                log.warn("无法获取用户ID，返回空菜单列表");
                return Result.success(new java.util.ArrayList<>());
            }
            
            log.info("查询用户菜单树: userId={}", userId);
            
            List<Menu> menuTree = menuService.getUserMenuTree(userId);
            
            log.info("查询用户菜单树成功: userId={}, count={}", userId, menuTree.size());
            return Result.success(menuTree);
        } catch (BusinessException e) {
            log.warn("查询用户菜单树失败: userId={}, message={}", userId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户菜单树异常: userId={}", userId, e);
            return Result.error("查询用户菜单树失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单详情
     * 
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{id}")
    public Result<Menu> getMenuById(
            @PathVariable @NotNull(message = "菜单ID不能为空") @Positive(message = "菜单ID必须大于0") Long id) {
        try {
            log.info("查询菜单详情: menuId={}", id);
            
            Menu menu = menuService.getMenuById(id);
            
            log.info("查询菜单详情成功: menuId={}", id);
            return Result.success(menu);
        } catch (BusinessException e) {
            log.warn("查询菜单详情失败: menuId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询菜单详情异常: menuId={}", id, e);
            return Result.error("查询菜单详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建菜单
     * 
     * @param menu 菜单信息
     * @return 创建的菜单
     */
    @PostMapping
    public Result<Menu> createMenu(@RequestBody Menu menu) {
        try {
            log.info("创建菜单: menuCode={}, menuName={}", menu.getMenuCode(), menu.getMenuName());
            
            Menu createdMenu = menuService.createMenu(menu);
            
            log.info("创建菜单成功: menuId={}", createdMenu.getId());
            return Result.success(createdMenu);
        } catch (BusinessException e) {
            log.warn("创建菜单失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建菜单异常", e);
            return Result.error("创建菜单失败: " + e.getMessage());
        }
    }

    /**
     * 更新菜单
     * 
     * @param id 菜单ID
     * @param menu 菜单信息
     * @return 更新后的菜单
     */
    @PutMapping("/{id}")
    public Result<Menu> updateMenu(
            @PathVariable @NotNull(message = "菜单ID不能为空") @Positive(message = "菜单ID必须大于0") Long id,
            @RequestBody Menu menu) {
        try {
            log.info("更新菜单: menuId={}", id);
            
            Menu updatedMenu = menuService.updateMenu(id, menu);
            
            log.info("更新菜单成功: menuId={}", id);
            return Result.success(updatedMenu);
        } catch (BusinessException e) {
            log.warn("更新菜单失败: menuId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新菜单异常: menuId={}", id, e);
            return Result.error("更新菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单
     * 
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(
            @PathVariable @NotNull(message = "菜单ID不能为空") @Positive(message = "菜单ID必须大于0") Long id) {
        try {
            log.info("删除菜单: menuId={}", id);
            
            menuService.deleteMenu(id);
            
            log.info("删除菜单成功: menuId={}", id);
            return Result.success(null);
        } catch (BusinessException e) {
            log.warn("删除菜单失败: menuId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除菜单异常: menuId={}", id, e);
            return Result.error("删除菜单失败: " + e.getMessage());
        }
    }
}

