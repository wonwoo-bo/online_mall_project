<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
        <!-- 个人信息 -->
        <el-tab-pane label="个人信息" name="info">
          <div class="info-content">
            <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="userForm.address" placeholder="请输入地址" type="textarea" :rows="3" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateUser" :loading="loading">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 会员信息 -->
        <el-tab-pane label="会员信息" name="member">
          <div class="member-content">
            <el-card v-if="memberInfo" class="member-card">
              <template #header>
                <div class="member-header">
                  <span>当前会员</span>
                  <el-tag :type="memberInfo.memberLevel === '非会员' ? 'default' : 'primary'" size="large">
                    {{ memberInfo.memberLevel || '非会员' }}
                  </el-tag>
                </div>
              </template>
              <div class="member-details">
                <div class="detail-item">
                  <span class="label">会员积分：</span>
                  <span class="value">{{ memberInfo.points || 0 }} 分</span>
                </div>
                <div class="detail-item" v-if="memberInfo.expireTime">
                  <span class="label">到期时间：</span>
                  <span class="value">{{ formatDate(memberInfo.expireTime) }}</span>
                </div>
                <div class="detail-item" v-else>
                  <span class="label">会员状态：</span>
                  <span class="value">暂未开通会员，请选择套餐开通</span>
                </div>
              </div>
            </el-card>

            <div class="member-types">
              <h3>选择会员套餐</h3>
              <div class="type-list">
                <el-card 
                  v-for="type in memberTypes" 
                  :key="type.id" 
                  class="type-card"
                  :class="{ 'selected': selectedType?.id === type.id }"
                  @click="selectedType = type"
                  shadow="hover"
                >
                  <div class="type-content">
                    <div class="type-name">{{ type.levelName }}</div>
                    <div class="type-price">¥{{ type.price }}</div>
                    <div class="type-duration">{{ type.durationDays }}天</div>
                    <div class="type-bonus">+{{ type.pointsBonus }}积分</div>
                    <div class="type-desc">{{ type.description }}</div>
                  </div>
                </el-card>
              </div>
              <el-button 
                type="primary" 
                size="large" 
                class="purchase-btn"
                :disabled="!selectedType"
                @click="handlePurchase"
                :loading="purchaseLoading"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 积分信息 -->
        <el-tab-pane label="积分信息" name="points">
          <div class="points-content">
            <el-card class="points-summary">
              <div class="summary-item">
                <div class="summary-label">当前积分</div>
                <div class="summary-value">{{ memberInfo?.points || 0 }}</div>
              </div>
              <div class="summary-divider"></div>
              <div class="summary-item">
                <div class="summary-label">获取方式</div>
                <div class="summary-desc">购物1元=1积分，购买会员额外赠积分</div>
              </div>
            </el-card>

            <div class="points-history">
              <h3>积分明细</h3>
              <el-table :data="pointsHistory" style="width: 100%">
                <el-table-column prop="points" label="积分变化" width="150">
                  <template #default="{ row }">
                    <span :class="{ 'positive': row.points > 0, 'negative': row.points < 0 }">
                      {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="type" label="类型" width="150">
                  <template #default="{ row }">
                    <el-tag :type="getPointTypeTag(row.type)" size="small">
                      {{ getPointTypeName(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="description" label="说明" />
                <el-table-column prop="createTime" label="时间" width="180">
                  <template #default="{ row }">
                    {{ formatDate(row.createTime) }}
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="pointsHistory.length === 0" description="暂无积分记录" />
            </div>
          </div>
        </el-tab-pane>

        <!-- 我的评价 -->
        <el-tab-pane label="我的评价" name="reviews">
          <div class="reviews-content">
            <div style="margin-bottom: 15px;">
              <el-button type="primary" @click="$router.push('/my/reviews')">查看全部评价</el-button>
            </div>
            <p style="color: #999;">点击上方按钮查看您发表的所有评价，包括追评和商家回复。</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, updateUserInfo, getMemberInfo, getMemberTypes, purchaseMember, getPointsHistory } from '@/api'

const activeTab = ref('info')
const loading = ref(false)
const purchaseLoading = ref(false)
const userFormRef = ref(null)
const userForm = ref({
  username: '',
  nickname: '',
  phone: '',
  address: ''
})
const memberInfo = ref(null)
const memberTypes = ref([])
const selectedType = ref(null)
const pointsHistory = ref([])

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 积分类型名称
const getPointTypeName = (type) => {
  const typeMap = {
    ADD: '积分增加',
    DEDUCT: '积分扣减',
    PURCHASE: '购物获得',
    MEMBER_BONUS: '会员赠送',
    ORDER_CANCEL: '订单取消',
    EXCHANGE: '积分兑换'
  }
  return typeMap[type] || type
}

// 积分类型标签
const getPointTypeTag = (type) => {
  const tagMap = {
    ADD: 'success',
    DEDUCT: 'danger',
    PURCHASE: 'success',
    MEMBER_BONUS: 'primary',
    ORDER_CANCEL: 'info',
    EXCHANGE: 'warning'
  }
  return tagMap[type] || 'info'
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      userForm.value = {
        username: res.data.username,
        nickname: res.data.nickname || '',
        phone: res.data.phone || '',
        address: res.data.address || ''
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 更新用户信息
const handleUpdateUser = async () => {
  if (!userFormRef.value) return
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await updateUserInfo({
          nickname: userForm.value.nickname,
          phone: userForm.value.phone,
          address: userForm.value.address
        })
        if (res.code === 200) {
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(res.message || '修改失败')
        }
      } catch (error) {
        console.error('更新用户信息失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 加载会员信息
const loadMemberInfo = async () => {
  try {
    const res = await getMemberInfo()
    if (res.code === 200) {
      memberInfo.value = res.data
    }
  } catch (error) {
    console.error('获取会员信息失败:', error)
  }
}

// 加载会员类型
const loadMemberTypes = async () => {
  try {
    const res = await getMemberTypes()
    if (res.code === 200) {
      memberTypes.value = res.data
    }
  } catch (error) {
    console.error('获取会员类型失败:', error)
  }
}

// 购买会员
const handlePurchase = async () => {
  if (!selectedType.value) return
  
  try {
    await ElMessageBox.confirm(
      `确认购买 ${selectedType.value.levelName} 会员吗？\n价格：¥${selectedType.value.price}`,
      '确认购买',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    purchaseLoading.value = true
    const res = await purchaseMember(selectedType.value.levelCode)
    if (res.code === 200) {
      ElMessage.success('购买成功')
      await loadMemberInfo()
      await loadPointsHistory()
      selectedType.value = null
    } else {
      ElMessage.error(res.message || '购买失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('购买会员失败:', error)
      ElMessage.error('购买失败，请重试')
    }
  } finally {
    purchaseLoading.value = false
  }
}

// 加载积分历史
const loadPointsHistory = async () => {
  try {
    const res = await getPointsHistory()
    if (res.code === 200) {
      pointsHistory.value = res.data || []
    }
  } catch (error) {
    console.error('获取积分历史失败:', error)
  }
}

// 切换标签时加载数据
const handleTabChange = (tabName) => {
  if (tabName === 'member') {
    loadMemberInfo()
    loadMemberTypes()
  } else if (tabName === 'points') {
    loadMemberInfo()
    loadPointsHistory()
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 8px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.info-content {
  padding: 20px;
  max-width: 600px;
}

.member-content {
  padding: 20px;
}

.member-card {
  margin-bottom: 30px;
}

.member-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.member-details {
  display: flex;
  gap: 40px;
}

.detail-item {
  display: flex;
  gap: 10px;
}

.detail-item .label {
  color: #666;
}

.detail-item .value {
  font-weight: bold;
  color: #333;
}

.member-types {
  margin-top: 30px;
}

.member-types h3 {
  margin-bottom: 20px;
  font-size: 16px;
}

.type-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.type-card {
  cursor: pointer;
  transition: all 0.3s;
}

.type-card.selected {
  border: 2px solid #409eff;
}

.type-content {
  text-align: center;
}

.type-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.type-price {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 8px;
}

.type-duration {
  color: #666;
  margin-bottom: 8px;
}

.type-bonus {
  color: #e6a23c;
  font-weight: bold;
  margin-bottom: 12px;
}

.type-desc {
  color: #999;
  font-size: 12px;
}

.purchase-btn {
  width: 200px;
  display: block;
  margin: 0 auto;
}

.points-content {
  padding: 20px;
}

.points-summary {
  margin-bottom: 30px;
}

.summary-item {
  flex: 1;
  text-align: center;
}

.summary-label {
  color: #666;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 36px;
  font-weight: bold;
  color: #e6a23c;
}

.summary-desc {
  color: #999;
  font-size: 12px;
}

.points-history h3 {
  margin-bottom: 20px;
  font-size: 16px;
}

.positive {
  color: #67c23a;
  font-weight: bold;
}

.negative {
  color: #f56c6c;
  font-weight: bold;
}

.points-summary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
}

.summary-divider {
  width: 1px;
  height: 80px;
  background: #eee;
}
</style>
