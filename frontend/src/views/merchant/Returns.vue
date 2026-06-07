<template>
  <div class="returns-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">退换货管理</span>
          <el-select v-model="queryParams.status" placeholder="处理状态" clearable @change="loadReturns">
            <el-option label="全部" value="" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="退货中" :value="3" />
            <el-option label="已完成" :value="4" />
          </el-select>
        </div>
      </template>

      <el-table :data="returnList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column label="退款金额" width="120">
          <template #default="{ row }">
            <strong>¥{{ row.refundAmount || row.amount }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <!-- 待审核：同意/拒绝 -->
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handleReturn(row, row.type === 1 ? 4 : 1)"
            >
              同意退款
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              size="small"
              @click="handleReturn(row, 2)"
            >
              拒绝
            </el-button>
            <!-- 退货中：确认完成 -->
            <el-button
              v-if="row.status === 3"
              type="primary"
              link
              size="small"
              @click="handleReturn(row, 4)"
            >
              确认完成
            </el-button>
            <el-button type="primary" link size="small" @click="viewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="退换货详情" width="600px" draggable>
      <el-descriptions :column="2" border v-if="currentReturn">
        <el-descriptions-item label="订单号">{{ currentReturn.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentReturn.productName }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">
          <strong>¥{{ currentReturn.refundAmount || currentReturn.amount }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="申请类型">
          {{ currentReturn.type === 1 ? '仅退款' : '退货退款' }}
        </el-descriptions-item>
        <el-descriptions-item label="申请原因" :span="2">{{ currentReturn.reason }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusType(currentReturn.status)">
            {{ getStatusText(currentReturn.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentReturn.createTime }}</el-descriptions-item>
        <el-descriptions-item label="物流公司" v-if="currentReturn.logisticsCompany">{{ currentReturn.logisticsCompany }}</el-descriptions-item>
        <el-descriptions-item label="物流单号" v-if="currentReturn.logisticsNo">{{ currentReturn.logisticsNo }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <!-- 待审核：同意/拒绝 -->
        <el-button v-if="currentReturn && currentReturn.status === 0" type="success" @click="handleReturn(currentReturn, currentReturn.type === 1 ? 4 : 1)">同意退款</el-button>
        <el-button v-if="currentReturn && currentReturn.status === 0" type="danger" @click="handleReturn(currentReturn, 2)">拒绝</el-button>
        <!-- 退货中：确认完成 -->
        <el-button v-if="currentReturn && currentReturn.status === 3" type="primary" @click="handleReturn(currentReturn, 4)">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReturnList, handleReturn as apiHandleReturn } from '@/api/merchant'
import { deductPoints } from '@/api'

const loading = ref(false)
const returnList = ref([])
const detailVisible = ref(false)
const currentReturn = ref(null)

const queryParams = reactive({
  status: ''
})

// 后端状态定义：0-待审核 1-已通过 2-已拒绝 3-退货中 4-已完成
const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '退货中', 4: '已完成' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'danger', 3: '', 4: 'success' }
  return map[status] || 'info'
}

const loadReturns = async () => {
  loading.value = true
  try {
    const res = await getReturnList(queryParams)
    if (res.code === 200) {
      returnList.value = res.data?.list || res.data || []
    }
  } catch (error) {
    ElMessage.error('加载退换货列表失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  currentReturn.value = row
  detailVisible.value = true
}

const handleReturn = async (row, status) => {
  const actionMap = { 1: '同意', 2: '拒绝', 4: '确认完成退换货' }
  const action = actionMap[status] || '处理'
  await ElMessageBox.confirm(`确定要${action}该退换货申请吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await apiHandleReturn(row.id, { status })
    if (res.code === 200) {
      // 退换货完成时扣减用户积分
      if (status === 4 && row.userId && row.refundAmount) {
        try {
          await deductPoints(row.userId, Math.round(row.refundAmount), '退换货完成，扣减订单积分')
        } catch (e) {
          console.error('积分扣减失败', e)
        }
      }
      ElMessage.success('处理成功')
      detailVisible.value = false
      loadReturns()
    } else {
      ElMessage.error(res.message || '处理失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('处理失败')
    }
  }
}

onMounted(() => {
  loadReturns()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  white-space: nowrap;
  font-size: 16px;
  font-weight: 500;
}
</style>
