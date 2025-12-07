<template>
  <Layout>
    <div class="topic-list">
      <div class="page-header">
        <h1>{{ $t('topic.title') }}</h1>
        <p>发现你感兴趣的技术专题</p>
      </div>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="topicList.length > 0" class="topic-grid">
        <div
          v-for="topic in topicList"
          :key="topic.id"
          class="topic-card"
          @click="$router.push(`/topic/${topic.id}`)"
        >
          <div class="cover" :style="{ backgroundImage: `url(${topic.coverImage || ''})` }">
            <div class="cover-overlay"></div>
          </div>
          <div class="topic-content">
            <h3>{{ topic.topicName }}</h3>
            <p class="description">{{ topic.description || '暂无描述' }}</p>
            <div class="meta">
              <div class="meta-item">
                <el-icon><Document /></el-icon>
                <span>{{ topic.articleCount || 0 }} 篇文章</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ topic.followerCount || 0 }} 人关注</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无专题" />
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { Document, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout.vue'
import { topicApi, type Topic } from '@/api/topic'

const { t } = useI18n()
const topicList = ref<Topic[]>([])
const loading = ref(false)

const loadTopics = async () => {
  loading.value = true
  try {
    const data = await topicApi.getTopicList()
    topicList.value = data || []
  } catch (error: any) {
    if (error?.response?.status !== 404) {
      ElMessage.error('加载专题失败')
    }
    topicList.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTopics()
})
</script>

<style scoped lang="scss">
.topic-list {
  .page-header {
    margin-bottom: 30px;
    text-align: center;

    h1 {
      font-size: 32px;
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

  .topic-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 24px;

    .topic-card {
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        transform: translateY(-4px);
      }

      .cover {
        width: 100%;
        height: 180px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        background-size: cover;
        background-position: center;
        position: relative;

        .cover-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.1);
        }
      }

      .topic-content {
        padding: 20px;

        h3 {
          font-size: 20px;
          font-weight: 600;
          color: #333;
          margin: 0 0 12px 0;
        }

        .description {
          color: #666;
          font-size: 14px;
          line-height: 1.6;
          margin: 0 0 16px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .meta {
          display: flex;
          gap: 20px;
          color: #999;
          font-size: 13px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 6px;

            .el-icon {
              font-size: 14px;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .topic-list {
    .topic-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>

