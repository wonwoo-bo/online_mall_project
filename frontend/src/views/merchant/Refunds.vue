<template>
  <div class="refunds-management">
    <el-card class="tabs-card">
      <el-tabs
        v-model="activeTab"
        type="border-card"
        @tab-change="handleTabChange"
      >
        <!-- 原售后退款管理标签页 -->
        <el-tab-pane label="售后列表" name="list">
          <div class="tab-content">
            <!-- 统计卡片 -->
            <el-card class="stats-card">
              <el-row :gutter="20">
                <el-col :span="4">
                  <div class="stat-item">
                    <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
                    <div class="stat-label">总售后数</div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="stat-item warning">
                    <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
                    <div class="stat-label">待审核</div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="stat-item info">
                    <div class="stat-value">{{ statistics.waitReturnCount || 0 }}</div>
                    <div class="stat-label">待退货</div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="stat-item primary">
                    <div class="stat-value">{{ statistics.returningCount || 0 }}</div>
                    <div class="stat-label">退货中</div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="stat-item danger">
                    <div class="stat-value">{{ statistics.waitRefundCount || 0 }}</div>
                    <div class="stat-label">待退款</div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="stat-item success">
                    <div class="stat-value">{{ statistics.completedCount || 0 }}</div>
                    <div class="stat-label">已完成</div>
                  </div>
                </el-col>
              </el-row>
            </el-card>

            <!-- 筛选栏 -->
            <el-card class="filter-card">
              <el-form :inline="true" :model="queryParams">
                <el-form-item label="售后类型">
                  <el-select v-model="queryParams.type" placeholder="全部" clearable @change="loadRefunds">
                    <el-option label="仅退款" :value="1" />
                    <el-option label="退货退款" :value="2" />
                  </el-select>
                </el-form-item>
                <el-form-item label="售后状态">
                  <el-select v-model="queryParams.status" placeholder="全部" clearable @change="loadRefunds">
                    <el-option label="待审核" :value="0" />
                    <el-option label="审核通过待退货" :value="1" />
                    <el-option label="退货中" :value="2" />
                    <el-option label="已收货待退款" :value="3" />
                    <el-option label="已完成" :value="4" />
                    <el-option label="已拒绝" :value="-1" />
                  </el-select>
                </el-form-item>
                <el-form-item label="订单号">
                  <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="loadRefunds" />
                </el-form-item>
                <el-form-item label="申请时间">
                  <el-date-picker
                    v-model="dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="YYYY-MM-DD"
                    @change="handleDateChange"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadRefunds">查询</el-button>
                  <el-button @click="resetQuery">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>

            <!-- 列表 -->
            <el-card>
              <el-table :data="refundList" v-loading="loading" stripe>
                <el-table-column prop="userId" label="用户ID" width="80" />
                <el-table-column prop="id" label="售后ID" width="80" />
                <el-table-column prop="orderNo" label="订单号" width="180" />
                <el-table-column label="商品信息" min-width="200">
                  <template #default="{ row }">
                    <div class="product-info">
                      <img :src="row.coverImg" class="product-thumb" />
                      <div class="product-name">{{ row.productName }}</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="售后类型" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.type === 1 ? 'primary' : 'success'">
                      {{ row.type === 1 ? '仅退款' : '退货退款' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="退款金额" width="120">
                  <template #default="{ row }">
                    <strong class="refund-amount">¥{{ row.refundAmount }}</strong>
                  </template>
                </el-table-column>
                <el-table-column label="申请原因" min-width="150" show-overflow-tooltip>
                  <template #default="{ row }">
                    {{ row.reason }}
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="120">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="申请时间" width="180" />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="viewDetail(row)">
                      查看详情
                    </el-button>
                    <template v-if="row.status === 0">
                      <el-button type="success" link size="small" @click="showAuditDialog(row, 1)">
                        同意
                      </el-button>
                      <el-button type="danger" link size="small" @click="showAuditDialog(row, 0)">
                        拒绝
                      </el-button>
                    </template>
                    <template v-else-if="row.status === 2">
                      <el-button type="primary" link size="small" @click="confirmReceive(row)">
                        确认收货
                      </el-button>
                    </template>
                    <template v-else-if="row.status === 3">
                      <el-button type="success" link size="small" @click="confirmRefund(row)">
                        确认退款
                      </el-button>
                    </template>
                  </template>
                </el-table-column>
              </el-table>

              <!-- 分页 -->
              <el-pagination
                v-model:current-page="queryParams.page"
                v-model:page-size="queryParams.pageSize"
                :total="total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="loadRefunds"
                @current-change="loadRefunds"
                class="pagination"
              />
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 售后原因统计 -->
        <el-tab-pane label="售后原因统计" name="statistics">
          <div class="tab-content">
            <div class="filter-bar">
              <el-date-picker
                v-model="statsDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="loadReasonStatistics"
              />
              <el-button type="primary" @click="loadReasonStatistics">查询</el-button>
            </div>

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
            <el-empty v-else description="暂无数据" />
          </div>
        </el-tab-pane>

        <!-- 超时自动处理 -->
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

        <!-- 运费规则 -->
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

        <!-- 运费险配置 -->
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

        <!-- 黑名单管理 -->
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

        <!-- 换货管理 -->
        <el-tab-pane label="换货管理" name="returns">
          <div class="tab-content">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span class="title">换货管理</span>
                  <el-select v-model="returnQueryParams.status" placeholder="处理状态" clearable @change="loadReturns">
                    <el-option label="全部" value="" />
                    <el-option label="待处理" :value="0" />
                    <el-option label="处理中" :value="1" />
                    <el-option label="已完成" :value="2" />
                    <el-option label="已拒绝" :value="-1" />
                  </el-select>
                </div>
              </template>

              <el-table :data="returnList" v-loading="returnLoading">
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
                    <el-tag :type="getReturnStatusType(row.status)">
                      {{ getReturnStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="申请时间" width="180" />
                <el-table-column label="操作" width="150" fixed="right">
                  <template #default="{ row }">
                    <el-button
                      v-if="row.status === 0"
                      type="primary"
                      link
                      size="small"
                      @click="handleReturn(row, 1)"
                    >
                      处理
                    </el-button>
                    <el-button type="primary" link size="small" @click="viewReturnDetail(row)">
                      详情
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 纠纷管理 -->
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="售后详情" width="800px" draggable>
      <div v-if="currentRefund" class="refund-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="售后ID">{{ currentRefund.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentRefund.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="售后类型">
            <el-tag :type="currentRefund.type === 1 ? 'primary' : 'success'">
              {{ currentRefund.type === 1 ? '仅退款' : '退货退款' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRefund.status)">
              {{ getStatusText(currentRefund.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="商品名称" :span="2">{{ currentRefund.productName }}</el-descriptions-item>
          <el-descriptions-item label="规格信息" :span="2">{{ currentRefund.specInfo }}</el-descriptions-item>
          <el-descriptions-item label="商品数量">{{ currentRefund.quantity }}</el-descriptions-item>
          <el-descriptions-item label="商品单价">¥{{ currentRefund.itemPrice }}</el-descriptions-item>
          <el-descriptions-item label="申请退款金额" class="refund-amount">¥{{ currentRefund.refundAmount }}</el-descriptions-item>
          <el-descriptions-item label="实际退款金额" v-if="currentRefund.actualRefundAmount">
            ¥{{ currentRefund.actualRefundAmount }}
          </el-descriptions-item>
          <el-descriptions-item label="申请原因" :span="2">{{ currentRefund.reason }}</el-descriptions-item>
          <el-descriptions-item label="用户凭证" :span="2" v-if="currentRefund.userEvidenceUrls">
            <div class="evidence-images">
              <img
                v-for="(url, index) in currentRefund.userEvidenceUrls.split(',')"
                :key="index"
                :src="url"
                class="evidence-image"
                @click="previewImage(url)"
              />
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentRefund.userName }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentRefund.createTime }}</el-descriptions-item>
          <el-descriptions-item label="审核时间" v-if="currentRefund.auditTime">{{ currentRefund.auditTime }}</el-descriptions-item>
          <el-descriptions-item label="收货时间" v-if="currentRefund.receiveTime">{{ currentRefund.receiveTime }}</el-descriptions-item>
          <el-descriptions-item label="退款时间" v-if="currentRefund.refundTime">{{ currentRefund.refundTime }}</el-descriptions-item>
          <el-descriptions-item label="商家备注" :span="2" v-if="currentRefund.merchantRemark">
            {{ currentRefund.merchantRemark }}
          </el-descriptions-item>
          <el-descriptions-item label="拒绝原因" :span="2" v-if="currentRefund.rejectReason">
            {{ currentRefund.rejectReason }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 退货地址 -->
        <div v-if="currentRefund.returnAddress" class="return-address">
          <h4>退货地址</h4>
          <p>收货人：{{ currentRefund.returnReceiverName }}</p>
          <p>联系电话：{{ currentRefund.returnReceiverPhone }}</p>
          <p>地址：{{ currentRefund.returnAddress }}</p>
        </div>

        <!-- 操作日志 -->
        <div class="operation-logs">
          <h4>操作日志</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(log, index) in operationLogs"
              :key="index"
              :timestamp="log.createTime"
            >
              <p><strong>{{ log.operatorName }}</strong> {{ getOperationText(log) }}</p>
              <p v-if="log.remark" class="log-remark">备注：{{ log.remark }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <template v-if="currentRefund && currentRefund.status === 0">
          <el-button type="success" @click="showAuditDialog(currentRefund, 1)">
            同意申请
          </el-button>
          <el-button type="danger" @click="showAuditDialog(currentRefund, 0)">
            拒绝申请
          </el-button>
        </template>
        <template v-else-if="currentRefund && currentRefund.status === 2">
          <el-button type="primary" @click="confirmReceive(currentRefund)">
            确认收货
          </el-button>
        </template>
        <template v-else-if="currentRefund && currentRefund.status === 3">
          <el-button type="success" @click="confirmRefund(currentRefund)">
            确认退款
          </el-button>
        </template>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditVisible" :title="auditAgree === 1 ? '同意申请' : '拒绝申请'" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="备注" v-if="auditAgree === 1">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（选填）"
          />
        </el-form-item>
        <el-form-item label="拒绝原因" v-if="auditAgree === 0" required>
          <el-input
            v-model="auditForm.rejectReason"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="auditLoading">
          确认提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 退货地址管理弹窗 -->
    <el-dialog v-model="addressVisible" title="退货地址管理" width="700px">
      <div class="address-management">
        <el-button type="primary" @click="showAddAddressDialog">
          添加地址
        </el-button>
        <el-table :data="addressList" style="margin-top: 15px;">
          <el-table-column prop="receiverName" label="收货人" width="100" />
          <el-table-column prop="receiverPhone" label="联系电话" width="130" />
          <el-table-column label="地址" min-width="250">
            <template #default="{ row }">
              {{ row.province }}{{ row.city }}{{ row.district }}{{ row.detailAddress }}
            </template>
          </el-table-column>
          <el-table-column label="是否默认" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="setDefaultAddress(row)">
                设为默认
              </el-button>
              <el-button link type="primary" size="small" @click="editAddress(row)">
                编辑
              </el-button>
              <el-button link type="danger" size="small" @click="deleteAddress(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog v-model="addressFormVisible" :title="editingAddress ? '编辑地址' : '添加地址'" width="500px">
      <el-form :model="addressForm" label-width="100px" :rules="addressRules" ref="addressFormRef">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addressFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddress" :loading="addressLoading">
          确认提交
        </el-button>
      </template>
    </el-dialog>

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

    <!-- 换货详情弹窗 -->
    <el-dialog v-model="returnDetailVisible" title="换货详情" width="600px" draggable>
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
          <el-tag :type="getReturnStatusType(currentReturn.status)">
            {{ getReturnStatusText(currentReturn.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentReturn.createTime }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="returnDetailVisible = false">关闭</el-button>
        <el-button v-if="currentReturn && currentReturn.status === 0" type="primary" @click="handleReturn(currentReturn, 1)">
          处理
        </el-button>
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

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imageViewerVisible"
      :url-list="[previewUrl]"
      :initial-index="0"
      @close="imageViewerVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRefundStatistics,
  getRefundList,
  getRefundDetail,
  auditRefund,
  confirmReceiveRefund,
  confirmExecuteRefund,
  getRefundLogs,
  getReturnAddressList,
  addReturnAddress,
  updateMerchantReturnAddress,
  deleteReturnAddress,
  setDefaultReturnAddress,
  getReturnList,
  handleReturn as apiHandleReturn,
  getRefundReasonStatistics
} from "@/api/merchant"

const loading = ref(false)
const statistics = ref({})
const refundList = ref([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  status: null,
  type: null,
  orderNo: '',
  userId: '',
  userName: '',
  startTime: '',
  endTime: ''
})
const dateRange = ref([])
const detailVisible = ref(false)
const currentRefund = ref(null)
const operationLogs = ref([])
const auditVisible = ref(false)
const auditAgree = ref(1)
const auditLoading = ref(false)
const auditForm = reactive({
  agree: 1,
  remark: '',
  rejectReason: ''
})
const addressVisible = ref(false)
const addressList = ref([])
const addressFormVisible = ref(false)
const editingAddress = ref(null)
const addressLoading = ref(false)
const addressFormRef = ref(null)
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})
const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}
const imageViewerVisible = ref(false)
const previewUrl = ref('')

const activeTab = ref('list')
const statsDateRange = ref([])
const reasonStats = ref([])
const statsTotalCount = ref(0)

const saving = ref(false)
const handling = ref(false)

const timeoutConfig = reactive({
  autoHandleEnabled: 0,
  reviewTimeout: 72,
  receiveTimeout: 168,
  autoApprove: 1,
})

const shippingRuleType = {
  1: "按金额免邮",
  2: "按重量计费",
  3: "按地区计费",
}

const shippingRule = reactive({
  id: null,
  name: "",
  type: 1,
  baseFee: 0,
  freeThreshold: 0,
  weightStart: 1,
  weightFee: 0,
  enabled: 1,
})

const shippingInsurance = reactive({
  id: null,
  enabled: 0,
  feeRate: 0.005,
  maxCompensation: 50,
  minAmount: 0,
})

const blacklist = ref([])
const addBlacklistVisible = ref(false)
const addBlacklistForm = reactive({
  userId: "",
  userName: "",
  userPhone: "",
  reason: "",
})

const disputes = ref([])
const disputeVisible = ref(false)
const disputeForm = reactive({
  returnId: "",
  userId: "",
  reason: "",
  evidence: "",
})

// 换货管理相关变量
const returnLoading = ref(false)
const returnList = ref([])
const returnDetailVisible = ref(false)
const currentReturn = ref(null)
const returnQueryParams = reactive({
  status: ''
})

// 换货状态映射
const getReturnStatusText = (status) => {
  const map = { 0: '待处理', 1: '处理中', 2: '已完成', '-1': '已拒绝' }
  return map[status] || '未知'
}

const getReturnStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', '-1': 'danger' }
  return map[status] || 'info'
}

const disputeStatusText = {
  0: "待平台介入",
  1: "处理中",
  2: "已裁决",
}

const disputeStatusType = {
  0: "warning",
  1: "info",
  2: "success",
}

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '待退货', type: 'info' },
  2: { text: '退货中', type: 'primary' },
  3: { text: '待退款', type: 'danger' },
  4: { text: '已完成', type: 'success' },
  '-1': { text: '已拒绝', type: 'info' }
}

const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

const getOperationText = (log) => {
  const operationMap = {
    'audit': '审核了售后申请',
    'confirm_receive': '确认收到退货',
    'confirm_refund': '确认退款'
  }
  return operationMap[log.operationType] || log.operationType
}

const formatTime = (time) => {
  if (!time) return "-"
  const date = new Date(time)
  return date.toLocaleString("zh-CN")
}

const getPercentage = (count) => {
  if (statsTotalCount.value === 0) return 0
  return ((count / statsTotalCount.value) * 100).toFixed(1)
}

const loadStatistics = async () => {
  try {
    const res = await getRefundStatistics()
    if (res.code === 200) {
      statistics.value = res.data || {}
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRefunds = async () => {
  loading.value = true
  try {
    const res = await getRefundList(queryParams)
    if (res.code === 200) {
      refundList.value = res.data?.list || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    ElMessage.error('加载售后列表失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    queryParams.startTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
}

const resetQuery = () => {
  queryParams.page = 1
  queryParams.pageSize = 10
  queryParams.status = null
  queryParams.type = null
  queryParams.orderNo = ''
  queryParams.userId = ''
  queryParams.userName = ''
  queryParams.startTime = ''
  queryParams.endTime = ''
  dateRange.value = []
  loadRefunds()
}

const viewDetail = async (row) => {
  currentRefund.value = null
  operationLogs.value = []
  try {
    const detailRes = await getRefundDetail(row.id)
    if (detailRes.code === 200) {
      currentRefund.value = detailRes.data
    } else {
      ElMessage.error(detailRes.message || '加载售后详情失败')
      return
    }
    
    try {
      const logsRes = await getRefundLogs(row.id)
      if (logsRes.code === 200) {
        operationLogs.value = logsRes.data || []
      }
    } catch (logError) {
      console.error('加载操作日志失败:', logError)
    }
    
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('加载售后详情失败: ' + (error.response?.data?.message || error.message))
  }
}

const showAuditDialog = (row, agree) => {
  auditAgree.value = agree
  auditForm.agree = agree
  auditForm.remark = ''
  auditForm.rejectReason = ''
  currentRefund.value = row
  auditVisible.value = true
}

const submitAudit = async () => {
  if (auditAgree.value === 0 && !auditForm.rejectReason.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  auditLoading.value = true
  try {
    const res = await auditRefund(currentRefund.value.id, {
      agree: auditAgree.value,
      rejectReason: auditForm.rejectReason,
      remark: auditForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('审核成功')
      auditVisible.value = false
      detailVisible.value = false
      loadRefunds()
      loadStatistics()
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

const confirmReceive = async (row) => {
  try {
    await ElMessageBox.confirm('确认已收到退货商品吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await confirmReceiveRefund(row.id)
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      loadRefunds()
      loadStatistics()
    } else {
      ElMessage.error(res.message || '确认收货失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

const confirmRefund = async (row) => {
  try {
    await ElMessageBox.confirm('确认执行退款吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await confirmExecuteRefund(row.id)
    if (res.code === 200) {
      ElMessage.success('确认退款成功')
      loadRefunds()
      loadStatistics()
    } else {
      ElMessage.error(res.message || '确认退款失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认退款失败')
    }
  }
}

const loadAddressList = async () => {
  try {
    const res = await getReturnAddressList()
    if (res.code === 200) {
      addressList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载地址列表失败')
  }
}

const showAddAddressDialog = () => {
  editingAddress.value = null
  Object.assign(addressForm, {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: 0
  })
  addressFormVisible.value = true
}

const editAddress = (row) => {
  editingAddress.value = row
  Object.assign(addressForm, {
    receiverName: row.receiverName,
    receiverPhone: row.receiverPhone,
    province: row.province,
    city: row.city,
    district: row.district,
    detailAddress: row.detailAddress,
    isDefault: row.isDefault
  })
  addressFormVisible.value = true
}

const deleteAddress = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteReturnAddress(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadAddressList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const setDefaultAddress = async (row) => {
  try {
    const res = await setDefaultReturnAddress(row.id)
    if (res.code === 200) {
      ElMessage.success('设置成功')
      loadAddressList()
    } else {
      ElMessage.error(res.message || '设置失败')
    }
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

const submitAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate(async (valid) => {
    if (!valid) return

    addressLoading.value = true
    try {
      let res
      if (editingAddress.value) {
        res = await updateMerchantReturnAddress(editingAddress.value.id, addressForm)
      } else {
        res = await addReturnAddress(addressForm)
      }
      if (res.code === 200) {
        ElMessage.success(editingAddress.value ? '更新成功' : '添加成功')
        addressFormVisible.value = false
        loadAddressList()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      addressLoading.value = false
    }
  })
}

const previewImage = (url) => {
  previewUrl.value = url
  imageViewerVisible.value = true
}

const handleTabChange = async (tabName) => {
  if (tabName === "blacklist") {
    await loadBlacklist()
  } else if (tabName === "disputes") {
    await loadDisputes()
  } else if (tabName === "statistics") {
    await loadReasonStatistics()
  } else if (tabName === "returns") {
    await loadReturns()
  }
}

// 换货管理相关函数
const loadReturns = async () => {
  returnLoading.value = true
  try {
    const res = await getReturnList(returnQueryParams)
    if (res.code === 200) {
      returnList.value = res.data?.list || res.data || []
    }
  } catch (error) {
    ElMessage.error('加载换货列表失败')
  } finally {
    returnLoading.value = false
  }
}

const viewReturnDetail = (row) => {
  currentReturn.value = row
  returnDetailVisible.value = true
}

const handleReturn = async (row, status) => {
  const action = status === 1 ? '同意' : '拒绝'
  await ElMessageBox.confirm(`确定要${action}该换货申请吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await apiHandleReturn(row.id, { status })
    if (res.code === 200) {
      ElMessage.success('处理成功')
      returnDetailVisible.value = false
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

const loadReasonStatistics = async () => {
  try {
    const params = {}
    if (statsDateRange.value && statsDateRange.value.length === 2) {
      params.startTime = statsDateRange.value[0]
      params.endTime = statsDateRange.value[1]
    }
    
    const res = await getRefundReasonStatistics(params)
    if (res.code === 200) {
      reasonStats.value = res.data || []
      statsTotalCount.value = reasonStats.value.reduce((sum, item) => sum + item.totalCount, 0)
    }
  } catch (error) {
    console.error("获取统计数据失败", error)
  }
}

const saveTimeoutConfig = async () => {
  saving.value = true
  try {
    ElMessage.success("配置保存成功")
  } catch (error) {
    ElMessage.error("保存失败")
  } finally {
    saving.value = false
  }
}

const triggerTimeoutHandle = async () => {
  handling.value = true
  try {
    ElMessage.success("超时处理完成")
  } catch (error) {
    ElMessage.error("处理失败")
  } finally {
    handling.value = false
  }
}

const handleSaveShippingRule = async () => {
  saving.value = true
  try {
    ElMessage.success("保存成功")
  } catch (error) {
    ElMessage.error("保存失败")
  } finally {
    saving.value = false
  }
}

const handleSaveShippingInsurance = async () => {
  saving.value = true
  try {
    ElMessage.success("保存成功")
  } catch (error) {
    ElMessage.error("保存失败")
  } finally {
    saving.value = false
  }
}

const loadBlacklist = async () => {
  try {
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
        createTime: "2024-02-12 16:30:00",
      },
    ]
  } catch (error) {
    console.error("获取黑名单失败", error)
  }
}

const openAddBlacklistDialog = () => {
  addBlacklistForm.userId = ""
  addBlacklistForm.userName = ""
  addBlacklistForm.userPhone = ""
  addBlacklistForm.reason = ""
  addBlacklistVisible.value = true
}

const submitAddBlacklist = async () => {
  if (!addBlacklistForm.userId || !addBlacklistForm.reason) {
    ElMessage.warning("请填写必要信息")
    return
  }
  try {
    ElMessage.success("添加成功")
    addBlacklistVisible.value = false
    await loadBlacklist()
  } catch (error) {
    ElMessage.error("添加失败")
  }
}

const removeBlacklist = async (id) => {
  ElMessageBox.confirm("确定要将该用户移出黑名单吗？", "提示", {
    type: "warning",
  }).then(async () => {
    try {
      ElMessage.success("移除成功")
      await loadBlacklist()
    } catch (error) {
      ElMessage.error("移除失败")
    }
  })
}

const loadDisputes = async () => {
  try {
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
    ]
  } catch (error) {
    console.error("获取纠纷列表失败", error)
  }
}

const openDisputeDialog = () => {
  disputeForm.returnId = ""
  disputeForm.userId = ""
  disputeForm.reason = ""
  disputeForm.evidence = ""
  disputeVisible.value = true
}

const submitDispute = async () => {
  if (!disputeForm.returnId || !disputeForm.userId || !disputeForm.reason) {
    ElMessage.warning("请填写必要信息")
    return
  }
  try {
    ElMessage.success("申请已提交")
    disputeVisible.value = false
    await loadDisputes()
  } catch (error) {
    ElMessage.error("提交失败")
  }
}

onMounted(() => {
  loadStatistics()
  loadRefunds()
})

watch(
  () => activeTab.value,
  () => {
    if (activeTab.value === "statistics") {
      nextTick(() => {
        renderChart()
      })
    }
  },
)
</script>

<style scoped>
.refunds-management {
  padding: 20px;
}

.tabs-card {
  height: calc(100vh - 120px);
}

.tab-content {
  padding: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-item .stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-item.warning .stat-value {
  color: #e6a23c;
}

.stat-item.info .stat-value {
  color: #409eff;
}

.stat-item.primary .stat-value {
  color: #409eff;
}

.stat-item.danger .stat-value {
  color: #f56c6c;
}

.stat-item.success .stat-value {
  color: #67c23a;
}

.stat-item .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.filter-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-thumb {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.product-name {
  font-size: 14px;
  color: #303133;
}

.refund-amount {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.refund-detail {
  max-height: 600px;
  overflow-y: auto;
}

.evidence-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.evidence-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #ebeef5;
}

.return-address {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.return-address h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}

.return-address p {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
}

.operation-logs {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.operation-logs h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}

.log-remark {
  margin: 5px 0 0 0;
  font-size: 13px;
  color: #909399;
}

.address-management {
  max-height: 400px;
  overflow-y: auto;
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
