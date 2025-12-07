import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/ArticleDetail.vue')
  },
  {
    path: '/article/publish',
    name: 'ArticlePublish',
    component: () => import('@/views/ArticlePublish.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/topic',
    name: 'TopicList',
    component: () => import('@/views/TopicList.vue')
  },
  {
    path: '/topic/:id',
    name: 'TopicDetail',
    component: () => import('@/views/TopicDetail.vue')
  },
  {
    path: '/boiling-point',
    name: 'BoilingPoint',
    component: () => import('@/views/BoilingPoint.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  // 如果已登录，访问登录/注册页时重定向到首页
  if ((to.path === '/login' || to.path === '/register') && localStorage.getItem('token')) {
    next('/')
    return
  }
  
  next()
})

export default router

