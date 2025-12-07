<template>
  <div class="comment-form">
    <el-input
      v-model="content"
      type="textarea"
      :rows="3"
      :placeholder="placeholder"
      maxlength="500"
      show-word-limit
      @keydown.ctrl.enter="handleSubmit"
    />
    <div class="form-actions">
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        发表评论
      </el-button>
      <el-button v-if="showCancel" @click="handleCancel">取消</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  targetType: 'ARTICLE' | 'COMMENT'
  targetId: number
  parentId?: number
  placeholder?: string
  showCancel?: boolean
}>()

const emit = defineEmits<{
  submit: [content: string, parentId?: number]
  cancel: []
}>()

const content = ref('')
const submitting = ref(false)

const handleSubmit = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  submitting.value = true
  try {
    emit('submit', content.value.trim(), props.parentId)
    content.value = ''
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  content.value = ''
  emit('cancel')
}
</script>

<style scoped lang="scss">
.comment-form {
  margin-bottom: 24px;
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 12px;
  }
}
</style>

