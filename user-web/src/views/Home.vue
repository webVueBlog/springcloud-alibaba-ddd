<template>
  <Layout>
    <div class="home-content">
      <!-- 顶部横幅（可选） -->
      <div class="home-banner">
        <div class="banner-content">
          <h1 class="banner-title">技术社区</h1>
          <p class="banner-subtitle">分享技术，共同成长</p>
        </div>
      </div>

      <div class="content-wrapper">
        <!-- 主内容区 -->
        <div class="article-list">
          <!-- 筛选栏 -->
          <div class="filter-bar">
            <div class="filter-left">
              <el-radio-group v-model="filterStatus" @change="loadArticles" size="default">
                <el-radio-button label="all">
                  <el-icon><List /></el-icon>
                  <span>全部</span>
                </el-radio-button>
                <el-radio-button label="hot">
                  <el-icon><Fire /></el-icon>
                  <span>热门</span>
                </el-radio-button>
                <el-radio-button label="latest">
                  <el-icon><Clock /></el-icon>
                  <span>最新</span>
                </el-radio-button>
              </el-radio-group>
            </div>
            <div class="filter-right">
              <span class="article-count" v-if="!loading && articleList.length > 0">
                共 {{ articleList.length }} 篇文章
              </span>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <el-skeleton :rows="3" animated :loading="loading">
              <template #template>
                <div v-for="i in 3" :key="i" class="skeleton-item">
                  <el-skeleton-item variant="rect" style="width: 100%; height: 200px; margin-bottom: 16px;" />
                  <el-skeleton-item variant="h3" style="width: 60%; margin-bottom: 8px;" />
                  <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 8px;" />
                  <el-skeleton-item variant="text" style="width: 80%;" />
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 文章列表 -->
          <div v-else-if="articleList.length > 0" class="articles">
            <transition-group name="article-list" tag="div">
              <article-card
                v-for="article in articleList"
                :key="article.id"
                :article="article"
                class="article-item"
                @click="handleArticleClick(article)"
              />
            </transition-group>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <el-empty description="暂无文章">
              <template #image>
                <el-icon :size="100" color="#c0c4cc"><Document /></el-icon>
              </template>
              <template #description>
                <p class="empty-description">还没有文章，快来发布第一篇吧！</p>
              </template>
              <el-button type="primary" @click="loadArticles" :loading="loading">
                <el-icon><Refresh /></el-icon>
                <span>刷新</span>
              </el-button>
            </el-empty>
            <div class="empty-tips" v-if="hasError">
              <el-alert
                title="数据加载失败"
                type="warning"
                :closable="false"
                show-icon
              >
                <template #default>
                  <p>可能的原因：</p>
                  <ul>
                    <li>后端服务未启动（Gateway、Content Service）</li>
                    <li>数据库连接失败</li>
                    <li>网络连接问题</li>
                  </ul>
                  <p style="margin-top: 10px;">
                    <el-button type="text" @click="loadArticles">点击重试</el-button>
                  </p>
                </template>
              </el-alert>
            </div>
          </div>
        </div>

        <!-- 侧边栏 -->
        <div class="sidebar">
          <!-- 推荐专题 -->
          <div class="sidebar-card">
            <div class="card-header">
              <h3>
                <el-icon><Collection /></el-icon>
                <span>推荐专题</span>
              </h3>
            </div>
            <div v-if="topicsLoading" class="topic-loading">
              <el-skeleton :rows="3" animated />
            </div>
            <div v-else-if="recommendTopics.length > 0" class="topic-list">
              <div 
                v-for="topic in recommendTopics" 
                :key="topic.id"
                class="topic-item"
                @click="handleTopicClick(topic)"
              >
                <div class="topic-cover" :style="{ backgroundImage: `url(${topic.coverImage || ''})` }">
                  <div class="topic-badge" v-if="topic.articleCount">
                    {{ topic.articleCount }} 篇
                  </div>
                </div>
                <div class="topic-info">
                  <h4>{{ topic.topicName }}</h4>
                  <p>{{ topic.description || '暂无描述' }}</p>
                  <div class="topic-meta">
                    <span v-if="topic.followerCount">
                      <el-icon><User /></el-icon>
                      {{ topic.followerCount }} 关注
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="topic-empty">
              <el-empty description="暂无专题" :image-size="60" />
            </div>
          </div>

          <!-- 热门标签 -->
          <div class="sidebar-card">
            <div class="card-header">
              <h3>
                <el-icon><PriceTag /></el-icon>
                <span>热门标签</span>
              </h3>
            </div>
            <div class="tag-list">
              <el-tag
                v-for="tag in hotTags"
                :key="tag"
                class="tag-item"
                effect="plain"
                size="default"
                @click="handleTagClick(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div v-if="hotTags.length === 0" class="tag-empty">
              <p>暂无标签</p>
            </div>
          </div>

          <!-- 快速操作 -->
          <div class="sidebar-card quick-actions" v-if="userStore.token">
            <div class="card-header">
              <h3>
                <el-icon><Operation /></el-icon>
                <span>快速操作</span>
              </h3>
            </div>
            <div class="action-list">
              <el-button type="primary" @click="$router.push('/article/publish')" style="width: 100%;">
                <el-icon><Edit /></el-icon>
                <span>发布文章</span>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Document, Refresh, List, Fire, Clock, 
  Collection, PriceTag, User, Operation, Edit 
} from '@element-plus/icons-vue'
import Layout from '@/components/Layout.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { articleApi, type Article } from '@/api/article'
import { topicApi, type Topic } from '@/api/topic'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const articleList = ref<Article[]>([])
const recommendTopics = ref<Topic[]>([])
const hotTags = ref<string[]>(['Vue', 'React', 'Java', 'Python', 'Spring Boot', '微服务'])
const loading = ref<boolean>(false)
const topicsLoading = ref<boolean>(false)
const hasError = ref<boolean>(false)
const filterStatus = ref<string>('all')

const loadArticles = async () => {
  console.log('开始加载文章列表...')
  loading.value = true
  hasError.value = false
  try {
    const params: any = { publishStatus: 'PUBLISHED' }
    
    // 根据筛选条件添加参数
    if (filterStatus.value === 'hot') {
      // 热门文章（按阅读数排序）
      params.orderBy = 'viewCount'
      params.order = 'desc'
    } else if (filterStatus.value === 'latest') {
      // 最新文章（按发布时间排序）
      params.orderBy = 'publishTime'
      params.order = 'desc'
    }
    
    console.log('请求参数:', params)
    console.log('请求URL: /api/content/article')
    const data = await articleApi.getArticleList(params)
    console.log('文章数据加载成功:', data)
    articleList.value = Array.isArray(data) ? data : []
    console.log('文章列表数量:', articleList.value.length)
    
    if (articleList.value.length === 0 && filterStatus.value === 'all') {
      hasError.value = true
    }
  } catch (error: any) {
    console.error('加载文章失败:', error)
    hasError.value = true
    articleList.value = []
    
    if (error?.response?.status === 404) {
      // 404 不显示错误消息，可能是接口不存在
      console.warn('文章接口不存在，请检查后端服务')
    } else if (error?.response?.status === 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else if (error?.code === 'ECONNABORTED' || error?.message?.includes('timeout')) {
      ElMessage.error('请求超时，请检查网络连接')
    } else if (!error?.response) {
      ElMessage.error('无法连接到服务器，请检查后端服务是否启动')
    } else {
      ElMessage.error(error?.response?.data?.message || '加载文章失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const loadRecommendTopics = async () => {
  console.log('开始加载专题列表...')
  topicsLoading.value = true
  try {
    console.log('请求URL: /api/content/topic')
    const data = await topicApi.getTopicList()
    console.log('专题数据加载成功:', data)
    recommendTopics.value = Array.isArray(data) ? data.slice(0, 5) : []
    console.log('专题列表数量:', recommendTopics.value.length)
  } catch (error: any) {
    console.log('加载推荐专题失败:', error)
    recommendTopics.value = []
    // 专题加载失败不影响主流程，静默处理
  } finally {
    topicsLoading.value = false
  }
}

const handleArticleClick = (article: Article) => {
  if (article.id) {
    router.push(`/article/${article.id}`)
  }
}

const handleTopicClick = (topic: Topic) => {
  if (topic.id) {
    router.push(`/topic/${topic.id}`)
  }
}

const handleTagClick = (tag: string) => {
  // TODO: 实现标签筛选功能
  ElMessage.info(`点击了标签: ${tag}`)
  // 可以在这里实现标签筛选逻辑
  // filterStatus.value = 'tag'
  // loadArticles()
}

onMounted(() => {
  console.log('Home组件已挂载，开始加载数据...')
  loadArticles()
  loadRecommendTopics()
})
</script>

<style scoped lang="scss">
@use '@/styles/ink-painting.scss' as *;

.home-content {
  min-height: calc(100vh - 64px);
  @include ink-background;

  // 顶部横幅 - 水墨画风格
  .home-banner {
    background: linear-gradient(135deg, 
      rgba(26, 26, 26, 0.95) 0%, 
      rgba(74, 74, 74, 0.9) 50%,
      rgba(26, 26, 26, 0.95) 100%);
    color: $paper-white;
    padding: 80px 20px;
    text-align: center;
    margin-bottom: 40px;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-image: 
        radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.05) 0%, transparent 50%),
        radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.03) 0%, transparent 50%);
      pointer-events: none;
    }

    .banner-content {
      max-width: 1200px;
      margin: 0 auto;
      position: relative;
      z-index: 1;

      .banner-title {
        @include ink-text-effect;
        font-size: 48px;
        font-weight: 400;
        margin: 0 0 16px 0;
        color: $paper-white;
        text-shadow: 
          0 2px 4px rgba(0, 0, 0, 0.3),
          0 4px 8px rgba(0, 0, 0, 0.2);
        letter-spacing: 2px;
        
        &::after {
          display: none; // 横幅标题不需要下划线
        }
      }

      .banner-subtitle {
        @include ink-text-effect;
        font-size: 20px;
        color: $paper-white;
        margin: 0;
        opacity: 0.85;
        font-weight: 300;
        letter-spacing: 1px;
        
        &::after {
          display: none;
        }
      }
    }
  }

  .content-wrapper {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 40px;
    display: flex;
    gap: 20px;
    align-items: flex-start;
  }

  .article-list {
    flex: 1;
    min-width: 0;

    .filter-bar {
      @include ink-card;
      padding: 20px 24px;
      margin-bottom: 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;

      .filter-left {
        display: flex;
        align-items: center;
        gap: 8px;

        :deep(.el-radio-button) {
          .el-radio-button__inner {
            display: flex;
            align-items: center;
            gap: 4px;
            padding: 8px 16px;
          }
        }
      }

      .filter-right {
        .article-count {
          @include ink-text-effect;
          color: $ink-gray;
          font-size: 14px;
          opacity: 0.8;
          
          &::after {
            display: none;
          }
        }
      }
    }

    .loading-state {
      @include ink-card;
      padding: 24px;
      margin-bottom: 24px;

      .skeleton-item {
        margin-bottom: 20px;
      }
    }

    .articles {
      .article-item {
        margin-bottom: 20px;
        transition: all 0.3s ease;
      }
    }

    // 文章列表动画
    .article-list-enter-active,
    .article-list-leave-active {
      transition: all 0.3s ease;
    }

    .article-list-enter-from {
      opacity: 0;
      transform: translateY(20px);
    }

    .article-list-leave-to {
      opacity: 0;
      transform: translateY(-20px);
    }

    .empty-state {
      @include ink-card;
      padding: 80px 40px;
      text-align: center;

      .empty-description {
        @include ink-text-effect;
        color: $ink-gray;
        font-size: 16px;
        margin: 16px 0 24px;
        opacity: 0.8;
      }

      .empty-tips {
        margin-top: 30px;
        max-width: 600px;
        margin-left: auto;
        margin-right: auto;

        :deep(.el-alert) {
          text-align: left;

          ul {
            margin: 8px 0;
            padding-left: 20px;

            li {
              margin: 4px 0;
              line-height: 1.6;
            }
          }
        }
      }
    }
  }

  .sidebar {
    width: 300px;
    flex-shrink: 0;

    .sidebar-card {
      @include ink-card;
      padding: 24px;
      margin-bottom: 24px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 
          0 4px 16px rgba(0, 0, 0, 0.08),
          0 0 0 1px rgba(0, 0, 0, 0.04) inset;
        transform: translateY(-2px);
      }

      .card-header {
        margin-bottom: 20px;
        padding-bottom: 16px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.08);

        h3 {
          @include ink-text-effect;
          font-size: 18px;
          font-weight: 500;
          color: $ink-black;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 10px;

          .el-icon {
            font-size: 20px;
            color: $ink-black;
            opacity: 0.7;
          }
        }
      }

      .topic-loading {
        padding: 10px 0;
      }

      .topic-list {
        .topic-item {
          display: flex;
          gap: 12px;
          padding: 12px;
          cursor: pointer;
          transition: all 0.3s;
          border-radius: 8px;
          margin-bottom: 8px;
          position: relative;

          &:hover {
            background-color: #f5f7fa;
            transform: translateX(4px);
          }

          .topic-cover {
            width: 60px;
            height: 60px;
            border-radius: 8px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            background-size: cover;
            background-position: center;
            flex-shrink: 0;
            position: relative;
            overflow: hidden;

            .topic-badge {
              position: absolute;
              bottom: 4px;
              right: 4px;
              background: rgba(0, 0, 0, 0.6);
              color: #fff;
              font-size: 10px;
              padding: 2px 6px;
              border-radius: 4px;
            }
          }

          .topic-info {
            flex: 1;
            min-width: 0;

            h4 {
              @include ink-text-effect;
              font-size: 15px;
              font-weight: 500;
              color: $ink-black;
              margin: 0 0 8px 0;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            p {
              @include ink-text-effect;
              font-size: 13px;
              color: $ink-gray;
              margin: 0 0 8px 0;
              overflow: hidden;
              text-overflow: ellipsis;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              line-height: 1.5;
              opacity: 0.8;
              
              &::after {
                display: none;
              }
            }

            .topic-meta {
              display: flex;
              align-items: center;
              gap: 6px;
              font-size: 12px;
              color: $ink-light-gray;
              opacity: 0.7;

              .el-icon {
                font-size: 12px;
              }
            }
          }
        }
      }

      .topic-empty,
      .tag-empty {
        text-align: center;
        padding: 24px;
        @include ink-text-effect;
        color: $ink-light-gray;
        font-size: 14px;
        opacity: 0.6;
        
        &::after {
          display: none;
        }
      }

      .tag-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .tag-item {
          cursor: pointer;
          transition: all 0.3s;
          margin: 0;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
          }
        }
      }

      &.quick-actions {
        .action-list {
          .el-button {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .home-content {
    .home-banner {
      padding: 40px 20px;

      .banner-content {
        .banner-title {
          font-size: 32px;
        }

        .banner-subtitle {
          font-size: 16px;
        }
      }
    }

    .content-wrapper {
      flex-direction: column;
      padding: 0 16px 20px;
    }

    .sidebar {
      width: 100%;

      .sidebar-card {
        .topic-list .topic-item {
          padding: 10px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .home-content {
    .article-list {
      .filter-bar {
        flex-direction: column;
        align-items: stretch;

        .filter-left {
          width: 100%;

          :deep(.el-radio-group) {
            width: 100%;
            display: flex;

            .el-radio-button {
              flex: 1;

              .el-radio-button__inner {
                width: 100%;
                justify-content: center;
              }
            }
          }
        }

        .filter-right {
          width: 100%;
          text-align: center;
        }
      }
    }
  }
}
</style>

