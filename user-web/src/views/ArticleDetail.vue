<template>
  <Layout>
    <div class="article-detail" v-if="article">
      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <div class="meta-left">
            <el-avatar :size="40" style="margin-right: 12px">
              {{ article.userId }}
            </el-avatar>
            <div>
              <div class="author-name">作者 ID: {{ article.userId }}</div>
              <div class="publish-time">
                <el-icon><Clock /></el-icon>
                {{ formatTime(article.publishTime) }}
              </div>
            </div>
          </div>
          <div class="meta-right">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span>{{ article.viewCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon><Like /></el-icon>
              <span>{{ article.likeCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon><ChatLineRound /></el-icon>
              <span>{{ article.commentCount || 0 }}</span>
            </div>
          </div>
        </div>
        <div class="article-tags" v-if="article.tagNames && article.tagNames.length > 0">
          <el-tag v-for="tag in article.tagNames" :key="tag" class="tag">{{ tag }}</el-tag>
        </div>
      </div>

      <div class="article-body">
        <div class="content-wrapper">
          <div class="article-content" v-html="renderedContent"></div>
        </div>
        <div class="article-sidebar">
          <div class="action-card">
            <el-button 
              type="primary" 
              :icon="Like" 
              @click="handleLike"
              :loading="liking"
            >
              点赞 {{ article.likeCount || 0 }}
            </el-button>
            <el-button :icon="Star" @click="handleCollect">
              收藏 {{ article.collectCount || 0 }}
            </el-button>
            <el-button :icon="Share">分享</el-button>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>评论 ({{ article.commentCount || 0 }})</h3>
        <comment-form
          v-if="userStore.token"
          :target-type="'ARTICLE'"
          :target-id="article.id!"
          @submit="handleCommentSubmit"
        />
        <comment-list
          ref="commentListRef"
          :target-type="'ARTICLE'"
          :target-id="article.id!"
          :current-user-id="userStore.userInfo?.userId"
          @refresh="loadArticle"
          @reply="handleReply"
        />
      </div>
    </div>
    <div v-else-if="loading" class="loading-state">
      <el-skeleton :rows="10" animated />
    </div>
    <div v-else class="error-state">
      <el-empty description="文章不存在或已被删除" />
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Like, Star, Share, View, ChatLineRound, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import Layout from '@/components/Layout.vue'
import CommentList from '@/components/CommentList.vue'
import CommentForm from '@/components/CommentForm.vue'
import { articleApi, type Article } from '@/api/article'
import { commentApi, type Comment } from '@/api/comment'
import { useUserStore } from '@/store'
import { formatRelativeTime } from '@/utils/date'

const route = useRoute()
const { t } = useI18n()
const userStore = useUserStore()

const article = ref<Article>()
const commentListRef = ref<InstanceType<typeof CommentList>>()
const liking = ref(false)
const loading = ref(false)

const renderedContent = computed(() => {
  if (!article.value) return ''
  if (article.value.contentType === 'MARKDOWN') {
    return marked(article.value.content)
  }
  return article.value.content
})

const formatTime = (time: string | Date | undefined) => {
  return formatRelativeTime(time)
}

const loadArticle = async () => {
  loading.value = true
  try {
    const data = await articleApi.getArticleById(Number(route.params.id))
    article.value = data
  } catch (error: any) {
    if (error?.response?.status !== 404) {
      ElMessage.error('加载文章失败')
    }
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!article.value) return
  liking.value = true
  try {
    await articleApi.likeArticle(article.value.id!)
    ElMessage.success('点赞成功')
    loadArticle()
  } catch (error) {
    ElMessage.error('点赞失败')
  } finally {
    liking.value = false
  }
}

const handleCollect = () => {
  ElMessage.info('收藏功能开发中')
}

const handleCommentSubmit = async (content: string, parentId?: number) => {
  if (!article.value?.id) return
  
  try {
    await commentApi.createComment({
      targetType: 'ARTICLE',
      targetId: article.value.id,
      parentId,
      content
    })
    ElMessage.success('评论成功')
    commentListRef.value?.refresh()
    // 刷新文章以更新评论数
    loadArticle()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || '评论失败')
  }
}

const handleReply = (comment: Comment, parentComment?: Comment) => {
  // TODO: 实现回复功能（可以打开回复表单）
  console.log('回复评论:', comment, parentComment)
}

onMounted(() => {
  loadArticle()
})
</script>

<style scoped lang="scss">
.article-detail {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .article-header {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;

    .article-title {
      font-size: 28px;
      font-weight: 700;
      color: #333;
      margin: 0 0 20px 0;
      line-height: 1.4;
    }

    .article-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .meta-left {
        display: flex;
        align-items: center;

        .author-name {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .publish-time {
          font-size: 12px;
          color: #999;
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .meta-right {
        display: flex;
        gap: 24px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 6px;
          color: #666;
          font-size: 14px;
        }
      }
    }

    .article-tags {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;

      .tag {
        margin: 0;
      }
    }
  }

  .article-body {
    display: flex;
    gap: 20px;

    .content-wrapper {
      flex: 1;
      min-width: 0;

      .article-content {
        line-height: 1.8;
        color: #333;
        font-size: 16px;

        :deep(h1), :deep(h2), :deep(h3) {
          margin-top: 24px;
          margin-bottom: 16px;
          font-weight: 600;
        }

        :deep(p) {
          margin-bottom: 16px;
        }

        :deep(code) {
          background: #f5f5f5;
          padding: 2px 6px;
          border-radius: 4px;
          font-family: 'Courier New', monospace;
        }

        :deep(pre) {
          background: #f5f5f5;
          padding: 16px;
          border-radius: 8px;
          overflow-x: auto;
          margin-bottom: 16px;
        }

        :deep(img) {
          max-width: 100%;
          border-radius: 8px;
          margin: 16px 0;
        }
      }
    }

    .article-sidebar {
      width: 200px;
      flex-shrink: 0;

      .action-card {
        position: sticky;
        top: 20px;
        background: #fff;
        border: 1px solid #f0f0f0;
        border-radius: 12px;
        padding: 20px;
        display: flex;
        flex-direction: column;
        gap: 12px;

        .el-button {
          width: 100%;
        }
      }
    }
  }

  .comment-section {
    margin-top: 40px;
    padding-top: 30px;
    border-top: 1px solid #f0f0f0;

    h3 {
      font-size: 20px;
      font-weight: 600;
      color: #333;
      margin-bottom: 20px;
    }

    .comment-form {
      margin-bottom: 30px;
    }

    .comment-list {
      // 评论列表样式
    }
  }
}

.loading-state {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
}

@media (max-width: 768px) {
  .article-detail {
    padding: 20px;

    .article-body {
      flex-direction: column;

      .article-sidebar {
        width: 100%;

        .action-card {
          position: static;
          flex-direction: row;
        }
      }
    }
  }
}
</style>

