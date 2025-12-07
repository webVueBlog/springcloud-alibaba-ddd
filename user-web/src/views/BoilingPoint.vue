<template>
  <Layout>
    <div class="boiling-point">
      <div class="page-header">
        <div>
          <h1>{{ $t('boilingPoint.title') }}</h1>
          <p>分享你的想法和见解</p>
        </div>
        <el-button 
          v-if="userStore.token" 
          type="primary" 
          size="large"
          @click="showPublishDialog = true"
        >
          <el-icon><Edit /></el-icon>
          {{ $t('boilingPoint.publish') }}
        </el-button>
        <el-button 
          v-else 
          type="primary" 
          size="large"
          @click="$router.push('/login')"
        >
          {{ $t('common.login') }}后发布
        </el-button>
      </div>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="boilingPointList.length > 0" class="boiling-point-list">
        <div
          v-for="item in boilingPointList"
          :key="item.id"
          class="boiling-point-card"
        >
          <div class="card-header">
            <el-avatar :size="40">U</el-avatar>
            <div class="user-info">
              <div class="username">用户 {{ item.userId }}</div>
              <div class="time">{{ formatTime(item.createTime) }}</div>
            </div>
          </div>
          <div class="card-content">
            <p>{{ item.content }}</p>
          </div>
          <div class="card-actions">
            <el-button link @click="handleLike(item.id!)" :disabled="!userStore.token">
              <el-icon><Like /></el-icon>
              <span>{{ item.likeCount || 0 }}</span>
            </el-button>
            <el-button link :disabled="!userStore.token">
              <el-icon><ChatLineRound /></el-icon>
              <span>{{ item.commentCount || 0 }}</span>
            </el-button>
            <el-button link :disabled="!userStore.token">
              <el-icon><Share /></el-icon>
              <span>分享</span>
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无沸点动态" />

      <!-- 发布沸点对话框 -->
      <el-dialog 
        v-model="showPublishDialog" 
        :title="$t('boilingPoint.publish')" 
        width="600px"
        :close-on-click-modal="false"
      >
        <el-form :model="form">
          <el-form-item>
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="6"
              :placeholder="$t('boilingPoint.placeholder')"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showPublishDialog = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="handlePublish" :loading="publishing">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { Like, ChatLineRound, Share, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout.vue'
import { useUserStore } from '@/store'
import { boilingPointApi, type BoilingPoint } from '@/api/boilingPoint'

const { t } = useI18n()
const userStore = useUserStore()

const boilingPointList = ref<BoilingPoint[]>([])
const showPublishDialog = ref(false)
const loading = ref(false)
const publishing = ref(false)
const form = ref({
  content: ''
})

const formatTime = (time: string | Date | undefined) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const loadBoilingPoints = async () => {
  loading.value = true
  try {
    const data = await boilingPointApi.getBoilingPointList()
    boilingPointList.value = data || []
  } catch (error: any) {
    if (error?.response?.status !== 404) {
      ElMessage.error('加载沸点失败')
    }
    boilingPointList.value = []
  } finally {
    loading.value = false
  }
}

const handlePublish = async () => {
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  publishing.value = true
  try {
    await boilingPointApi.createBoilingPoint({
      ...form.value,
      userId: userStore.userInfo?.userId
    })
    ElMessage.success('发布成功')
    showPublishDialog.value = false
    form.value.content = ''
    loadBoilingPoints()
  } catch (error) {
    ElMessage.error('发布失败')
  } finally {
    publishing.value = false
  }
}

const handleLike = async (id: number) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await boilingPointApi.likeBoilingPoint(id)
    ElMessage.success('点赞成功')
    loadBoilingPoints()
  } catch (error) {
    ElMessage.error('点赞失败')
  }
}

onMounted(() => {
  loadBoilingPoints()
})
</script>

<style scoped lang="scss">
.boiling-point {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding: 30px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    h1 {
      font-size: 28px;
      font-weight: 700;
      color: #333;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 16px;
      color: #666;
      margin: 0;
    }
  }

  .loading-state {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
  }

  .boiling-point-list {
    .boiling-point-card {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      }

      .card-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;

        .user-info {
          flex: 1;

          .username {
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 4px;
          }

          .time {
            font-size: 12px;
            color: #999;
          }
        }
      }

      .card-content {
        margin-bottom: 16px;

        p {
          font-size: 15px;
          line-height: 1.6;
          color: #333;
          margin: 0;
          white-space: pre-wrap;
          word-break: break-word;
        }
      }

      .card-actions {
        display: flex;
        gap: 24px;
        padding-top: 12px;
        border-top: 1px solid #f0f0f0;

        .el-button {
          color: #666;
          
          &:hover:not(:disabled) {
            color: #409eff;
          }
        }
      }
    }
  }
}
</style>

