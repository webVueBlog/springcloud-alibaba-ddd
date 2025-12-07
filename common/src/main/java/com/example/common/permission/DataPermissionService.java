package com.example.common.permission;

import com.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据权限服务
 * <p>
 * 提供数据权限检查和过滤功能
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class DataPermissionService {
    
    /**
     * 检查用户是否有访问指定内容的权限
     * 
     * @param accessLevel 访问级别
     * @param contentUserId 内容作者ID
     * @param currentUserId 当前用户ID
     * @param userPermissions 用户权限列表（权限编码列表）
     * @return 是否有权限
     */
    public boolean checkContentAccess(String accessLevel, Long contentUserId, Long currentUserId, List<String> userPermissions) {
        if (accessLevel == null) {
            accessLevel = "PUBLIC";
        }
        
        AccessLevel level = AccessLevel.fromString(accessLevel);
        
        switch (level) {
            case PUBLIC:
                // 公开内容，所有人可访问
                return true;
                
            case PRIVATE:
                // 私有内容，仅作者可访问
                if (currentUserId == null) {
                    return false;
                }
                return currentUserId.equals(contentUserId);
                
            case PAID:
                // 付费内容，需要检查是否已付费
                // TODO: 实现付费检查逻辑
                if (currentUserId == null) {
                    return false;
                }
                // 临时：有管理员权限的用户可以访问付费内容
                if (hasPermission(userPermissions, "ADMIN") || hasPermission(userPermissions, "CONTENT_PAID_ACCESS")) {
                    return true;
                }
                // TODO: 检查用户是否已购买
                return false;
                
            case SPECIFIED:
                // 指定用户可见，需要检查用户是否在指定列表中
                if (currentUserId == null) {
                    return false;
                }
                // 作者本人可以访问
                if (currentUserId.equals(contentUserId)) {
                    return true;
                }
                // TODO: 检查用户是否在指定列表中
                // 临时：有管理员权限的用户可以访问
                return hasPermission(userPermissions, "ADMIN");
                
            default:
                return false;
        }
    }
    
    /**
     * 检查用户是否有指定权限
     * 
     * @param userPermissions 用户权限列表
     * @param permissionCode 权限编码
     * @return 是否有权限
     */
    public boolean hasPermission(List<String> userPermissions, String permissionCode) {
        if (userPermissions == null || permissionCode == null) {
            return false;
        }
        return userPermissions.contains(permissionCode);
    }
    
    /**
     * 检查并抛出异常（如果没有权限）
     * 
     * @param accessLevel 访问级别
     * @param contentUserId 内容作者ID
     * @param currentUserId 当前用户ID
     * @param userPermissions 用户权限列表
     * @throws BusinessException 如果没有权限
     */
    public void checkContentAccessOrThrow(String accessLevel, Long contentUserId, Long currentUserId, List<String> userPermissions) {
        if (!checkContentAccess(accessLevel, contentUserId, currentUserId, userPermissions)) {
            throw new BusinessException("无权限访问该内容");
        }
    }
}

