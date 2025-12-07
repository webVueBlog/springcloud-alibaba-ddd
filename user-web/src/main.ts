import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { createI18n } from 'vue-i18n'
import '@/styles/ink-painting.scss'

import App from './App.vue'
import router from './router'
import zh from './locales/zh-CN'
import en from './locales/en-US'

const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: localStorage.getItem('locale') || 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zh,
    'en-US': en
  }
})

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(i18n)

// 确保 DOM 元素存在后再挂载
const appElement = document.getElementById('app')
if (appElement) {
  app.mount('#app')
  console.log('Vue app mounted successfully')
} else {
  console.error('App element not found!')
}

