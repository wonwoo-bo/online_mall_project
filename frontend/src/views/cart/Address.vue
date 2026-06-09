<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="title">收货地址</h1>
      <div class="header-right">
        <span class="manage-btn" @click="isManageMode = !isManageMode">{{ isManageMode ? '完成' : '管理' }}</span>
        <span class="add-btn" @click="showAddModal">新增地址</span>
      </div>
    </div>

    <div class="address-list">
      <div
        class="address-item"
        v-for="item in addresses"
        :key="item.id"
        :class="{ selected: selectedId === item.id }"
        @click="selectAddress(item)"
      >
        <div class="address-left">
          <div class="address-main">
            <span class="address-name">{{ item.receiverName }}</span>
            <span class="address-phone">{{ item.receiverPhone }}</span>
          </div>
          <div class="address-detail">{{ item.province }}{{ item.city }}{{ item.district }}{{ item.detailAddress }}</div>
        </div>
        <div class="address-right">
          <span class="address-default" v-if="item.isDefault === 1">默认</span>
          <span class="edit-btn" @click.stop="showEditModal(item)">编辑</span>
        </div>
      </div>
    </div>

    <el-empty v-if="addresses.length === 0" description="暂无收货地址" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '新增地址'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="市">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区">
          <el-input v-model="form.district" placeholder="请输入区/县" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="form.detailAddress" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAddressList, addAddress, updateAddress, deleteAddress as deleteAddrApi } from '@/api/user'

const router = useRouter()
const route = useRoute()
const dialogVisible = ref(false)
const isEdit = ref(false)
const isManageMode = ref(false)
const selectedId = ref(null)
const addresses = ref([])
const form = ref({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', isDefault: 0 })
let editId = null

const goBack = () => {
  const query = { ...route.query }
  const returnUrl = query.return || 'checkout'
  delete query.return
  router.push({ path: `/${returnUrl}`, query })
}

const selectAddress = (item) => {
  selectedId.value = item.id
  sessionStorage.setItem('selectedAddress', `${item.receiverName} ${item.receiverPhone} ${item.province}${item.city}${item.district}${item.detailAddress}`)
  goBack()
}

const showAddModal = () => {
  isEdit.value = false
  form.value = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', isDefault: 0 }
  dialogVisible.value = true
}

const showEditModal = (item) => {
  isEdit.value = true
  editId = item.id
  form.value = {
    receiverName: item.receiverName,
    receiverPhone: item.receiverPhone,
    province: item.province,
    city: item.city,
    district: item.district,
    detailAddress: item.detailAddress,
    isDefault: item.isDefault || 0
  }
  dialogVisible.value = true
}

const saveAddress = async () => {
  if (!form.value.receiverName || !form.value.receiverPhone || !form.value.province || !form.value.city || !form.value.district || !form.value.detailAddress) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    if (isEdit.value) {
      await updateAddress(editId, form.value)
    } else {
      await addAddress(form.value)
    }
    dialogVisible.value = false
    ElMessage.success('保存成功')
    await loadAddresses()
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
    await deleteAddrApi(id)
    await loadAddresses()
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

const loadAddresses = async () => {
  try {
    const res = await getAddressList()
    if (res.code === 200) {
      addresses.value = res.data || []
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

onMounted(async () => {
  await loadAddresses()
  const selectedAddress = sessionStorage.getItem('selectedAddress')
  if (selectedAddress && addresses.value.length > 0) {
    const addr = addresses.value.find(a => selectedAddress.includes(a.receiverPhone))
    if (addr) selectedId.value = addr.id
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
}

.title {
  flex: 1;
  font-size: 22px;
  font-weight: bold;
  color: #ff6700;
}

.header-right {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.manage-btn {
  color: #666;
  cursor: pointer;
}

.add-btn {
  color: #ff6700;
  cursor: pointer;
}

.address-list {
  padding: 16px;
}

.address-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #ff6700;
}

.address-item.selected {
  border-color: #ff6700;
  background: #fff8f0;
}

.address-left {
  flex: 1;
}

.address-main {
  margin-bottom: 8px;
}

.address-name {
  font-weight: bold;
  font-size: 15px;
  margin-right: 12px;
  color: #333;
}

.address-phone {
  color: #666;
  font-size: 14px;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.address-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  margin-left: 12px;
}

.address-default {
  font-size: 12px;
  color: #ff6700;
  border: 1px solid #ff6700;
  border-radius: 3px;
  padding: 2px 6px;
  line-height: 1.2;
}

.edit-btn {
  font-size: 13px;
  color: #1890ff;
  cursor: pointer;
}

.edit-btn:hover {
  color: #40a9ff;
}
</style>
