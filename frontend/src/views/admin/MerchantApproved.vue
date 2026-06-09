<template>
  <div class="merchant-list">
    <h2>已审核商家</h2>
    <el-table :data="merchants" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="shopName" label="店铺名称" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="contactPhone" label="联系电话" />
      <el-table-column prop="shopDesc" label="店铺描述" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '已通过' : '已拒绝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" />
      <el-table-column prop="updateTime" label="审核时间" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button 
            v-if="scope.row.status === 1" 
            type="warning" 
            size="small" 
            @click="handleDisable(scope.row.id)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            type="success" 
            size="small" 
            @click="handleEnable(scope.row.id)"
          >
            启用
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="total > 10"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 20, 50]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantList, approveMerchant } from '@/api'

const merchants = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadMerchants = async () => {
  try {
    const res = await getMerchantList()
    if (res.code === 200) {
      merchants.value = res.data.filter(m => m.status !== 0)
      total.value = merchants.value.length
    }
  } catch (error) {
    console.error('获取商家列表失败:', error)
  }
}

onMounted(() => {
  loadMerchants()
})

const handleDisable = async (id) => {
  try {
    const res = await approveMerchant(id, 0)
    if (res.code === 200) {
      ElMessage.success('已禁用')
      loadMerchants()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleEnable = async (id) => {
  try {
    const res = await approveMerchant(id, 1)
    if (res.code === 200) {
      ElMessage.success('已启用')
      loadMerchants()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadMerchants()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadMerchants()
}
</script>

<style scoped>
.merchant-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 20px;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>