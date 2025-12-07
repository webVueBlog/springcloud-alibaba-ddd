<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="handleAdd">新增菜单</el-button>
        </div>
      </template>

      <el-table
        :data="menuTree"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        style="width: 100%"
      >
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="menuCode" label="菜单编码" width="150" />
        <el-table-column prop="path" label="路由路径" width="200" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 菜单编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="父菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTree"
            :props="{ label: 'menuName', value: 'id' }"
            placeholder="请选择父菜单"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="菜单编码" prop="menuCode">
          <el-input v-model="form.menuCode" placeholder="请输入菜单编码" />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuApi, type Menu } from '@/api/menu'

const menuTree = ref<Menu[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref()
const form = ref<Menu>({
  menuCode: '',
  menuName: '',
  path: '',
  icon: '',
  sort: 0,
  status: 1,
  description: ''
})

const rules = {
  menuCode: [{ required: true, message: '请输入菜单编码', trigger: 'blur' }],
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }]
}

const loadMenuTree = async () => {
  try {
    const data = await menuApi.getMenuTree()
    menuTree.value = data
  } catch (error) {
    ElMessage.error('加载菜单失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增菜单'
  form.value = {
    menuCode: '',
    menuName: '',
    path: '',
    icon: '',
    sort: 0,
    status: 1,
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: Menu) => {
  dialogTitle.value = '编辑菜单'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Menu) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
      type: 'warning'
    })
    await menuApi.deleteMenu(row.id!)
    ElMessage.success('删除成功')
    loadMenuTree()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.value.id) {
          await menuApi.updateMenu(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await menuApi.createMenu(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadMenuTree()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const resetForm = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped lang="scss">
.menu-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

