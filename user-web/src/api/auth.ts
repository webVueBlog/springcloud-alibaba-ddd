import request from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  confirmPassword: string
  phone?: string
  email?: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
}

export const authApi = {
  // 用户名密码登录
  loginByUsername: (params: LoginParams) => {
    return request.post<LoginResponse>('/auth/login/username', params)
  },
  // 用户注册
  register: (params: RegisterParams) => {
    return request.post<LoginResponse>('/auth/register', params)
  }
}

