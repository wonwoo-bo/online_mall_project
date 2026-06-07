<template>
  <div class="merchant-list">
    <h2>待审核商家</h2>
    <el-table :data="merchants" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="shopName" label="店铺名称" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="contactPhone" label="联系电话" />
      <el-table-column prop="shopDesc" label="店铺描述" />
      <el-table-column prop="createTime" label="申请时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="success" size="small" @click="handleApprove(scope.row.id)">
            通过
          </el-button>
          <el-button type="danger" size="small" @click="handleReject(scope.row.id)">
            拒绝
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
    const res = await getMerchantList(0)
    if (res.code === 200) {
      merchants.value = res.data
      total.value = res.data.length
    }
  } catch (error) {
    console.error('获取商家列表失败:', error)
  }
}

onMounted(() => {
  loadMerchants()
})

const handleApprove = async (id) => {
  try {
    const res = await approveMerchant(id, 1)
    if (res.code === 200) {
      ElMessage.success('审核通过')
      loadMerchants()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (id) => {
  try {
    const res = await approveMerchant(id, 2)
    if (res.code === 200) {
      ElMessage.success('已拒绝')
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