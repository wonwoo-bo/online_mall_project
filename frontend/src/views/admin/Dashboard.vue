<template>
  <div class="dashboard">
    <h1>仪表盘</h1>
    <div class="stats-cards">
      <div class="card">
        <div class="card-icon blue">
          <el-icon><Shop /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">商家总数</div>
          <div class="card-value">{{ merchantCount }}</div>
        </div>
      </div>
      <div class="card">
        <div class="card-icon orange">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">待审核商家</div>
          <div class="card-value">{{ pendingCount }}</div>
        </div>
      </div>
      <div class="card">
        <div class="card-icon green">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">已审核商家</div>
          <div class="card-value">{{ approvedCount }}</div>
        </div>
      </div>
      <div class="card">
        <div class="card-icon purple">
          <el-icon><Users /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">管理员数量</div>
          <div class="card-value">{{ adminCount }}</div>
        </div>
      </div>
    </div>
    <div class="recent-section">
      <h3>最近入驻商家</h3>
      <el-table :data="recentMerchants" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="shopName" label="店铺名称" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
              {{ scope.row.status === 1 ? '已通过' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入驻时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

import { getMerchantList, getAdminList } from '@/api'

const merchantCount = ref(0)
const pendingCount = ref(0)
const approvedCount = ref(0)
const adminCount = ref(0)
const recentMerchants = ref([])

onMounted(async () => {
  try {
    const merchantRes = await getMerchantList()
    if (merchantRes.code === 200) {
      merchantCount.value = merchantRes.data.length
      pendingCount.value = merchantRes.data.filter(m => m.status !== 1).length
      approvedCount.value = merchantRes.data.filter(m => m.status === 1).length
      recentMerchants.value = merchantRes.data.slice(0, 5)
    }
    
    const adminRes = await getAdminList()
    if (adminRes.code === 200) {
      adminCount.value = adminRes.data.length
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.card-icon.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.green {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-icon.purple {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 5px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.recent-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recent-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}
</style>