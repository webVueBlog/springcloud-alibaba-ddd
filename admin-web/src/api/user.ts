import request from './request'

export interface User {
  id?: number
  username: string
  phone?: string
  email?: string
  status?: number
}

export interface UserListParams {
  username?: string
  phone?: string
  email?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

export const userApi = {
  // 获取用户列表
  getUserList: (params?: UserListParams) => {
    return request.get<{ list: User[]; total: number }>('/user/user/list', { params })
  },
  // 获取用户详情
  getUserById: (id: number) => {
    return request.get<User>(`/user/user/${id}`)
  },
  // 创建用户
  createUser: (user: User & { password: string }) => {
    return request.post<User>('/user/user', user)
  },
  // 更新用户
  updateUser: (id: number, user: User) => {
    return request.put<User>(`/user/user/${id}`, user)
  },
  // 删除用户
  deleteUser: (id: number) => {
    return request.delete(`/user/user/${id}`)
  },
  // 禁用/启用用户
  updateUserStatus: (id: number, status: number) => {
    return request.put(`/user/user/${id}/status`, { status })
  },
  // 重置密码
  resetPassword: (id: number, newPassword: string) => {
    return request.put(`/user/user/${id}/password`, { newPassword })
  }
}

