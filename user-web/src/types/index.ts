/**
 * 全局类型定义
 */

// 用户信息
export interface UserInfo {
  userId: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  permissions?: string[]
}

// 分页参数
export interface PageParams {
  pageNum?: number
  pageSize?: number
}

// 分页结果
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}

// API响应
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 排序参数
export interface SortParams {
  orderBy?: string
  order?: 'asc' | 'desc'
}

