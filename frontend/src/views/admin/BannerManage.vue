<template>
  <div class="banner-manage">
    <div class="page-header">
      <h3>首页轮播图管理</h3>
      <el-button type="primary" @click="showAdd">添加轮播图</el-button>
    </div>

    <el-table :data="banners" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="预览" width="120">
        <template #default="{ row }">
          <div class="banner-preview" :style="{ background: row.color || '#FF5000' }">
            <span>{{ row.title?.charAt(0) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="imageUrl" label="图片URL" width="180" show-overflow-tooltip />
      <el-table-column prop="linkUrl" label="跳转链接" width="150" show-overflow-tooltip />
      <el-table-column label="背景色" width="80">
        <template #default="{ row }">
          <div class="color-block" :style="{ background: row.color || '#FF5000' }"></div>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '添加轮播图'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="轮播图标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="轮播图描述" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="图片地址（可选）" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="点击后跳转的链接（可选）" />
        </el-form-item>
        <el-form-item label="背景色">
          <el-color-picker v-model="form.color" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const banners = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  title: '',
  description: '',
  imageUrl: '',
  linkUrl: '',
  color: '#FF5000',
  sortOrder: 0,
  status: 1
})

const loadBanners = async () => {
  const res = await request.get('/admin/banners')
  if (res.code === 200) {
    banners.value = res.data || []
  }
}

const showAdd = () => {
  isEdit.value = false
  form.value = { title: '', description: '', imageUrl: '', linkUrl: '', color: '#FF5000', sortOrder: 0, status: 1 }
  dialogVisible.value = true
}

const showEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.title) {
    ElMessage.warning('请输入标题')
    return
  }
  if (isEdit.value) {
    await request.put(`/admin/banners/${form.value.id}`, form.value)
    ElMessage.success('更新成功')
  } else {
    await request.post('/admin/banners', form.value)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadBanners()
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await request.put(`/admin/banners/${row.id}`, { status: newStatus })
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadBanners()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该轮播图？', '提示', { type: 'warning' })
  await request.delete(`/admin/banners/${row.id}`)
  ElMessage.success('删除成功')
  loadBanners()
}

onMounted(() => {
  loadBanners()
})
</script>

<style scoped>
.banner-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
}

.banner-preview {
  width: 60px;
  height: 40px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}

.color-block {
  width: 30px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #ddd;
}
</style>
