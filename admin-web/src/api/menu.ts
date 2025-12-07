import request from './request'

export interface Menu {
  id?: number
  parentId?: number
  menuCode: string
  menuName: string
  path?: string
  icon?: string
  sort?: number
  description?: string
  status?: number
  children?: Menu[]
}

export const menuApi = {
  // 获取菜单列表
  getMenuList: (params?: { menuName?: string; menuCode?: string; status?: number }) => {
    return request.get<Menu[]>('/user/menu/list', { params })
  },
  // 获取菜单树
  getMenuTree: () => {
    return request.get<Menu[]>('/user/menu/tree')
  },
  // 获取用户菜单树
  getUserMenuTree: () => {
    return request.get<Menu[]>('/user/menu/user-tree')
  },
  // 创建菜单
  createMenu: (menu: Menu) => {
    return request.post<Menu>('/user/menu', menu)
  },
  // 更新菜单
  updateMenu: (id: number, menu: Menu) => {
    return request.put<Menu>(`/user/menu/${id}`, menu)
  },
  // 删除菜单
  deleteMenu: (id: number) => {
    return request.delete(`/user/menu/${id}`)
  }
}

