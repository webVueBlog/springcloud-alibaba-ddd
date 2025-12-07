import request from './request'

export interface LoginParams {
  username: string
  password: string
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
  // 手机号登录
  loginByPhone: (phone: string, code: string) => {
    return request.post<LoginResponse>('/auth/login/phone', { phone, code })
  },
  // 退出登录
  logout: () => {
    return request.post('/auth/logout')
  }
}
