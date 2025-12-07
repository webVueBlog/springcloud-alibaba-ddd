<template>
  <el-container class="dashboard-container">
    <el-header class="header">
      <div class="header-left">
        <h2>管理后台</h2>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ userStore.userInfo?.username || '管理员' }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu
          default-active="/dashboard"
          class="menu"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="/system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/menu">
              <el-icon><Menu /></el-icon>
              <span>菜单管理</span>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <el-icon><UserFilled /></el-icon>
              <span>角色管理</span>
            </el-menu-item>
            <el-menu-item index="/system/user">
              <el-icon><User /></el-icon>
              <span>账号管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <div class="welcome">
          <h1>欢迎使用管理后台</h1>
          <p>当前用户: {{ userStore.userInfo?.username }}</p>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, ArrowDown, HomeFilled, Setting, Menu as MenuIcon, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch {
      // 用户取消
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;

  .header-left h2 {
    margin: 0;
    color: #333;
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      color: #333;

      .el-icon {
        margin-right: 5px;
      }
    }
  }
}

.aside {
  background: #fff;
  border-right: 1px solid #e6e6e6;

  .menu {
    border-right: none;
  }
}

.main {
  background: #f5f5f5;
  padding: 20px;

  .welcome {
    background: #fff;
    padding: 40px;
    border-radius: 8px;
    text-align: center;

    h1 {
      margin-bottom: 20px;
      color: #333;
    }

    p {
      color: #666;
    }
  }
}
</style>
