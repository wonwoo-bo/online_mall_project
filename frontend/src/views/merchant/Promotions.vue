<template>
  <div class="promotion-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>促销活动</span>
          <el-button type="primary" @click="openDialog('add')">
            <el-icon><Plus /></el-icon>
            创建活动
          </el-button>
        </div>
      </template>

      <el-table :data="promotionList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="活动名称" min-width="150" />
        <el-table-column label="活动类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1">满减</el-tag>
            <el-tag v-else-if="row.type === 2">折扣</el-tag>
            <el-tag v-else type="info">包邮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="200">
          <template #default="{ row }">
            <span v-if="row.type === 1">
              满{{ row.minAmount }}减{{ row.reduceAmount }}
            </span>
            <span v-else-if="row.type === 2">
              {{ row.discountRate }}折
            </span>
            <span v-else>
              满{{ row.minAmount }}包邮
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="活动时间" width="220">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }} 至 {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog('edit', row)">
              编辑
            </el-button>
            <el-button type="primary" link size="small" @click="manageProducts(row)">
              管理商品
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
      :title="dialogType === 'add' ? '创建活动' : '编辑活动'"
      width="600px"
      draggable
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">满减</el-radio>
            <el-radio :label="2">折扣</el-radio>
            <el-radio :label="3">包邮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="满减门槛" prop="minAmount" v-if="form.type === 1 || form.type === 3">
          <el-input-number v-model="form.minAmount" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元</span>
        </el-form-item>
        <el-form-item label="减" prop="reduceAmount" v-if="form.type === 1">
          <el-input-number v-model="form.reduceAmount" :min="0.01" :precision="2" />
          <span style="margin-left: 8px;">元</span>
        </el-form-item>
        <el-form-item label="折扣" prop="discountRate" v-if="form.type === 2">
          <el-input-number v-model="form.discountRate" :min="0.1" :max="9.9" :precision="1" />
          <span style="margin-left: 8px;">折</span>
        </el-form-item>
        <el-form-item label="活动时间" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动描述"
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
import { getPromotionList, createPromotion, updatePromotion, deletePromotion } from '@/api/merchant'

const loading = ref(false)
const promotionList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  type: 1,
  minAmount: 100,
  reduceAmount: 10,
  discountRate: 9,
  description: '',
  dateRange: []
})

const rules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }]
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString()
}

const loadPromotions = async () => {
  loading.value = true
  try {
    const res = await getPromotionList()
    if (res.code === 200) {
      promotionList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载活动失败')
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
      minAmount: row.minAmount || 100,
      reduceAmount: row.reduceAmount || 10,
      discountRate: row.discountRate || 9,
      description: row.description || '',
      dateRange: [row.startTime, row.endTime]
    })
  } else {
    Object.assign(form, {
      id: null,
      name: '',
      type: 1,
      minAmount: 100,
      reduceAmount: 10,
      discountRate: 9,
      description: '',
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

      const api = dialogType.value === 'add' ? createPromotion : updatePromotion
      const res = await api(form.id, data)

      if (res.code === 200) {
        ElMessage.success(dialogType.value === 'add' ? '创建成功' : '修改成功')
        dialogVisible.value = false
        loadPromotions()
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

const manageProducts = (row) => {
  ElMessage.info('商品管理功能开发中')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除活动"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await deletePromotion(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadPromotions()
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
  loadPromotions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
