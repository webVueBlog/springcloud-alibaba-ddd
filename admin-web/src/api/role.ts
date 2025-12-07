import request from './request'

export interface Role {
  id?: number
  roleCode: string
  roleName: string
  description?: string
  status?: number
}

export interface Permission {
  id: number
  permissionCode: string
  permissionName: string
  permissionType: string
  resource?: string
  method?: string
}

export const roleApi = {
  // 获取角色列表
  getRoleList: (params?: { roleName?: string; roleCode?: string; status?: number }) => {
    return request.get<Role[]>('/user/role/list', { params })
  },
  // 获取角色详情
  getRoleById: (id: number) => {
    return request.get<Role>(`/user/role/${id}`)
  },
  // 创建角色
  createRole: (role: Role) => {
    return request.post<Role>('/user/role', role)
  },
  // 更新角色
  updateRole: (id: number, role: Role) => {
    return request.put<Role>(`/user/role/${id}`, role)
  },
  // 删除角色
  deleteRole: (id: number) => {
    return request.delete(`/user/role/${id}`)
  },
  // 获取权限列表
  getPermissionList: () => {
    return request.get<Permission[]>('/user/permission/list')
  },
  // 获取角色权限树
  getRolePermissionTree: (roleId: number) => {
    return request.get<{ tree: Permission[]; checked: number[] }>(`/user/role/${roleId}/permissions/tree`)
  },
  // 分配角色权限
  assignRolePermissions: (roleId: number, permissionIds: number[]) => {
    return request.post(`/user/role/${roleId}/permissions`, { permissionIds })
  }
}

