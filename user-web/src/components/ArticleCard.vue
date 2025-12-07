<template>
  <div class="article-card" @click="$emit('click')">
    <div class="article-header">
      <div class="article-cover" v-if="article.coverImage">
        <img :src="article.coverImage" :alt="article.title" />
      </div>
      <div class="article-content">
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
        <div class="article-meta">
          <span class="meta-item">
            <el-icon><View /></el-icon>
            {{ article.viewCount || 0 }}
          </span>
          <span class="meta-item">
            <el-icon><Like /></el-icon>
            {{ article.likeCount || 0 }}
          </span>
          <span class="meta-item">
            <el-icon><ChatLineRound /></el-icon>
            {{ article.commentCount || 0 }}
          </span>
          <span class="meta-item" v-if="article.publishTime">
            <el-icon><Clock /></el-icon>
            {{ formatTime(article.publishTime) }}
          </span>
        </div>
        <div class="article-tags" v-if="article.tagNames && article.tagNames.length > 0">
          <el-tag
            v-for="tag in article.tagNames.slice(0, 3)"
            :key="tag"
            size="small"
            class="tag"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </div>
    <div class="article-footer">
      <div class="article-badges">
        <el-tag v-if="article.isTop" type="danger" size="small">置顶</el-tag>
        <el-tag v-if="article.isHot" type="warning" size="small">热门</el-tag>
        <el-tag v-if="article.isRecommend" type="success" size="small">推荐</el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { View, Like, ChatLineRound, Clock } from '@element-plus/icons-vue'
import type { Article } from '@/api/article'
import { formatRelativeTime } from '@/utils/date'

defineProps<{
  article: Article
}>()

defineEmits<{
  click: []
}>()

const formatTime = (time: string | Date | undefined) => {
  return formatRelativeTime(time)
}
</script>

<style scoped lang="scss">
@use '@/styles/ink-painting.scss' as *;

.article-card {
  @include ink-card;
  padding: 24px;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 
      0 6px 24px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(0, 0, 0, 0.05) inset;
    transform: translateY(-3px);
  }

  .article-header {
    display: flex;
    gap: 16px;

    .article-cover {
      flex-shrink: 0;
      width: 200px;
      height: 120px;
      border-radius: 0;
      overflow: hidden;
      background: $paper-cream;
      border: 1px solid rgba(0, 0, 0, 0.08);
      position: relative;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, 
          rgba(0, 0, 0, 0.02) 0%, 
          transparent 50%,
          rgba(0, 0, 0, 0.02) 100%);
        pointer-events: none;
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        filter: grayscale(10%) contrast(1.05);
      }
    }

    .article-content {
      flex: 1;
      min-width: 0;

      .article-title {
        @include ink-text-effect;
        font-size: 20px;
        font-weight: 500;
        color: $ink-black;
        margin: 0 0 14px 0;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .article-summary {
        @include ink-text-effect;
        color: $ink-gray;
        font-size: 15px;
        line-height: 1.7;
        margin: 0 0 14px 0;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        opacity: 0.85;
        
        &::after {
          display: none;
        }
      }

      .article-meta {
        display: flex;
        gap: 24px;
        margin-bottom: 14px;
        @include ink-text-effect;
        color: $ink-light-gray;
        font-size: 13px;
        opacity: 0.7;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 6px;

          .el-icon {
            font-size: 14px;
          }
        }
        
        &::after {
          display: none;
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
  }

  .article-footer {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid rgba(0, 0, 0, 0.08);

    .article-badges {
      display: flex;
      gap: 10px;
      
      :deep(.el-tag) {
        border-radius: 0;
        border: 1px solid rgba(0, 0, 0, 0.1);
        font-family: 'ZCOOL QingKe HuangYou', 'Microsoft YaHei', sans-serif;
        font-size: 12px;
        padding: 4px 10px;
      }
    }
  }
}

@media (max-width: 768px) {
  .article-card {
    .article-header {
      flex-direction: column;

      .article-cover {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>

