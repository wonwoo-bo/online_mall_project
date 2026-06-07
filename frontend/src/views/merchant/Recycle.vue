<template>
  <div class="recycle-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>回收站</span>
          <div class="header-actions">
            <el-radio-group v-model="activeTab" @change="handleTabChange">
              <el-radio-button label="product">商品</el-radio-button>
              <el-radio-button label="category">分类</el-radio-button>
              <el-radio-button label="brand">品牌</el-radio-button>
              <el-radio-button label="spec">规格</el-radio-button>
            </el-radio-group>
            <el-button @click="loadRecycle">刷新</el-button>
            <el-button type="danger" @click="clearRecycle" :disabled="recycleList.length === 0">
              清空当前类型
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="recycleList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column v-if="activeTab === 'product'" prop="name" label="商品名称" min-width="200" />
        <el-table-column v-if="activeTab === 'product'" prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column v-if="activeTab === 'product'" prop="stock" label="库存" width="100" />
        <el-table-column v-if="activeTab === 'category'" prop="name" label="分类名称" min-width="200" />
        <el-table-column v-if="activeTab === 'category'" prop="level" label="层级" width="80" />
        <el-table-column v-if="activeTab === 'brand'" prop="name" label="品牌名称" min-width="200" />
        <el-table-column v-if="activeTab === 'brand'" prop="logo" label="Logo" width="100">
          <template #default="{ row }">
            <el-image v-if="row.logo" :src="row.logo" style="width: 60px; height: 60px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column v-if="activeTab === 'spec'" prop="name" label="规格名称" min-width="200" />
        <el-table-column v-if="activeTab === 'spec'" prop="typeName" label="规格类型" width="100" />
        <el-table-column prop="deleteTime" label="删除时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="success" link size="small" @click="restore(row)">
              恢复
            </el-button>
            <el-button type="danger" link size="small" @click="remove(row)">
              彻底删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && recycleList.length === 0" description="回收站为空" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRecycleList, 
  restoreItem, 
  deleteFromRecycle
} from '@/api/merchant'

const route = useRoute()
const loading = ref(false)
const activeTab = ref('product')
const recycleList = ref([])

const handleTabChange = () => {
  loadRecycle()
}

const loadRecycle = async () => {
  loading.value = true
  try {
    const response = await getRecycleList(activeTab.value)
    if (response.code === 200) {
      recycleList.value = response.data?.list || []
    }
  } catch (error) {
    ElMessage.error('加载回收站失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const type = route.query.type
  if (type) {
    activeTab.value = type
  }
  loadRecycle()
})

const restore = async (row) => {
  const typeNames = {
    product: '商品',
    category: '分类',
    brand: '品牌',
    spec: '规格'
  }
  
  const typeName = typeNames[activeTab.value] || '项目'
  const itemName = row.name || row.title || row.productName || '未知'
  
  await ElMessageBox.confirm(`确定要恢复${typeName}"${itemName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await restoreItem(activeTab.value, row.id)
    if (res.code === 200) {
      ElMessage.success('恢复成功')
      loadRecycle()
    } else {
      ElMessage.error(res.message || '恢复失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败')
    }
  }
}

const remove = async (row) => {
  const typeNames = {
    product: '商品',
    category: '分类',
    brand: '品牌',
    spec: '规格'
  }
  
  const typeName = typeNames[activeTab.value] || '项目'
  const itemName = row.name || row.title || row.productName || '未知'

  await ElMessageBox.confirm(`彻底删除后无法恢复，确定要删除${typeName}"${itemName}"吗？`, '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await deleteFromRecycle(activeTab.value, row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadRecycle()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const clearRecycle = async () => {
  const typeNames = {
    product: '商品',
    category: '分类',
    brand: '品牌',
    spec: '规格'
  }
  
  const typeName = typeNames[activeTab.value] || '项目'
  
  await ElMessageBox.confirm(`确定要清空${typeName}回收站吗？此操作不可恢复！`, '警告', {
    confirmButtonText: '确定清空',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    // 暂时不支持清空功能，需要后端实现
    ElMessage.warning('清空功能暂未实现')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}
</style>
