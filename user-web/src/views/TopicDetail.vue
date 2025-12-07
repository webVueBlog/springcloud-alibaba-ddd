<template>
  <Layout>
    <div class="topic-detail" v-if="topic">
      <div class="topic-header">
        <div class="topic-cover" :style="{ backgroundImage: `url(${topic.coverImage || ''})` }">
          <div class="cover-overlay"></div>
        </div>
        <div class="topic-info">
          <h1>{{ topic.topicName }}</h1>
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
          <div class="actions" v-if="userStore.token">
            <el-button type="primary">关注专题</el-button>
          </div>
        </div>
      </div>

      <div class="article-list">
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="articles.length > 0" class="articles">
          <article-card
            v-for="article in articles"
            :key="article.id"
            :article="article"
            @click="$router.push(`/article/${article.id}`)"
          />
        </div>
        <el-empty v-else description="该专题暂无文章" />
      </div>
    </div>
    <div v-else-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>
    <div v-else class="error-state">
      <el-empty description="专题不存在" />
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Document, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { topicApi, type Topic } from '@/api/topic'
import { articleApi, type Article } from '@/api/article'
import { useUserStore } from '@/store'

const route = useRoute()
const userStore = useUserStore()

const topic = ref<Topic>()
const articles = ref<Article[]>([])
const loading = ref(false)

const loadTopic = async () => {
  loading.value = true
  try {
    const data = await topicApi.getTopicById(Number(route.params.id))
    topic.value = data
    if (data) {
      loadArticles()
    }
  } catch (error: any) {
    if (error?.response?.status !== 404) {
      ElMessage.error('加载专题失败')
    }
  } finally {
    loading.value = false
  }
}

const loadArticles = async () => {
  try {
    // TODO: 根据专题ID查询文章列表
    const data = await articleApi.getArticleList({ publishStatus: 'PUBLISHED' })
    articles.value = data || []
  } catch (error) {
    console.error('加载文章失败:', error)
    articles.value = []
  }
}

onMounted(() => {
  loadTopic()
})
</script>

<style scoped lang="scss">
.topic-detail {
  .topic-header {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 30px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    .topic-cover {
      width: 100%;
      height: 300px;
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
        background: rgba(0, 0, 0, 0.3);
      }
    }

    .topic-info {
      padding: 30px;

      h1 {
        font-size: 32px;
        font-weight: 700;
        color: #333;
        margin: 0 0 16px 0;
      }

      .description {
        font-size: 16px;
        color: #666;
        line-height: 1.6;
        margin: 0 0 20px 0;
      }

      .meta {
        display: flex;
        gap: 30px;
        margin-bottom: 20px;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #666;
          font-size: 14px;
        }
      }

      .actions {
        // 操作按钮
      }
    }
  }

  .article-list {
    .loading-state {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
    }

    .articles {
      // 文章列表样式由 ArticleCard 组件控制
    }
  }
}

.loading-state,
.error-state {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
}
</style>

