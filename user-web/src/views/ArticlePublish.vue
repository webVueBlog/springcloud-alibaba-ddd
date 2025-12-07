<template>
  <Layout>
    <div class="article-publish">
      <div class="publish-header">
        <h1>{{ $t('article.publish') }}</h1>
        <div class="actions">
          <el-button @click="$router.back()">{{ $t('common.cancel') }}</el-button>
          <el-button @click="handleSaveDraft" :loading="saving">{{ $t('article.draft') }}</el-button>
          <el-button type="primary" @click="handlePublish" :loading="publishing">
            {{ $t('article.publish') }}
          </el-button>
        </div>
      </div>

      <div class="publish-content">
        <el-form :model="form" label-width="100px" class="publish-form">
          <el-form-item label="标题" required>
            <el-input
              v-model="form.title"
              placeholder="请输入文章标题"
              size="large"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="摘要">
            <el-input
              v-model="form.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入文章摘要（可选）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="封面图">
            <el-input
              v-model="form.coverImage"
              placeholder="请输入封面图片URL（可选）"
            />
          </el-form-item>
          
          <el-form-item label="内容" required>
            <div class="editor-container">
              <el-tabs v-model="activeTab" class="editor-tabs">
                <el-tab-pane label="编辑" name="edit">
                  <el-input
                    v-model="form.content"
                    type="textarea"
                    :rows="20"
                    placeholder="请输入文章内容（支持Markdown）"
                    class="editor-textarea"
                  />
                </el-tab-pane>
                <el-tab-pane label="预览" name="preview">
                  <div class="preview-content" v-html="renderedContent"></div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-form-item>
          
          <el-form-item label="访问级别">
            <el-radio-group v-model="form.accessLevel">
              <el-radio label="PUBLIC">公开</el-radio>
              <el-radio label="PRIVATE">私有</el-radio>
              <el-radio label="PAID">付费可见</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import Layout from '@/components/Layout.vue'
import { articleApi } from '@/api/article'
import { useUserStore } from '@/store'

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()

const form = ref({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  contentType: 'MARKDOWN',
  accessLevel: 'PUBLIC'
})

const activeTab = ref('edit')
const publishing = ref(false)
const saving = ref(false)

const renderedContent = computed(() => {
  if (!form.value.content) return ''
  return marked(form.value.content)
})

const handlePublish = async () => {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  publishing.value = true
  try {
    const article = await articleApi.createArticle({
      ...form.value,
      userId: userStore.userInfo?.userId
    })
    await articleApi.publishArticle(article.id!)
    ElMessage.success('发布成功')
    router.push(`/article/${article.id}`)
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || '发布失败')
  } finally {
    publishing.value = false
  }
}

const handleSaveDraft = async () => {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  saving.value = true
  try {
    await articleApi.createArticle({
      ...form.value,
      userId: userStore.userInfo?.userId,
      publishStatus: 'DRAFT'
    })
    ElMessage.success('保存草稿成功')
    router.back()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
.article-publish {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .publish-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;

    h1 {
      font-size: 24px;
      font-weight: 700;
      color: #333;
      margin: 0;
    }

    .actions {
      display: flex;
      gap: 12px;
    }
  }

  .publish-content {
    .publish-form {
      .editor-container {
        border: 1px solid #e6e6e6;
        border-radius: 8px;
        overflow: hidden;

        .editor-tabs {
          :deep(.el-tabs__header) {
            margin: 0;
            border-bottom: 1px solid #e6e6e6;
          }

          .editor-textarea {
            :deep(.el-textarea__inner) {
              border: none;
              border-radius: 0;
              font-family: 'Courier New', monospace;
              font-size: 14px;
              line-height: 1.6;
            }
          }

          .preview-content {
            padding: 20px;
            min-height: 400px;
            line-height: 1.8;
            color: #333;

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
      }
    }
  }
}
</style>

