<template>
  <div class="ink-container">
    <div class="ink-form-card">
      <div class="ink-decoration ink-decoration-1"></div>
      <div class="ink-decoration ink-decoration-2"></div>
      
      <h1 class="ink-form-title">注册</h1>
      
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-position="top"
        class="ink-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            class="ink-input"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            class="ink-input"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            class="ink-input"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="邮箱（可选）" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱地址"
            class="ink-input"
            :prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="ink-button"
            :loading="loading"
            @click="handleRegister"
            size="large"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="ink-divider"></div>
      
      <div class="form-footer">
        <span class="ink-text">已有账号？</span>
        <el-link 
          type="primary" 
          class="ink-link"
          @click="$router.push('/login')"
        >
          立即登录
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/store'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateEmail = (rule: any, value: string, callback: Function) => {
  if (value && value !== '') {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(value)) {
      callback(new Error('请输入正确的邮箱地址'))
    } else {
      callback()
    }
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await authApi.register({
          username: form.value.username,
          password: form.value.password,
          confirmPassword: form.value.confirmPassword,
          email: form.value.email || undefined
        })
        
        userStore.setToken(res.token)
        userStore.setUserInfo({ userId: res.userId, username: res.username })
        ElMessage.success('注册成功，已自动登录')
        
        // 跳转到重定向页面或首页
        const redirect = route.query.redirect as string
        router.push(redirect || '/')
      } catch (error: any) {
        ElMessage.error(error?.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
@use '@/styles/ink-painting.scss' as *;

.ink-form {
  :deep(.el-form-item__label) {
    @include ink-text-effect;
    font-size: 14px;
    color: $ink-gray;
    margin-bottom: 8px;
  }
  
  :deep(.el-input__wrapper) {
    background: transparent;
    border: none;
    border-bottom: 2px solid rgba(0, 0, 0, 0.1);
    border-radius: 0;
    box-shadow: none;
    padding: 0;
    
    &:hover {
      box-shadow: none;
    }
    
    &.is-focus {
      box-shadow: 0 2px 0 rgba(0, 0, 0, 0.1);
      border-bottom-color: $ink-black;
    }
  }
  
  :deep(.el-input__inner) {
    @include ink-input;
    padding-left: 32px;
  }
  
  :deep(.el-input__prefix) {
    left: 0;
    color: $ink-gray;
  }
  
  :deep(.el-button) {
    @include ink-button;
    margin-top: 8px;
  }
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  
  .ink-text {
    @include ink-text-effect;
    font-size: 14px;
    color: $ink-gray;
    margin-right: 8px;
  }
  
  .ink-link {
    @include ink-link;
    font-size: 14px;
  }
}
</style>

