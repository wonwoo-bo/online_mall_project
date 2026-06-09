<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.productCount }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ stats.todaySales }}</div>
              <div class="stat-label">总销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingReturns }}</div>
              <div class="stat-label">待处理退换货</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待处理订单</span>
              <el-button type="primary" link @click="goToOrders"
                >查看全部</el-button
              >
            </div>
          </template>
          <el-table :data="pendingOrders" style="width: 100%" :fit="true">
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="userName" label="买家" />
            <el-table-column prop="totalAmount" label="金额">
              <template #default="{ row }"> ¥{{ row.totalAmount }} </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待发货</el-tag>
                <el-tag v-else-if="row.status === 1" type="info">待收货</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="handleShip(row)"
                >
                  {{ row.status === 0 ? "发货" : "查看" }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
              <template v-if="!isEditing">
                <el-button type="text" class="edit-btn" @click="startEdit">编辑</el-button>
              </template>
              <template v-else>
                <el-button type="text" class="edit-btn" @click="cancelEdit">取消</el-button>
                <el-button type="primary" size="small" @click="saveActions">保存</el-button>
              </template>
            </div>
          </template>
          
          <template v-if="!isEditing">
            <div class="quick-actions">
              <div v-for="action in quickActions" :key="action.id" class="action-btn-wrapper">
                <el-button :type="action.type" @click="handleAction(action)">
                  <el-icon><component :is="getIcon(action.icon)" /></el-icon>
                  {{ action.label }}
                </el-button>
              </div>
            </div>
          </template>
          
          <template v-else>
            <div class="quick-actions-edit">
              <div v-for="(action, index) in quickActions" :key="action.id" class="action-item">
                <div class="action-content">
                  <el-icon class="drag-icon"><Menu /></el-icon>
                  <el-button :type="action.type" disabled>
                    <el-icon><component :is="getIcon(action.icon)" /></el-icon>
                    {{ action.label }}
                  </el-button>
                </div>
                <div class="action-actions">
                  <el-button type="text" size="small" @click="moveUp(index)" :disabled="index === 0">
                    <el-icon><ArrowUp /></el-icon>
                  </el-button>
                  <el-button type="text" size="small" @click="moveDown(index)" :disabled="index === quickActions.length - 1">
                    <el-icon><ArrowDown /></el-icon>
                  </el-button>
                  <el-button type="text" size="small" @click="removeAction(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <div class="add-action">
              <el-button type="dashed" block @click="showAddModal = true">
                <el-icon><Plus /></el-icon>
                添加快捷操作
              </el-button>
            </div>
            <div class="save-actions">
              <el-button type="primary" @click="saveActions">保存配置</el-button>
            </div>
          </template>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <div class="notice-list">
            <div class="notice-item">
              <el-icon><Bell /></el-icon>
              <span>平台将于本周日凌晨进行系统维护</span>
            </div>
            <div class="notice-item">
              <el-icon><Bell /></el-icon>
              <span>新功能上线：优惠券已支持满减叠加</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog title="添加快捷操作" v-model="showAddModal" width="400px" draggable>
      <el-form :model="newAction" label-width="80px">
        <el-form-item label="操作名称">
          <el-input v-model="newAction.label" placeholder="请输入操作名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="newAction.icon" placeholder="请选择图标">
              <el-option label="Plus" value="Plus" />
              <el-option label="List" value="List" />
              <el-option label="Refresh" value="Refresh" />
              <el-option label="TrendCharts" value="TrendCharts" />
              <el-option label="Goods" value="Goods" />
              <el-option label="Money" value="Money" />
              <el-option label="Settings" value="Settings" />
              <el-option label="Users" value="Users" />
              <el-option label="ShoppingCart" value="ShoppingCart" />
              <el-option label="Document" value="Document" />
              <el-option label="PieChart" value="PieChart" />
            </el-select>
        </el-form-item>
        <el-form-item label="按钮类型">
          <el-select v-model="newAction.type" placeholder="请选择按钮类型">
            <el-option label="primary" value="primary" />
            <el-option label="success" value="success" />
            <el-option label="warning" value="warning" />
            <el-option label="info" value="info" />
            <el-option label="danger" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="跳转路径">
          <el-input v-model="newAction.path" placeholder="请输入路由路径" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="addAction">确定添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDashboardData } from '@/api/merchant'
import { 
  Goods, List, Money, Warning, Plus, Refresh, TrendCharts, Bell, 
  Menu, User, ShoppingCart, ArrowUp, ArrowDown, Delete, Document, Setting, PieChart
} from '@element-plus/icons-vue'

const router = useRouter()

const stats = reactive({
  productCount: 0,
  orderCount: 0,
  todaySales: '0.00',
  pendingReturns: 0
})

const pendingOrders = ref([])
const isEditing = ref(false)
const showAddModal = ref(false)

const newAction = reactive({
  label: '',
  icon: 'Plus',
  type: 'primary',
  path: ''
})

const defaultActions = [
  { id: 1, label: '添加商品', icon: 'Plus', type: 'primary', path: '/merchant/products/add' },
  { id: 2, label: '处理订单', icon: 'List', type: 'success', path: '/merchant/orders' },
  { id: 3, label: '退换货', icon: 'Refresh', type: 'warning', path: '/merchant/returns' },
  { id: 4, label: '查看统计', icon: 'TrendCharts', type: 'info', path: '/merchant/statistics' }
]

const quickActions = ref([])

const iconMap = {
  Plus, List, Refresh, TrendCharts, Goods, Money, Menu, User, 
  ShoppingCart, Document, PieChart, Setting
}

const getIcon = (iconName) => {
  return iconMap[iconName] || Plus
}

const loadActions = () => {
  const saved = localStorage.getItem('merchantQuickActions')
  if (saved) {
    try {
      quickActions.value = JSON.parse(saved)
    } catch {
      quickActions.value = [...defaultActions]
    }
  } else {
    quickActions.value = [...defaultActions]
  }
}

const saveActions = () => {
  localStorage.setItem('merchantQuickActions', JSON.stringify(quickActions.value))
  isEditing.value = false
  ElMessage.success('快捷操作配置已保存')
}

const startEdit = () => {
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
  loadActions()
}

const moveUp = (index) => {
  if (index > 0) {
    const temp = quickActions.value[index]
    quickActions.value[index] = quickActions.value[index - 1]
    quickActions.value[index - 1] = temp
  }
}

const moveDown = (index) => {
  if (index < quickActions.value.length - 1) {
    const temp = quickActions.value[index]
    quickActions.value[index] = quickActions.value[index + 1]
    quickActions.value[index + 1] = temp
  }
}

const removeAction = (index) => {
  quickActions.value.splice(index, 1)
}

const addAction = () => {
  if (!newAction.label || !newAction.path) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const newId = Math.max(...quickActions.value.map(a => a.id), 0) + 1
  quickActions.value.push({
    id: newId,
    label: newAction.label,
    icon: newAction.icon,
    type: newAction.type,
    path: newAction.path
  })
  
  newAction.label = ''
  newAction.icon = 'Plus'
  newAction.type = 'primary'
  newAction.path = ''
  showAddModal.value = false
}

const handleAction = (action) => {
  router.push(action.path)
}

const loadDashboard = async () => {
  try {
    const res = await getDashboardData()
    if (res.code === 200) {
      Object.assign(stats, res.data || {})
      pendingOrders.value = res.data?.pendingOrders || []
    }
  } catch (error) {
    console.error('加载数据失败', error)
    stats.productCount = 0
    stats.orderCount = 0
    stats.todaySales = '0.00'
    stats.pendingReturns = 0
  }
}

const goToOrders = () => router.push('/merchant/orders')
const goToAddProduct = () => router.push('/merchant/products/add')
const goToReturns = () => router.push('/merchant/returns')
const goToStatistics = () => router.push('/merchant/statistics')

const handleShip = (order) => {
  ElMessage.success(`订单 ${order.orderNo} 已发货`)
  loadDashboard()
}

onMounted(() => {
  loadDashboard()
  loadActions()
})
</script>

<style scoped>
.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.edit-btn {
  color: #409eff;
  font-size: 13px;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.action-btn-wrapper {
  width: 100%;
}

.action-btn-wrapper .el-button {
  width: 100%;
  justify-content: center;
}

.quick-actions-edit {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  background: #fafafa;
  border-radius: 6px;
}

.action-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.drag-icon {
  cursor: move;
  color: #999;
  font-size: 16px;
}

.action-actions {
  display: flex;
  gap: 4px;
}

.action-actions .el-button {
  padding: 4px;
}

.add-action {
  margin-top: 8px;
}

.save-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}
</style>