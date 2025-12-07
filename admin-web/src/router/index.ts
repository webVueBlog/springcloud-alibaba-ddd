import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: {
      title: '首页',
      requiresAuth: true
    }
  },
  {
    path: '/system/menu',
    name: 'MenuManagement',
    component: () => import('@/views/MenuManagement.vue'),
    meta: {
      title: '菜单管理',
      requiresAuth: true
    }
  },
  {
    path: '/system/role',
    name: 'RoleManagement',
    component: () => import('@/views/RoleManagement.vue'),
    meta: {
      title: '角色管理',
      requiresAuth: true
    }
  },
  {
    path: '/system/user',
    name: 'UserManagement',
    component: () => import('@/views/UserManagement.vue'),
    meta: {
      title: '账号管理',
      requiresAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
  }
  next()
})

export default router
