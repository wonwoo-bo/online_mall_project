<template>
  <div class="config-page">
    <div class="page-header">
      <h2>系统配置</h2>
      <el-button type="primary" @click="showAddForm = true">添加配置</el-button>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane v-for="(list, tab) in configs" :key="tab" :label="tabLabels[tab]" :name="tab">
        <el-form :model="list" label-width="150px">
          <el-form-item v-for="config in list" :key="config.id" :label="config.configName">
            <div class="config-row">
              <!-- 有选项的配置用下拉框 -->
              <el-select
                v-if="selectOptions[config.configKey]"
                v-model="config.configValue"
                @change="handleConfigChange(config)"
                style="flex:1"
              >
                <el-option
                  v-for="opt in selectOptions[config.configKey]"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
              <!-- 没有选项的配置用输入框 -->
              <el-input
                v-else
                v-model="config.configValue"
                @blur="handleConfigChange(config)"
                :placeholder="config.description"
                style="flex:1"
              />
              <el-button type="danger" size="small" @click="handleDelete(config)" style="margin-left:8px">删除</el-button>
            </div>
            <span class="config-desc">{{ config.description }}</span>
          </el-form-item>
        </el-form>
        <el-empty v-if="list.length === 0" description="暂无配置" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="添加配置" v-model="showAddForm" width="400px">
      <el-form :model="newConfig" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="newConfig.configKey" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="newConfig.configValue" />
        </el-form-item>
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="newConfig.configName" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="newConfig.category">
            <el-option label="基础设置" value="basic" />
            <el-option label="商家设置" value="merchant" />
            <el-option label="订单设置" value="order" />
            <el-option label="会员设置" value="member" />
            <el-option label="上传设置" value="upload" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="newConfig.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddForm = false">取消</el-button>
        <el-button type="primary" @click="handleAddConfig">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConfigList, updateConfig, addConfig, deleteConfig } from '@/api'

const tabLabels = {
  basic: '基础设置',
  merchant: '商家设置',
  order: '订单设置',
  member: '会员设置',
  upload: '上传设置'
}

// 有固定选项的配置项
const selectOptions = {
  merchant_auto_approve: [
    { label: '是（自动通过）', value: 'true' },
    { label: '否（需人工审核）', value: 'false' }
  ],
  default_page_size: [
    { label: '5条/页', value: '5' },
    { label: '10条/页', value: '10' },
    { label: '20条/页', value: '20' },
    { label: '50条/页', value: '50' }
  ],
  order_auto_cancel_minutes: [
    { label: '15分钟', value: '15' },
    { label: '30分钟', value: '30' },
    { label: '60分钟', value: '60' },
    { label: '120分钟', value: '120' }
  ],
  order_auto_confirm_days: [
    { label: '7天', value: '7' },
    { label: '15天', value: '15' },
    { label: '30天', value: '30' }
  ],
  order_return_deadline_days: [
    { label: '3天', value: '3' },
    { label: '7天', value: '7' },
    { label: '15天', value: '15' },
    { label: '30天', value: '30' }
  ],
  points_per_yuan: [
    { label: '1积分/元', value: '1' },
    { label: '2积分/元', value: '2' },
    { label: '5积分/元', value: '5' },
    { label: '10积分/元', value: '10' }
  ],
  upload_max_size_mb: [
    { label: '1MB', value: '1' },
    { label: '2MB', value: '2' },
    { label: '5MB', value: '5' },
    { label: '10MB', value: '10' }
  ],
  merchant_settlement_cycle: [
    { label: '1天', value: '1' },
    { label: '3天', value: '3' },
    { label: '7天', value: '7' },
    { label: '15天', value: '15' },
    { label: '30天', value: '30' }
  ]
}

const activeTab = ref('basic')
const showAddForm = ref(false)
const addFormRef = ref()

const configs = reactive({
  basic: [],
  merchant: [],
  order: [],
  member: [],
  upload: []
})

const newConfig = reactive({
  configKey: '',
  configValue: '',
  configName: '',
  category: 'basic',
  description: ''
})

const addRules = {
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }]
}

const loadConfigs = async () => {
  try {
    const res = await getConfigList()
    if (res.code === 200) {
      const data = res.data
      configs.basic = data.filter(c => c.category === 'basic')
      configs.merchant = data.filter(c => c.category === 'merchant')
      configs.order = data.filter(c => c.category === 'order')
      configs.member = data.filter(c => c.category === 'member')
      configs.upload = data.filter(c => c.category === 'upload')
    }
  } catch (error) {
    console.error('获取配置失败:', error)
  }
}

onMounted(() => {
  loadConfigs()
})

const handleConfigChange = async (config) => {
  try {
    const res = await updateConfig(config.id, config.configValue)
    if (res.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleDelete = async (config) => {
  try {
    await ElMessageBox.confirm(`确定删除配置「${config.configName}」？`, '删除确认', { type: 'warning' })
    const res = await deleteConfig(config.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadConfigs()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAddConfig = async () => {
  try {
    await addFormRef.value.validate()
    const res = await addConfig(newConfig)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      showAddForm.value = false
      newConfig.configKey = ''
      newConfig.configValue = ''
      newConfig.configName = ''
      newConfig.category = 'basic'
      newConfig.description = ''
      loadConfigs()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}
</script>

<style scoped>
.config-page {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  margin: 0;
}

.config-row {
  display: flex;
  align-items: center;
  width: 100%;
}

.config-desc {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
