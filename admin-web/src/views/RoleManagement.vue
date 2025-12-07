<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table :data="roleList" border style="width: 100%">
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAssignPermission(row)">分配权限</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 角色编辑对话框 -->
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
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
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

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="{ label: 'permissionName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roleApi, type Role, type Permission } from '@/api/role'

const roleList = ref<Role[]>([])
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref()
const permissionTreeRef = ref()
const currentRoleId = ref<number>()
const permissionTree = ref<Permission[]>([])
const checkedPermissions = ref<number[]>([])

const form = ref<Role>({
  roleCode: '',
  roleName: '',
  status: 1,
  description: ''
})

const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const loadRoleList = async () => {
  try {
    const data = await roleApi.getRoleList()
    roleList.value = data
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  form.value = {
    roleCode: '',
    roleName: '',
    status: 1,
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: Role) => {
  dialogTitle.value = '编辑角色'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await roleApi.deleteRole(row.id!)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAssignPermission = async (row: Role) => {
  currentRoleId.value = row.id
  try {
    const data = await roleApi.getRolePermissionTree(row.id!)
    permissionTree.value = data.tree
    checkedPermissions.value = data.checked
    permissionDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载权限失败')
  }
}

const handleSubmitPermission = async () => {
  if (!permissionTreeRef.value) return
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  try {
    await roleApi.assignRolePermissions(currentRoleId.value!, checkedKeys)
    ElMessage.success('分配权限成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配权限失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.value.id) {
          await roleApi.updateRole(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await roleApi.createRole(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadRoleList()
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
  loadRoleList()
})
</script>

<style scoped lang="scss">
.role-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

