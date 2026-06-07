<template>
  <div class="admin-list">
    <div class="page-header">
      <h2>管理员管理</h2>
      <el-button type="primary" @click="showAddForm = true">添加管理员</el-button>
    </div>
    
    <el-table :data="admins" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button 
            v-if="scope.row.status === 1" 
            type="warning" 
            size="small" 
            @click="handleToggleStatus(scope.row.id, 0)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            type="success" 
            size="small" 
            @click="handleToggleStatus(scope.row.id, 1)"
          >
            启用
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="添加管理员" v-model="showAddForm" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddForm = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="编辑管理员" v-model="showEditForm" width="400px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="新密码（不填则不变）">
          <el-input v-model="editForm.password" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditForm = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminList, addAdmin, deleteAdmin, updateAdminInfo, updateAdminPassword } from '@/api'

const admins = ref([])
const showAddForm = ref(false)
const showEditForm = ref(false)
const formRef = ref()
const editFormRef = ref()
const editingId = ref(null)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: ''
})

const editForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const editRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

const loadAdmins = async () => {
  try {
    const res = await getAdminList()
    if (res.code === 200) {
      admins.value = res.data
    }
  } catch (error) {
    console.error('获取管理员列表失败:', error)
  }
}

onMounted(() => {
  loadAdmins()
})

const handleAdd = async () => {
  try {
    await formRef.value.validate()
    const res = await addAdmin(form)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      showAddForm.value = false
      form.username = ''
      form.password = ''
      form.nickname = ''
      form.phone = ''
      loadAdmins()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleEdit = (admin) => {
  editingId.value = admin.id
  editForm.username = admin.username
  editForm.nickname = admin.nickname
  editForm.phone = admin.phone
  editForm.password = ''
  showEditForm.value = true
}

const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate()
    const updateData = {
      nickname: editForm.nickname,
      phone: editForm.phone
    }
    
    const res = await updateAdminInfo(updateData)
    if (res.code === 200) {
      if (editForm.password) {
        await updateAdminPassword({
          oldPassword: '',
          newPassword: editForm.password
        })
      }
      ElMessage.success('更新成功')
      showEditForm.value = false
      loadAdmins()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleToggleStatus = async (id, status) => {
  try {
    const admin = admins.value.find(a => a.id === id)
    admin.status = status
    const res = await updateAdminInfo({ nickname: admin.nickname, phone: admin.phone })
    if (res.code === 200) {
      ElMessage.success(status === 1 ? '已启用' : '已禁用')
      loadAdmins()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该管理员吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteAdmin(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadAdmins()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.admin-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  margin: 0;
}
</style>