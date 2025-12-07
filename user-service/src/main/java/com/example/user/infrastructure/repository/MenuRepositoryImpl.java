package com.example.user.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.domain.model.Menu;
import com.example.user.domain.repository.MenuRepository;
import com.example.user.infrastructure.mapper.MenuMapper;
import com.example.user.infrastructure.po.MenuPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单仓储实现
 * <p>
 * 实现菜单仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuMapper menuMapper;

    @Override
    public Menu findById(Long id) {
        Assert.notNull(id, "菜单ID不能为空");
        
        try {
            MenuPO po = menuMapper.selectById(id);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询菜单失败: menuId={}", id, e);
            throw new RuntimeException("查询菜单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Menu findByMenuCode(String menuCode) {
        Assert.hasText(menuCode, "菜单编码不能为空");
        
        try {
            MenuPO po = menuMapper.selectByMenuCode(menuCode);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询菜单失败: menuCode={}", menuCode, e);
            throw new RuntimeException("查询菜单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Menu> findAll() {
        try {
            // 显式添加逻辑删除过滤条件，确保只查询未删除的记录
            QueryWrapper<MenuPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("deleted", 0);
            List<MenuPO> pos = menuMapper.selectList(queryWrapper);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有菜单失败", e);
            throw new RuntimeException("查询所有菜单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Menu> findByParentId(Long parentId) {
        try {
            List<MenuPO> pos = menuMapper.selectByParentId(parentId);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询子菜单失败: parentId={}", parentId, e);
            throw new RuntimeException("查询子菜单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Menu> findByCondition(String menuName, String menuCode, Integer status) {
        try {
            List<MenuPO> pos = menuMapper.selectList(null);
            return pos.stream()
                    .filter(po -> {
                        if (StringUtils.hasText(menuName)) {
                            return po.getMenuName() != null && 
                                   po.getMenuName().contains(menuName);
                        }
                        return true;
                    })
                    .filter(po -> {
                        if (StringUtils.hasText(menuCode)) {
                            return po.getMenuCode() != null && 
                                   po.getMenuCode().toLowerCase().contains(menuCode.toLowerCase());
                        }
                        return true;
                    })
                    .filter(po -> {
                        if (status != null) {
                            return po.getStatus() != null && po.getStatus().equals(status);
                        }
                        return true;
                    })
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("条件查询菜单失败", e);
            throw new RuntimeException("条件查询菜单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Menu save(Menu menu) {
        Assert.notNull(menu, "菜单不能为空");
        
        try {
            MenuPO po = convertToPO(menu);
            if (menu.getId() == null) {
                menuMapper.insert(po);
                menu.setId(po.getId());
            } else {
                menuMapper.updateById(po);
            }
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("保存菜单失败: menuCode={}", menu.getMenuCode(), e);
            throw new RuntimeException("保存菜单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "菜单ID不能为空");
        
        try {
            // 使用自定义SQL直接更新deleted字段，绕过逻辑删除拦截器
            int updateCount = menuMapper.logicalDeleteById(id);
            if (updateCount > 0) {
                log.info("删除菜单成功: menuId={}", id);
            } else {
                log.warn("删除菜单失败，菜单不存在或已被删除: menuId={}", id);
            }
        } catch (Exception e) {
            log.error("删除菜单失败: menuId={}", id, e);
            throw new RuntimeException("删除菜单失败: " + e.getMessage(), e);
        }
    }

    private Menu convertToDomain(MenuPO po) {
        if (po == null) {
            return null;
        }
        
        Menu menu = new Menu();
        menu.setId(po.getId());
        menu.setParentId(po.getParentId());
        menu.setMenuCode(po.getMenuCode());
        menu.setMenuName(po.getMenuName());
        menu.setPath(po.getPath());
        menu.setIcon(po.getIcon());
        menu.setSort(po.getSort());
        menu.setDescription(po.getDescription());
        menu.setStatus(po.getStatus());
        menu.setCreateTime(po.getCreateTime());
        menu.setUpdateTime(po.getUpdateTime());
        menu.setDeleted(po.getDeleted());
        return menu;
    }
    
    private MenuPO convertToPO(Menu menu) {
        if (menu == null) {
            return null;
        }
        
        MenuPO po = new MenuPO();
        po.setId(menu.getId());
        po.setParentId(menu.getParentId());
        po.setMenuCode(menu.getMenuCode());
        po.setMenuName(menu.getMenuName());
        po.setPath(menu.getPath());
        po.setIcon(menu.getIcon());
        po.setSort(menu.getSort());
        po.setDescription(menu.getDescription());
        po.setStatus(menu.getStatus());
        po.setCreateTime(menu.getCreateTime());
        po.setUpdateTime(menu.getUpdateTime());
        po.setDeleted(menu.getDeleted());
        return po;
    }
}

