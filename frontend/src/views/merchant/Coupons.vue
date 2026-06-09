<template>
  <div class="coupon-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
          <el-button type="primary" @click="openDialog('add')">
            <el-icon><Plus /></el-icon>
            创建优惠券
          </el-button>
        </div>
      </template>

      <el-table :data="couponList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type === 1 ? '满减券' : '折扣券' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="面值" width="120">
          <template #default="{ row }">
            <span v-if="row.type === 1">¥{{ row.faceValue }}</span>
            <span v-else>{{ row.discountRate }}折</span>
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="使用门槛" width="120">
          <template #default="{ row }">
            满¥{{ row.minAmount || 0 }}可用
          </template>
        </el-table-column>
        <el-table-column label="发放/已领" width="120">
          <template #default="{ row }">
            {{ row.receivedCount || 0 }} / {{ row.totalCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="有效期" width="220">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }} 至 {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button type="primary" link size="small" @click="openDialog('edit', row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '创建优惠券' : '编辑优惠券'"
      width="600px"
      draggable
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">满减券</el-radio>
            <el-radio :label="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="面值" prop="faceValue" v-if="form.type === 1">
          <el-input-number v-model="form.faceValue" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="折扣" prop="discountRate" v-else>
          <el-input-number v-model="form.discountRate" :min="0.1" :max="9.9" :precision="1" />
          <span style="margin-left: 8px;">折</span>
        </el-form-item>
        <el-form-item label="使用门槛" prop="minAmount">
          <el-input-number v-model="form.minAmount" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元</span>
        </el-form-item>
        <el-form-item label="发放总量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="每人限领" prop="perUserLimit">
          <el-input-number v-model="form.perUserLimit" :min="1" />
        </el-form-item>
        <el-form-item label="有效期" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCouponList, createCoupon, updateCoupon, deleteCoupon, updateCouponStatus } from '@/api/merchant'

const loading = ref(false)
const couponList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  type: 1,
  faceValue: 10,
  discountRate: 9,
  minAmount: 0,
  totalCount: 100,
  perUserLimit: 1,
  dateRange: []
})

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择优惠类型', trigger: 'change' }],
  totalCount: [{ required: true, message: '请输入发放总量', trigger: 'blur' }]
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString()
}

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await getCouponList()
    if (res.code === 200) {
      couponList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

const openDialog = (type, row = null) => {
  dialogType.value = type
  if (type === 'edit' && row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      type: row.type,
      faceValue: row.faceValue || 10,
      discountRate: row.discountRate || 9,
      minAmount: row.minAmount || 0,
      totalCount: row.totalCount || 100,
      perUserLimit: row.perUserLimit || 1,
      dateRange: [row.startTime, row.endTime]
    })
  } else {
    Object.assign(form, {
      id: null,
      name: '',
      type: 1,
      faceValue: 10,
      discountRate: 9,
      minAmount: 0,
      totalCount: 100,
      perUserLimit: 1,
      dateRange: []
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        ...form,
        startTime: form.dateRange[0],
        endTime: form.dateRange[1]
      }
      delete data.dateRange

      const api = dialogType.value === 'add' ? createCoupon : updateCoupon
      const res = await api(form.id, data)

      if (res.code === 200) {
        ElMessage.success(dialogType.value === 'add' ? '创建成功' : '修改成功')
        dialogVisible.value = false
        loadCoupons()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = row.status === 1 ? '下架' : '发布'

  await ElMessageBox.confirm(`确定要${action}优惠券"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await updateCouponStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadCoupons()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除优惠券"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await deleteCoupon(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadCoupons()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
