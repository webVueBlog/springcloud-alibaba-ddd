import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加用户ID和权限信息到请求头（用于数据权限控制）
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      try {
        const userInfo = JSON.parse(userInfoStr)
        if (userInfo?.userId) {
          config.headers['X-User-Id'] = userInfo.userId.toString()
        }
        // 如果有权限信息，也添加到请求头
        if (userInfo?.permissions && Array.isArray(userInfo.permissions)) {
          config.headers['X-User-Permissions'] = userInfo.permissions.join(',')
        }
      } catch (e) {
        // 忽略解析错误
        console.warn('解析用户信息失败:', e)
      }
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else {
      console.error('API请求失败:', res.message || '请求失败')
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error) => {
    console.error('请求错误:', error)
    if (error.response) {
      // 服务器返回了错误状态码
      if (error.response.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else if (error.response.status === 404) {
        console.warn('API接口不存在:', error.config.url)
        // 404 错误不显示消息，避免干扰用户
      } else {
        ElMessage.error(error.response.data?.message || error.message || '网络错误')
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('网络错误: 无法连接到服务器')
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      // 其他错误
      console.error('请求配置错误:', error.message)
      ElMessage.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request

