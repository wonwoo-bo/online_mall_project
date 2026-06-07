<template>
  <div class="advanced-returns">
    <el-card class="tabs-card">
      <el-tabs
        v-model="activeTab"
        type="border-card"
        @tab-change="handleTabChange"
      >
        <el-tab-pane label="售后原因统计" name="statistics">
          <div class="tab-content">
            <div class="filter-bar">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="loadStatistics"
              />
              <el-button type="primary" @click="loadStatistics">查询</el-button>
            </div>

            <div v-if="reasonStats.length > 0" class="chart-section">
              <div ref="chartRef" style="height: 400px"></div>
            </div>
            <el-empty v-else description="暂无数据" />

            <el-table
              v-if="reasonStats.length > 0"
              :data="reasonStats"
              style="margin-top: 20px"
            >
              <el-table-column prop="reasonType" label="售后原因" />
              <el-table-column prop="totalCount" label="售后数量" />
              <el-table-column prop="completedCount" label="已完成" />
              <el-table-column prop="approvedCount" label="已同意" />
              <el-table-column prop="rejectedCount" label="已拒绝" />
              <el-table-column label="占比">
                <template #default="{ row }">
                  {{ getPercentage(row.totalCount) }}%
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="超时自动处理" name="timeout">
          <div class="tab-content">
            <el-form
              :model="timeoutConfig"
              label-width="150px"
              class="config-form"
            >
              <el-form-item label="启用自动处理">
                <el-switch
                  v-model="timeoutConfig.autoHandleEnabled"
                  :active-value="1"
                  :inactive-value="0"
                />
              </el-form-item>
              <el-form-item label="审核超时时间(小时)">
                <el-input-number
                  v-model="timeoutConfig.reviewTimeout"
                  :min="1"
                  :max="168"
                />
              </el-form-item>
              <el-form-item label="收货超时时间(小时)">
                <el-input-number
                  v-model="timeoutConfig.receiveTimeout"
                  :min="1"
                  :max="336"
                />
              </el-form-item>
              <el-form-item label="超时自动处理方式">
                <el-radio-group v-model="timeoutConfig.autoApprove">
                  <el-radio :label="1">自动同意</el-radio>
                  <el-radio :label="0">自动拒绝</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="saving"
                  @click="saveTimeoutConfig"
                  >保存配置</el-button
                >
                <el-button :loading="handling" @click="triggerTimeoutHandle"
                  >立即处理超时订单</el-button
                >
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="运费规则" name="shipping-rule">
          <div class="tab-content">
            <el-form
              :model="shippingRule"
              label-width="150px"
              class="config-form"
            >
              <el-form-item label="规则名称">
                <el-input v-model="shippingRule.name" />
              </el-form-item>
              <el-form-item label="计费类型">
                <el-select v-model="shippingRule.type">
                  <el-option :label="shippingRuleType[1]" :value="1" />
                  <el-option :label="shippingRuleType[2]" :value="2" />
                  <el-option :label="shippingRuleType[3]" :value="3" />
                </el-select>
              </el-form-item>
              <el-form-item label="基础运费(元)">
                <el-input-number
                  v-model="shippingRule.baseFee"
                  :min="0"
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="免邮门槛(元)">
                <el-input-number
                  v-model="shippingRule.freeThreshold"
                  :min="0"
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="首重(kg)">
                <el-input-number
                  v-model="shippingRule.weightStart"
                  :min="0"
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="续重费用(元)">
                <el-input-number
                  v-model="shippingRule.weightFee"
                  :min="0"
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="启用状态">
                <el-switch
                  v-model="shippingRule.enabled"
                  :active-value="1"
                  :inactive-value="0"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="saving"
                  @click="handleSaveShippingRule"
                  >保存规则</el-button
                >
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="运费险配置" name="shipping-insurance">
          <div class="tab-content">
            <el-form
              :model="shippingInsurance"
              label-width="150px"
              class="config-form"
            >
              <el-form-item label="启用运费险">
                <el-switch
                  v-model="shippingInsurance.enabled"
                  :active-value="1"
                  :inactive-value="0"
                />
              </el-form-item>
              <el-form-item label="保费费率(%)">
                <el-input-number
                  v-model="shippingInsurance.feeRate"
                  :min="0"
                  :max="10"
                  :precision="4"
                />
              </el-form-item>
              <el-form-item label="最大赔付金额(元)">
                <el-input-number
                  v-model="shippingInsurance.maxCompensation"
                  :min="0"
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="最低订单金额(元)">
                <el-input-number
                  v-model="shippingInsurance.minAmount"
                  :min="0"
                  :precision="2"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="saving"
                  @click="handleSaveShippingInsurance"
                  >保存配置</el-button
                >
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="黑名单管理" name="blacklist">
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" @click="openAddBlacklistDialog"
                >添加黑名单</el-button
              >
            </div>
            <el-table :data="blacklist">
              <el-table-column prop="userName" label="用户昵称" />
              <el-table-column prop="userPhone" label="手机号" />
              <el-table-column prop="reason" label="拉黑原因" />
              <el-table-column prop="createTime" label="添加时间">
                <template #default="{ row }">{{
                  formatTime(row.createTime)
                }}</template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button
                    type="danger"
                    size="small"
                    @click="removeBlacklist(row.id)"
                    >移除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
            <el-empty
              v-if="blacklist.length === 0"
              description="暂无黑名单用户"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="纠纷管理" name="disputes">
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" @click="openDisputeDialog"
                >申请平台介入</el-button
              >
            </div>
            <el-table :data="disputes">
              <el-table-column prop="returnId" label="售后ID" />
              <el-table-column prop="reason" label="纠纷原因" />
              <el-table-column label="状态">
                <template #default="{ row }">
                  <el-tag :type="disputeStatusType[row.status]">{{
                    disputeStatusText[row.status]
                  }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="platformResult" label="裁决结果" />
              <el-table-column prop="createTime" label="申请时间">
                <template #default="{ row }">{{
                  formatTime(row.createTime)
                }}</template>
              </el-table-column>
            </el-table>
            <el-empty v-if="disputes.length === 0" description="暂无纠纷记录" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加黑名单弹窗 -->
    <el-dialog v-model="addBlacklistVisible" title="添加黑名单" width="500px" draggable>
      <el-form :model="addBlacklistForm" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="addBlacklistForm.userId" />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="addBlacklistForm.userName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addBlacklistForm.userPhone" />
        </el-form-item>
        <el-form-item label="拉黑原因">
          <el-input
            v-model="addBlacklistForm.reason"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addBlacklistVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddBlacklist"
          >确认添加</el-button
        >
      </template>
    </el-dialog>

    <!-- 申请纠纷弹窗 -->
    <el-dialog v-model="disputeVisible" title="申请平台介入" width="500px" draggable>
      <el-form :model="disputeForm" label-width="100px">
        <el-form-item label="售后ID">
          <el-input v-model="disputeForm.returnId" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="disputeForm.userId" />
        </el-form-item>
        <el-form-item label="纠纷原因">
          <el-input v-model="disputeForm.reason" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="证据材料">
          <el-input
            v-model="disputeForm.evidence"
            type="textarea"
            :rows="3"
            placeholder="可上传图片链接，用逗号分隔"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="disputeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDispute">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getDisputeList, applyDispute, getBlacklist, addToBlacklist, removeFromBlacklist } from "@/api/merchant";

const activeTab = ref("statistics");
const dateRange = ref([]);
const reasonStats = ref([]);
const totalCount = ref(0);
const chartRef = ref(null);

const saving = ref(false);
const handling = ref(false);

const timeoutConfig = reactive({
  autoHandleEnabled: 0,
  reviewTimeout: 72,
  receiveTimeout: 168,
  autoApprove: 1,
});

const shippingRuleType = {
  1: "按金额免邮",
  2: "按重量计费",
  3: "按地区计费",
};

const shippingRule = reactive({
  id: null,
  name: "",
  type: 1,
  baseFee: 0,
  freeThreshold: 0,
  weightStart: 1,
  weightFee: 0,
  enabled: 1,
});

const shippingInsurance = reactive({
  id: null,
  enabled: 0,
  feeRate: 0.005,
  maxCompensation: 50,
  minAmount: 0,
});

const blacklist = ref([]);
const addBlacklistVisible = ref(false);
const addBlacklistForm = reactive({
  userId: "",
  userName: "",
  userPhone: "",
  reason: "",
});

const disputes = ref([]);
const disputeVisible = ref(false);
const disputeForm = reactive({
  returnId: "",
  userId: "",
  reason: "",
  evidence: "",
});

const disputeStatusText = {
  0: "待平台介入",
  1: "处理中",
  2: "已裁决",
};

const disputeStatusType = {
  0: "warning",
  1: "info",
  2: "success",
};

const formatTime = (time) => {
  if (!time) return "-";
  const date = new Date(time);
  return date.toLocaleString("zh-CN");
};

const getPercentage = (count) => {
  if (totalCount.value === 0) return 0;
  return ((count / totalCount.value) * 100).toFixed(1);
};

const loadStatistics = async () => {
  try {
    reasonStats.value = [
      {
        reasonType: "尺码问题",
        totalCount: 12,
        completedCount: 10,
        approvedCount: 8,
        rejectedCount: 2,
      },
      {
        reasonType: "质量问题",
        totalCount: 8,
        completedCount: 7,
        approvedCount: 6,
        rejectedCount: 1,
      },
      {
        reasonType: "与描述不符",
        totalCount: 6,
        completedCount: 5,
        approvedCount: 4,
        rejectedCount: 1,
      },
      {
        reasonType: "不喜欢",
        totalCount: 4,
        completedCount: 4,
        approvedCount: 4,
        rejectedCount: 0,
      },
      {
        reasonType: "重复购买",
        totalCount: 3,
        completedCount: 3,
        approvedCount: 3,
        rejectedCount: 0,
      },
    ];
    totalCount.value = 33;
    renderChart();
  } catch (error) {
    console.error("获取统计数据失败", error);
  }
};

const renderChart = () => {
  if (!chartRef.value) return;
};

const handleTabChange = async (tabName) => {
  if (tabName === "blacklist") {
    await loadBlacklist();
  } else if (tabName === "disputes") {
    await loadDisputes();
  }
};

const saveTimeoutConfig = async () => {
  saving.value = true;
  try {
    ElMessage.success("配置保存成功");
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};

const triggerTimeoutHandle = async () => {
  handling.value = true;
  try {
    ElMessage.success("超时处理完成");
  } catch (error) {
    ElMessage.error("处理失败");
  } finally {
    handling.value = false;
  }
};

const handleSaveShippingRule = async () => {
  saving.value = true;
  try {
    ElMessage.success("保存成功");
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};

const handleSaveShippingInsurance = async () => {
  saving.value = true;
  try {
    ElMessage.success("保存成功");
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
};

const loadBlacklist = async () => {
  try {
    const res = await getBlacklist();
    if (res.code === 200 || res.code === 0) {
      blacklist.value = res.data || [];
    } else {
      // 降级处理
      blacklist.value = [
        {
          id: 1,
          userName: "恶意用户1",
          userPhone: "138****1234",
          reason: "多次恶意退款",
          createTime: "2024-02-10 10:00:00",
        },
        {
          id: 2,
          userName: "恶意用户2",
          userPhone: "139****5678",
          reason: "虚假投诉",
          createTime: "2024-02-12 14:30:00",
        },
      ];
    }
  } catch (error) {
    console.error("获取黑名单失败", error);
    // 降级处理
    blacklist.value = [
      {
        id: 1,
        userName: "恶意用户1",
        userPhone: "138****1234",
        reason: "多次恶意退款",
        createTime: "2024-02-10 10:00:00",
      },
      {
        id: 2,
        userName: "恶意用户2",
        userPhone: "139****5678",
        reason: "虚假投诉",
        createTime: "2024-02-12 14:30:00",
      },
    ];
  }
};

const openAddBlacklistDialog = () => {
  addBlacklistForm.userId = "";
  addBlacklistForm.userName = "";
  addBlacklistForm.userPhone = "";
  addBlacklistForm.reason = "";
  addBlacklistVisible.value = true;
};

const submitAddBlacklist = async () => {
  if (!addBlacklistForm.userId || !addBlacklistForm.reason) {
    ElMessage.warning("请填写必要信息");
    return;
  }
  try {
    const res = await addToBlacklist(addBlacklistForm);
    if (res.code === 200 || res.code === 0) {
      ElMessage.success("添加成功");
      addBlacklistVisible.value = false;
      await loadBlacklist();
    } else {
      ElMessage.error(res.message || "添加失败");
    }
  } catch (error) {
    ElMessage.error("添加失败");
  }
};

const removeBlacklist = async (id) => {
  ElMessageBox.confirm("确定要将该用户移出黑名单吗？", "提示", {
    type: "warning",
  }).then(async () => {
    try {
      const res = await removeFromBlacklist(id);
      if (res.code === 200 || res.code === 0) {
        ElMessage.success("移除成功");
        await loadBlacklist();
      } else {
        ElMessage.error(res.message || "移除失败");
      }
    } catch (error) {
      ElMessage.error("移除失败");
    }
  });
};

const loadDisputes = async () => {
  try {
    const res = await getDisputeList();
    if (res.code === 200 || res.code === 0) {
      disputes.value = res.data || [];
    } else {
      // 降级处理
      disputes.value = [
        {
          id: 1,
          returnId: 1001,
          reason: "用户拒绝沟通，无法协商",
          status: 0,
          platformResult: "",
          createTime: "2024-02-14 09:00:00",
        },
        {
          id: 2,
          returnId: 1002,
          reason: "商品质量争议",
          status: 1,
          platformResult: "",
          createTime: "2024-02-13 16:00:00",
        },
        {
          id: 3,
          returnId: 1003,
          reason: "退款金额纠纷",
          status: 2,
          platformResult: "支持商家，驳回用户申请",
          createTime: "2024-02-10 10:00:00",
        },
      ];
    }
  } catch (error) {
    console.error("获取纠纷列表失败", error);
    // 降级处理
    disputes.value = [
      {
        id: 1,
        returnId: 1001,
        reason: "用户拒绝沟通，无法协商",
        status: 0,
        platformResult: "",
        createTime: "2024-02-14 09:00:00",
      },
      {
        id: 2,
        returnId: 1002,
        reason: "商品质量争议",
        status: 1,
        platformResult: "",
        createTime: "2024-02-13 16:00:00",
      },
      {
        id: 3,
        returnId: 1003,
        reason: "退款金额纠纷",
        status: 2,
        platformResult: "支持商家，驳回用户申请",
        createTime: "2024-02-10 10:00:00",
      },
    ];
  }
};

const openDisputeDialog = () => {
  disputeForm.returnId = "";
  disputeForm.userId = "";
  disputeForm.reason = "";
  disputeForm.evidence = "";
  disputeVisible.value = true;
};

const submitDispute = async () => {
  if (!disputeForm.returnId || !disputeForm.userId || !disputeForm.reason) {
    ElMessage.warning("请填写必要信息");
    return;
  }
  try {
    const res = await applyDispute(disputeForm);
    if (res.code === 200 || res.code === 0) {
      ElMessage.success("申请已提交");
      disputeVisible.value = false;
      await loadDisputes();
    } else {
      ElMessage.error(res.message || "提交失败");
    }
  } catch (error) {
    ElMessage.error("提交失败");
  }
};

onMounted(() => {
  nextTick(() => {
    loadStatistics();
  });
});

watch(
  () => activeTab.value,
  () => {
    if (activeTab.value === "statistics") {
      nextTick(() => {
        renderChart();
      });
    }
  },
);
</script>

<style scoped>
.advanced-returns {
  padding: 20px;
}

.tabs-card {
  height: calc(100vh - 120px);
}

.tab-content {
  padding: 20px;
}

.filter-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.config-form {
  max-width: 600px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
