<template>
  <div class="settings">
    <el-card class="tabs-card">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 商家个人信息 -->
        <el-tab-pane label="个人信息" name="personal">
          <div class="tab-content">
            <el-form :model="personalForm" label-width="150px" class="form-container">
              <el-form-item label="账号头像">
                <el-upload
                  class="upload-demo"
                  :action="uploadAction"
                  :show-file-list="false"
                  :on-success="handleAvatarUpload"
                >
                  <img
                    v-if="personalForm.avatar"
                    :src="personalForm.avatar"
                    class="avatar-preview"
                  />
                  <el-button v-else type="primary" icon="Plus">上传头像</el-button>
                </el-upload>
              </el-form-item>
              <el-form-item label="绑定手机号">
                <el-input v-model="personalForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="个人简介">
                <el-input
                  v-model="personalForm.intro"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入个人简介"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingPersonal"
                  @click="savePersonalInfo"
                >
                  保存个人信息
                </el-button>
              </el-form-item>
            </el-form>

            <div class="password-section">
              <h3>修改密码</h3>
              <el-form :model="passwordForm" label-width="150px" class="form-container">
                <el-form-item label="当前密码">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认密码">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="changingPassword"
                    @click="changePassword"
                  >
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>

        <!-- 店铺基础信息 -->
        <el-tab-pane label="店铺信息" name="shop-info">
          <div class="tab-content">
            <el-form :model="shopForm" label-width="150px" class="form-container">
              <el-form-item label="店铺名称">
                <el-input v-model="shopForm.shopName" placeholder="请输入店铺名称" />
              </el-form-item>
              <el-form-item label="店铺Logo">
                <el-upload
                  class="upload-demo"
                  :action="uploadAction"
                  :show-file-list="false"
                  :on-success="handleLogoUpload"
                >
                  <img
                    v-if="shopForm.shopLogo"
                    :src="shopForm.shopLogo"
                    class="logo-preview"
                  />
                  <el-button v-else type="primary" icon="Plus">上传Logo</el-button>
                </el-upload>
              </el-form-item>
              <el-form-item label="店铺简介">
                <el-input
                  v-model="shopForm.shopDesc"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入店铺简介"
                />
              </el-form-item>
              <el-form-item label="主营类目">
                <el-select v-model="shopForm.mainCategory" placeholder="请选择主营类目">
                  <el-option label="数码电器" value="数码电器" />
                  <el-option label="服饰鞋包" value="服饰鞋包" />
                  <el-option label="美妆护肤" value="美妆护肤" />
                  <el-option label="家居生活" value="家居生活" />
                  <el-option label="母婴用品" value="母婴用品" />
                  <el-option label="食品生鲜" value="食品生鲜" />
                </el-select>
              </el-form-item>
              <el-form-item label="店铺公告">
                <el-input
                  v-model="shopForm.shopNotice"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入店铺公告"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingShop"
                  @click="saveShopInfo"
                >
                  保存店铺信息
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 营业状态 -->
        <el-tab-pane label="营业状态" name="business-status">
          <div class="tab-content">
            <el-card class="status-card">
              <div class="status-display">
                <div class="status-icon" :class="{ 'open': merchant.businessStatus === 1, 'closed': merchant.businessStatus === 0 }">
                  <el-icon :size="60">
                    <component :is="merchant.businessStatus === 1 ? 'CircleCheck' : 'Clock'" />
                  </el-icon>
                </div>
                <div class="status-text">
                  <h2>{{ merchant.businessStatus === 1 ? '正常营业' : '暂停营业' }}</h2>
                  <p>{{ merchant.businessStatus === 1 ? '用户可以正常下单购买' : '店铺休息中，用户无法下单' }}</p>
                </div>
              </div>
              <div class="status-action">
                <el-switch
                  v-model="businessStatus"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="营业"
                  inactive-text="休息"
                  @change="handleStatusChange"
                />
              </div>
            </el-card>
            <el-alert
              title="温馨提示"
              type="info"
              :closable="false"
              style="margin-top: 20px;"
            >
              <template #default>
                <p>暂停营业后，您的店铺将在前台显示休息状态，用户无法创建新订单，但已创建的订单不受影响。</p>
              </template>
            </el-alert>
          </div>
        </el-tab-pane>

        <!-- 联系方式 -->
        <el-tab-pane label="联系方式" name="contact">
          <div class="tab-content">
            <el-form :model="contactForm" label-width="150px" class="form-container">
              <el-form-item label="客服电话">
                <el-input v-model="contactForm.contactPhone" placeholder="请输入客服电话" />
              </el-form-item>
              <el-form-item label="在线客服">
                <el-input v-model="contactForm.onlineService" placeholder="请输入在线客服链接或账号" />
              </el-form-item>
              <el-form-item label="店铺地址">
                <el-input
                  v-model="contactForm.shopAddress"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入店铺详细地址"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingContact"
                  @click="saveContactInfo"
                >
                  保存联系方式
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 收款账户 -->
        <el-tab-pane label="收款账户" name="payment">
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" @click="openAddPaymentDialog">添加收款账户</el-button>
            </div>
            <el-table :data="paymentAccounts" style="width: 100%">
              <el-table-column prop="accountName" label="账户名称" width="150" />
              <el-table-column prop="accountType" label="账户类型" width="120">
                <template #default="{ row }">
                  <el-tag>{{ accountTypeMap[row.accountType] }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="accountNumber" label="账号" />
              <el-table-column prop="bankName" label="开户银行" />
              <el-table-column label="是否默认" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="250">
                <template #default="{ row }">
                  <el-button size="small" @click="editPaymentAccount(row)">编辑</el-button>
                  <el-button
                    v-if="row.isDefault !== 1"
                    size="small"
                    type="success"
                    @click="setDefaultPaymentAccount(row.id)"
                  >
                    设为默认
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="deletePaymentAccount(row.id)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty
              v-if="paymentAccounts.length === 0"
              description="暂无收款账户"
            />
          </div>
        </el-tab-pane>

        <!-- 入驻状态 -->
        <el-tab-pane label="入驻状态" name="settlement">
          <div class="tab-content">
            <el-card class="settlement-card">
              <div class="settlement-status">
                <el-steps :active="settlementStep" finish-status="success" align-center>
                  <el-step title="提交申请" description="申请已提交" />
                  <el-step title="资质审核" :description="auditDescription" />
                  <el-step title="入驻成功" description="开始您的电商之旅" />
                </el-steps>
              </div>
              <div class="settlement-info" style="margin-top: 40px;">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="店铺名称">{{ merchant.shopName }}</el-descriptions-item>
                  <el-descriptions-item label="入驻状态">
                    <el-tag :type="statusTagType">{{ statusText }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="申请时间">{{ merchant.createTime }}</el-descriptions-item>
                  <el-descriptions-item label="审核时间">{{ merchant.updateTime || '待审核' }}</el-descriptions-item>
                </el-descriptions>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 系统消息 -->
        <el-tab-pane label="消息中心" name="messages">
          <div class="tab-content">
            <div class="message-toolbar">
              <el-select v-model="messageFilter" placeholder="消息类型" style="width: 150px; margin-right: 10px;">
                <el-option label="全部" value="" />
                <el-option label="订单通知" value="order" />
                <el-option label="售后通知" value="refund" />
                <el-option label="平台公告" value="announcement" />
              </el-select>
              <el-select v-model="readFilter" placeholder="阅读状态" style="width: 120px; margin-right: 10px;">
                <el-option label="全部" :value="null" />
                <el-option label="未读" :value="0" />
                <el-option label="已读" :value="1" />
              </el-select>
              <el-button type="primary" @click="loadMessages">刷新</el-button>
              <el-button type="success" @click="handleBatchRead" :disabled="selectedMessages.length === 0">
                批量标记已读
              </el-button>
            </div>
            <div class="message-list">
              <div
                v-for="message in messages"
                :key="message.id"
                class="message-item"
                :class="{ 'unread': message.isRead === 0 }"
                @click="viewMessage(message)"
              >
                <div class="message-header">
                  <div class="message-type">
                    <el-tag :type="getMessageTypeTag(message.messageType)" size="small">
                      {{ getMessageTypeText(message.messageType) }}
                    </el-tag>
                    <span class="message-title">{{ message.title }}</span>
                  </div>
                  <div class="message-time">{{ message.createTime }}</div>
                </div>
                <div class="message-content">{{ message.content }}</div>
                <div class="message-footer">
                  <el-checkbox v-model="message.selected" @click.stop />
                </div>
              </div>
              <el-empty v-if="messages.length === 0" description="暂无消息" />
            </div>
          </div>
        </el-tab-pane>



        <!-- 操作日志 -->
        <el-tab-pane label="操作日志" name="logs">
          <div class="tab-content">
            <div class="log-toolbar">
              <el-select v-model="logFilter.operationType" placeholder="操作类型" style="width: 150px; margin-right: 10px;">
                <el-option label="全部" value="" />
                <el-option label="商品操作" value="商品操作" />
                <el-option label="订单操作" value="订单操作" />
                <el-option label="店铺设置" value="店铺设置" />
              </el-select>
              <el-date-picker
                v-model="logFilter.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="margin-right: 10px;"
              />
              <el-button type="primary" @click="loadLogs">查询</el-button>
              <el-button @click="resetLogFilter">重置</el-button>
            </div>
            <el-table :data="operationLogs" style="width: 100%; margin-top: 20px;">
              <el-table-column prop="operationType" label="操作类型" width="150" />
              <el-table-column prop="operationDesc" label="操作描述" />
              <el-table-column prop="createTime" label="操作时间" width="180" />
              <el-table-column prop="ipAddress" label="IP地址" width="140" />
            </el-table>
            <el-empty
              v-if="operationLogs.length === 0"
              description="暂无操作日志"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 收款账户弹窗 -->
    <el-dialog v-model="paymentDialogVisible" :title="editingPayment ? '编辑收款账户' : '添加收款账户'" width="500px">
      <el-form :model="paymentForm" label-width="120px">
        <el-form-item label="账户名称">
          <el-input v-model="paymentForm.accountName" placeholder="请输入账户名称" />
        </el-form-item>
        <el-form-item label="账户类型">
          <el-select v-model="paymentForm.accountType" placeholder="请选择账户类型">
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="微信支付" value="WECHAT" />
            <el-option label="银行卡" value="BANK" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="paymentForm.accountNumber" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="开户银行" v-if="paymentForm.accountType === 'BANK'">
          <el-input v-model="paymentForm.bankName" placeholder="请输入开户银行" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="paymentForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePaymentAccount">保存</el-button>
      </template>
    </el-dialog>



    <!-- 消息详情弹窗 -->
    <el-dialog v-model="messageDetailVisible" title="消息详情" width="600px">
      <div v-if="currentMessage" class="message-detail">
        <div class="detail-header">
          <el-tag :type="getMessageTypeTag(currentMessage.messageType)" size="small">
            {{ getMessageTypeText(currentMessage.messageType) }}
          </el-tag>
          <span class="detail-time">{{ currentMessage.createTime }}</span>
        </div>
        <h3>{{ currentMessage.title }}</h3>
        <div class="detail-content">{{ currentMessage.content }}</div>
      </div>
      <template #footer>
        <el-button @click="messageDetailVisible = false">关闭</el-button>
        <el-button v-if="currentMessage && currentMessage.isRead === 0" type="primary" @click="markAsRead">
          标记已读
        </el-button>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { CircleCheck, Clock } from "@element-plus/icons-vue";
import {
  getMerchantInfo,
  updateMerchantInfo,
  updatePassword,
  updateShopInfo,
  toggleBusinessStatus,
  getPaymentAccounts,
  createPaymentAccount,
  updatePaymentAccountById,
  deletePaymentAccountById,
  setDefaultAccount,
  getMessageListFull,
  readMessage,
  batchMarkAsRead,
  deleteMessageById,
  getOperationLogs
} from "@/api/merchant.js";

const activeTab = ref("personal");
const savingPersonal = ref(false);
const savingShop = ref(false);
const savingContact = ref(false);
const changingPassword = ref(false);
const uploadAction = computed(() => import.meta.env.PROD ? '/api/upload' : 'http://localhost:8080/api/upload');

const merchant = ref({});
const businessStatus = ref(1);

// 个人信息表单
const personalForm = reactive({
  avatar: "",
  phone: "",
  intro: ""
});

// 密码表单
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

// 店铺信息表单
const shopForm = reactive({
  shopName: "",
  shopLogo: "",
  shopDesc: "",
  mainCategory: "",
  shopNotice: ""
});

// 联系方式表单
const contactForm = reactive({
  contactPhone: "",
  onlineService: "",
  shopAddress: ""
});

// 收款账户相关
const accountTypeMap = {
  ALIPAY: "支付宝",
  WECHAT: "微信支付",
  BANK: "银行卡",
  alipay: "支付宝",
  wechat: "微信支付",
  bank: "银行卡"
};
const paymentAccounts = ref([]);
const paymentDialogVisible = ref(false);
const editingPayment = ref(false);
const paymentForm = reactive({
  id: null,
  accountName: "",
  accountType: "",
  accountNumber: "",
  bankName: "",
  isDefault: 0
});

// 消息相关
const messages = ref([]);
const messageFilter = ref("");
const readFilter = ref(null);
const selectedMessages = ref([]);
const messageDetailVisible = ref(false);
const currentMessage = ref(null);

// 监听消息筛选条件变化，自动刷新消息列表
const watchMessageFilter = () => {
  loadMessages();
};



// 操作日志相关
const operationLogs = ref([]);
const logFilter = reactive({
  operationType: "",
  dateRange: []
});

// 重置操作日志筛选条件
const resetLogFilter = () => {
  logFilter.operationType = "";
  logFilter.dateRange = [];
  loadLogs();
};

// 入驻状态
const settlementStep = computed(() => {
  if (merchant.value.status === 2) return 2;
  if (merchant.value.status === 1) return 1;
  return 0;
});

const auditDescription = computed(() => {
  if (merchant.value.status === 2) return "审核已通过";
  if (merchant.value.status === 0) return "审核未通过";
  return "审核中，请耐心等待";
});

const statusText = computed(() => {
  const statusMap = { 0: "已驳回", 1: "待审核", 2: "已通过" };
  return statusMap[merchant.value.status] || "未知";
});

const statusTagType = computed(() => {
  const typeMap = { 0: "danger", 1: "warning", 2: "success" };
  return typeMap[merchant.value.status] || "info";
});

// 加载商家信息
const loadMerchantInfo = async () => {
  try {
    const res = await getMerchantInfo();
    if (res.code === 200) {
      merchant.value = res.data;
      businessStatus.value = res.data.businessStatus || 1;
      Object.assign(personalForm, {
        avatar: res.data.avatar,
        phone: res.data.phone,
        intro: res.data.intro
      });
      Object.assign(shopForm, {
        shopName: res.data.shopName,
        shopLogo: res.data.shopLogo,
        shopDesc: res.data.shopDesc,
        mainCategory: res.data.mainCategory,
        shopNotice: res.data.shopNotice
      });
      Object.assign(contactForm, {
        contactPhone: res.data.contactPhone,
        onlineService: res.data.onlineService,
        shopAddress: res.data.shopAddress
      });
    }
  } catch (error) {
    console.error("加载商家信息失败:", error);
  }
};

// 保存个人信息
const savePersonalInfo = async () => {
  savingPersonal.value = true;
  try {
    const res = await updateMerchantInfo(personalForm);
    if (res.code === 200) {
      ElMessage.success("个人信息保存成功");
      loadMerchantInfo();
    }
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    savingPersonal.value = false;
  }
};

// 修改密码
const changePassword = async () => {
  if (!passwordForm.oldPassword) {
    ElMessage.warning("请输入当前密码");
    return;
  }
  if (!passwordForm.newPassword) {
    ElMessage.warning("请输入新密码");
    return;
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning("两次输入的密码不一致");
    return;
  }
  changingPassword.value = true;
  try {
    const res = await updatePassword(passwordForm);
    if (res.code === 200) {
      ElMessage.success("密码修改成功");
      passwordForm.oldPassword = "";
      passwordForm.newPassword = "";
      passwordForm.confirmPassword = "";
    }
  } catch (error) {
    ElMessage.error("密码修改失败");
  } finally {
    changingPassword.value = false;
  }
};

// 保存店铺信息
const saveShopInfo = async () => {
  savingShop.value = true;
  try {
    const res = await updateShopInfo(shopForm);
    if (res.code === 200) {
      ElMessage.success("店铺信息保存成功");
      loadMerchantInfo();
    }
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    savingShop.value = false;
  }
};

// 营业状态切换
const handleStatusChange = async (val) => {
  try {
    const res = await toggleBusinessStatus(val);
    if (res.code === 200) {
      ElMessage.success(val === 1 ? "店铺已开始营业" : "店铺已暂停营业");
      merchant.value.businessStatus = val;
    }
  } catch (error) {
    businessStatus.value = merchant.value.businessStatus || 1;
    ElMessage.error("状态切换失败");
  }
};

// 保存联系方式
const saveContactInfo = async () => {
  savingContact.value = true;
  try {
    const res = await updateMerchantInfo(contactForm);
    if (res.code === 200) {
      ElMessage.success("联系方式保存成功");
      loadMerchantInfo();
    }
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    savingContact.value = false;
  }
};

// 加载收款账户
const loadPaymentAccounts = async () => {
  try {
    const res = await getPaymentAccounts();
    if (res.code === 200) {
      paymentAccounts.value = res.data || [];
    }
  } catch (error) {
    console.error("加载收款账户失败:", error);
  }
};

// 添加收款账户弹窗
const openAddPaymentDialog = () => {
  editingPayment.value = false;
  paymentForm.id = null;
  paymentForm.accountName = "";
  paymentForm.accountType = "";
  paymentForm.accountNumber = "";
  paymentForm.bankName = "";
  paymentForm.isDefault = 0;
  paymentDialogVisible.value = true;
};

// 编辑收款账户
const editPaymentAccount = (row) => {
  editingPayment.value = true;
  Object.assign(paymentForm, row);
  paymentDialogVisible.value = true;
};

// 保存收款账户
const savePaymentAccount = async () => {
  if (!paymentForm.accountName) {
    ElMessage.warning("请输入账户名称");
    return;
  }
  if (!paymentForm.accountType) {
    ElMessage.warning("请选择账户类型");
    return;
  }
  if (!paymentForm.accountNumber) {
    ElMessage.warning("请输入账号");
    return;
  }
  try {
    let res;
    if (editingPayment.value) {
      res = await updatePaymentAccountById(paymentForm.id, paymentForm);
    } else {
      res = await createPaymentAccount(paymentForm);
    }
    if (res.code === 200) {
      ElMessage.success("保存成功");
      paymentDialogVisible.value = false;
      loadPaymentAccounts();
    }
  } catch (error) {
    ElMessage.error("保存失败");
  }
};

// 设置默认账户
const setDefaultPaymentAccount = async (id) => {
  try {
    const res = await setDefaultAccount(id);
    if (res.code === 200) {
      ElMessage.success("设置成功");
      loadPaymentAccounts();
    }
  } catch (error) {
    ElMessage.error("设置失败");
  }
};

// 删除收款账户
const deletePaymentAccount = async (id) => {
  ElMessageBox.confirm("确定要删除这个收款账户吗？", "提示", {
    type: "warning",
  }).then(async () => {
    try {
      const res = await deletePaymentAccountById(id);
      if (res.code === 200) {
        ElMessage.success("删除成功");
        loadPaymentAccounts();
      }
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 加载消息
const loadMessages = async () => {
  try {
    const params = {};
    if (messageFilter.value) params.messageType = messageFilter.value;
    if (readFilter.value !== null) params.isRead = readFilter.value;
    const res = await getMessageListFull(params);
    if (res.code === 200) {
      messages.value = (res.data?.messages || []).map(m => ({ ...m, selected: false }));
    }
  } catch (error) {
    console.error("加载消息失败:", error);
  }
};

// 查看消息
const viewMessage = (message) => {
  currentMessage.value = message;
  messageDetailVisible.value = true;
  if (message.isRead === 0) {
    markAsRead();
  }
};

// 标记已读
const markAsRead = async () => {
  if (!currentMessage.value) return;
  try {
    await readMessage(currentMessage.value.id);
    currentMessage.value.isRead = 1;
    const msg = messages.value.find(m => m.id === currentMessage.value.id);
    if (msg) msg.isRead = 1;
  } catch (error) {
    console.error("标记已读失败:", error);
  }
};

// 批量标记已读
const handleBatchRead = async () => {
  const ids = messages.value.filter(m => m.selected && m.isRead === 0).map(m => m.id);
  if (ids.length === 0) {
    ElMessage.warning("请选择要标记的消息");
    return;
  }
  try {
    await batchMarkAsRead(ids);
    ElMessage.success("批量标记成功");
    loadMessages();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 消息类型标签
const getMessageTypeTag = (type) => {
  const map = { order: "primary", refund: "warning", announcement: "success" };
  return map[type] || "info";
};

const getMessageTypeText = (type) => {
  const map = { order: "订单通知", refund: "售后通知", announcement: "平台公告" };
  return map[type] || "系统消息";
};



// 加载操作日志
const loadLogs = async () => {
  try {
    const params = {};
    if (logFilter.operationType) params.operationType = logFilter.operationType;
    if (logFilter.dateRange && logFilter.dateRange.length === 2) {
      params.startTime = logFilter.dateRange[0];
      params.endTime = logFilter.dateRange[1];
    }
    const res = await getOperationLogs(params);
    if (res.code === 200) {
      operationLogs.value = res.data || [];
    }
  } catch (error) {
    console.error("加载操作日志失败:", error);
  }
};

// 图片上传处理
const handleAvatarUpload = (response) => {
  if (response.code === 200) {
    personalForm.avatar = response.data;
  }
};

const handleLogoUpload = (response) => {
  if (response.code === 200) {
    shopForm.shopLogo = response.data;
  }
};

// 计算选中的消息
const selectedMessagesIds = computed(() => {
  return messages.value.filter(m => m.selected).map(m => m.id);
});

onMounted(() => {
  loadMerchantInfo();
  loadPaymentAccounts();
  loadMessages();
  loadLogs();
});

// 监听消息筛选条件变化
watch(messageFilter, watchMessageFilter);
watch(readFilter, watchMessageFilter);
</script>

<style scoped>
.settings {
  padding: 20px;
}

.tabs-card {
  min-height: calc(100vh - 120px);
}

.tab-content {
  padding: 20px;
  max-width: 800px;
}

.form-container {
  max-width: 600px;
}

.password-section {
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid #eee;
}

.password-section h3 {
  margin-bottom: 20px;
}

.avatar-preview,
.logo-preview {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.status-card {
  max-width: 600px;
}

.status-display {
  display: flex;
  align-items: center;
  gap: 30px;
}

.status-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon.open {
  background: #f0f9ff;
  color: #67c23a;
}

.status-icon.closed {
  background: #fef0f0;
  color: #f56c6c;
}

.status-text h2 {
  margin: 0 0 10px;
  font-size: 24px;
}

.status-text p {
  margin: 0;
  color: #909399;
}

.status-action {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.toolbar,
.message-toolbar,
.log-toolbar {
  margin-bottom: 20px;
}

.message-list {
  max-height: 500px;
  overflow-y: auto;
}

.message-item {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background: #f5f7fa;
  border-left: 3px solid #409eff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-type {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-title {
  font-weight: bold;
}

.message-time {
  color: #909399;
  font-size: 12px;
}

.message-content {
  color: #606266;
  margin-bottom: 8px;
}

.message-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.detail-time {
  color: #909399;
  font-size: 12px;
}

.detail-content {
  line-height: 1.6;
  color: #606266;
}


</style>
