<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <el-icon><Document /></el-icon>
          <span>技术社区</span>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          router
          class="nav-menu"
        >
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            <span>{{ $t('common.home') }}</span>
          </el-menu-item>
          <el-menu-item index="/topic">
            <el-icon><Collection /></el-icon>
            <span>{{ $t('common.topic') }}</span>
          </el-menu-item>
          <el-menu-item index="/boiling-point">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ $t('common.boilingPoint') }}</span>
          </el-menu-item>
        </el-menu>
        <div class="header-right">
          <el-select 
            :model-value="locale" 
            @change="handleLocaleChange" 
            size="small"
            style="width: 100px; margin-right: 15px"
          >
            <el-option label="中文" value="zh-CN" />
            <el-option label="English" value="en-US" />
          </el-select>
          <el-button 
            v-if="!userStore.token" 
            type="primary" 
            size="small"
            @click="$router.push('/login')"
          >
            {{ $t('common.login') }}
          </el-button>
          <el-dropdown v-else @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" style="margin-right: 8px">
                {{ userStore.userInfo?.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span>{{ userStore.userInfo?.username }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="publish">
                  <el-icon><Edit /></el-icon>
                  {{ $t('article.publish') }}
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  {{ $t('common.logout') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <slot />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { 
  Document, House, Collection, ChatDotRound, 
  ArrowDown, Edit, SwitchButton 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store'

const route = useRoute()
const router = useRouter()
const { locale: i18nLocale } = useI18n()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const locale = computed(() => userStore.locale || 'zh-CN')

const handleLocaleChange = (value: string) => {
  userStore.setLocale(value)
  i18nLocale.value = value
}

const handleCommand = (command: string) => {
  if (command === 'publish') {
    router.push('/article/publish')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
  height: 64px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    padding: 0 20px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: bold;
    color: #409eff;
    cursor: pointer;
    transition: opacity 0.3s;

    &:hover {
      opacity: 0.8;
    }

    .el-icon {
      font-size: 24px;
    }
  }

  .nav-menu {
    flex: 1;
    border-bottom: none;
    margin-left: 40px;
  }

  .header-right {
    display: flex;
    align-items: center;

    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 4px 12px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }
    }
  }
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 64px);
}
</style>

