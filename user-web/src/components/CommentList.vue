<template>
  <div class="comment-list">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="comments.length === 0" class="empty">
      <el-empty description="暂无评论" :image-size="100" />
    </div>
    
    <div v-else class="comments">
      <comment-item
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :current-user-id="currentUserId"
        @reply="handleReply"
        @like="handleLike"
        @delete="handleDelete"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CommentItem from './CommentItem.vue'
import { commentApi, type Comment } from '@/api/comment'

const props = defineProps<{
  targetType: 'ARTICLE' | 'COMMENT'
  targetId: number
  currentUserId?: number
}>()

const emit = defineEmits<{
  refresh: []
  reply: [comment: Comment, parentComment?: Comment]
}>()

const comments = ref<Comment[]>([])
const loading = ref(false)

const loadComments = async () => {
  loading.value = true
  try {
    const data = await commentApi.getCommentList(props.targetType, props.targetId)
    comments.value = (data as Comment[]) || []
  } catch (error: any) {
    if (error?.response?.status !== 404) {
      console.error('加载评论失败:', error)
    }
    comments.value = []
  } finally {
    loading.value = false
  }
}

const handleReply = (comment: Comment, parentComment?: Comment) => {
  emit('reply', comment, parentComment)
}

const handleLike = async (comment: Comment) => {
  if (!comment.id) return
  try {
    await commentApi.likeComment(comment.id)
    ElMessage.success('点赞成功')
    loadComments()
  } catch (error) {
    ElMessage.error('点赞失败')
  }
}

const handleDelete = async (comment: Comment) => {
  if (!comment.id) return
  try {
    await commentApi.deleteComment(comment.id)
    ElMessage.success('删除成功')
    loadComments()
    emit('refresh')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

watch(() => [props.targetType, props.targetId], () => {
  loadComments()
}, { immediate: true })

onMounted(() => {
  loadComments()
})

defineExpose({
  refresh: loadComments
})
</script>

<style scoped lang="scss">
.comment-list {
  .loading {
    padding: 20px;
  }
  
  .empty {
    padding: 40px 0;
  }
  
  .comments {
    // 评论列表样式
  }
}
</style>

