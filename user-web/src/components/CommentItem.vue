<template>
  <div class="comment-item">
    <div class="comment-header">
      <el-avatar :size="40" :src="comment.avatar">
        {{ comment.username?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="comment-info">
        <div class="username">{{ comment.username || `用户${comment.userId}` }}</div>
        <div class="time">{{ formatTime(comment.createTime) }}</div>
      </div>
      <div class="comment-actions" v-if="currentUserId">
        <el-button link size="small" @click="$emit('reply', comment)">
          <el-icon><ChatLineRound /></el-icon>
          回复
        </el-button>
        <el-button link size="small" @click="$emit('like', comment)">
          <el-icon><Like /></el-icon>
          {{ comment.likeCount || 0 }}
        </el-button>
        <el-button 
          v-if="currentUserId === comment.userId" 
          link 
          size="small" 
          type="danger"
          @click="handleDelete"
        >
          <el-icon><Delete /></el-icon>
          删除
        </el-button>
      </div>
    </div>
    <div class="comment-content">
      {{ comment.content }}
    </div>
    <div v-if="comment.replies && comment.replies.length > 0" class="replies">
        <comment-item
          v-for="reply in comment.replies"
          :key="reply.id"
          :comment="reply"
          :current-user-id="currentUserId"
          @reply="(c) => $emit('reply', c, comment)"
          @like="$emit('like', reply)"
          @delete="$emit('delete', reply)"
        />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChatLineRound, Like, Delete } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import type { Comment } from '@/api/comment'
import { formatRelativeTime } from '@/utils/date'

const props = defineProps<{
  comment: Comment
  currentUserId?: number
}>()

const emit = defineEmits<{
  reply: [comment: Comment]
  like: [comment: Comment]
  delete: [comment: Comment]
}>()

const formatTime = (time: string | Date | undefined) => {
  return formatRelativeTime(time)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    emit('delete', props.comment)
  } catch {
    // 用户取消
  }
}
</script>

<style scoped lang="scss">
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
  
  .comment-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
    
    .comment-info {
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
    
    .comment-actions {
      display: flex;
      gap: 12px;
    }
  }
  
  .comment-content {
    font-size: 14px;
    line-height: 1.6;
    color: #333;
    margin-bottom: 12px;
    white-space: pre-wrap;
    word-break: break-word;
  }
  
  .replies {
    margin-left: 52px;
    padding-left: 16px;
    border-left: 2px solid #f0f0f0;
  }
}
</style>

