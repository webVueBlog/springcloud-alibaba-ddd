import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const locale = ref<string>(localStorage.getItem('locale') || 'zh-CN')

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: UserInfo | null) => {
    userInfo.value = info
    if (info) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  const setLocale = (newLocale: string) => {
    locale.value = newLocale
    localStorage.setItem('locale', newLocale)
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  // 初始化时从localStorage加载用户信息
  const initUserInfo = () => {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      try {
        userInfo.value = JSON.parse(userInfoStr)
      } catch (e) {
        // 忽略解析错误
      }
    }
    const savedToken = localStorage.getItem('token')
    if (savedToken) {
      token.value = savedToken
    }
  }
  
  // 初始化
  initUserInfo()

  return {
    token,
    userInfo,
    locale,
    setToken,
    setUserInfo,
    setLocale,
    logout
  }
})

