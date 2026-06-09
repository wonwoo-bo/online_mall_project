<template>
  <div class="order-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单中心</span>
          <div class="header-actions">
            <el-button type="primary" @click="profitModal = true">
              <el-icon><PieChart /></el-icon>对账统计
            </el-button>
            <el-button
              type="primary"
              @click="showBatchModal = true"
              :disabled="selectedOrders.length === 0"
            >
              <el-icon><ShoppingCart /></el-icon>
              批量操作 ({{ selectedOrders.length }})
            </el-button>
            <el-button type="warning" @click="handleAutoClose">
              <el-icon><Clock /></el-icon>
              自动关单
            </el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索订单号、收货人、手机号"
          class="search-input"
          @keyup.enter="loadOrders"
        >
          <template #append>
            <el-button @click="loadOrders">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>

        <el-select
          v-model="queryParams.status"
          placeholder="订单状态"
          clearable
          class="status-select"
        >
          <el-option label="全部" value="" />
          <el-option label="待付款" :value="0" />
          <el-option label="待发货" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
          <el-option label="已关闭" :value="-1" />
        </el-select>

        <div class="date-range">
          <el-date-picker
            v-model="queryParams.startDate"
            type="date"
            placeholder="开始日期"
            class="date-input"
          />
          <span class="date-separator">至</span>
          <el-date-picker
            v-model="queryParams.endDate"
            type="date"
            placeholder="结束日期"
            class="date-input"
          />
        </div>

        <el-button type="primary" @click="loadOrders">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <div v-if="orderList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无订单数据" />
      </div>

      <el-table
        v-else
        :data="orderList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="买家" width="120" />
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div v-for="item in row.items" :key="item.id" class="order-item">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-details">
                <span v-if="item.brandName" class="brand-tag">{{
                  item.brandName
                }}</span>
                <span v-if="item.specs" class="specs-text">{{
                  parseSpecs(item.specs)
                }}</span>
              </div>
              <div class="product-quantity">x{{ item.quantity }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <strong>¥{{ row.totalAmount }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="标签" width="150">
          <template #default="{ row }">
            <div v-if="row.tagList && row.tagList.length > 0" class="tag-list">
              <span
                v-for="(tag, index) in row.tagList"
                :key="index"
                class="tag-item"
                :style="{ backgroundColor: tag.tagColor }"
              >
                {{ tag.tagName }}
              </span>
            </div>
            <div v-else-if="row.tags" class="tag-list">
              <span
                v-for="(tag, index) in parseTags(row.tags)"
                :key="index"
                class="tag-item"
                :style="{ backgroundColor: getTagColor(index) }"
              >
                {{ tag }}
              </span>
            </div>
            <span v-else class="no-tag">无标签</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="remark-text" :class="{ 'no-remark': !row.remark }">
              {{ row.remark || "无" }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)"
              >详情</el-button
            >
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handleModifyPrice(row)"
              >改价</el-button
            >
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handleConfirm(row)"
              >接单确认</el-button
            >
            <el-button
              v-if="row.status === 1"
              type="success"
              link
              size="small"
              @click="handleShip(row)"
              >发货</el-button
            >
            <el-button
              v-if="row.status === 0 || row.status === 1"
              type="warning"
              link
              size="small"
              @click="handleClose(row)"
              >关单</el-button
            >
            <el-button
              v-if="row.status !== -1"
              type="info"
              link
              size="small"
              @click="handleTag(row)"
              >标签</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div v-if="orderList.length > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="800px" draggable>
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{
          currentOrder.orderNo
        }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="买家">{{
          currentOrder.userName
        }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{
          currentOrder.receiverName
        }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{
          currentOrder.receiverPhone
        }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{
          currentOrder.shippingAddress
        }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <strong>¥{{ currentOrder.totalAmount }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{
          currentOrder.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="标签" :span="2">
          <div
            v-if="currentOrder.tagList && currentOrder.tagList.length > 0"
            class="tag-list"
          >
            <span
              v-for="(tag, index) in currentOrder.tagList"
              :key="index"
              class="tag-item"
              :style="{ backgroundColor: tag.tagColor }"
            >
              {{ tag.tagName }}
            </span>
          </div>
          <div v-else-if="currentOrder.tags" class="tag-list">
            <span
              v-for="(tag, index) in parseTags(currentOrder.tags)"
              :key="index"
              class="tag-item"
              :style="{ backgroundColor: getTagColor(index) }"
            >
              {{ tag }}
            </span>
          </div>
          <span v-else>无标签</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentOrder.remark || "无"
        }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>商品清单</el-divider>
      <el-table :data="currentOrder.items" size="small">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="brandName" label="品牌" width="100" />
        <el-table-column label="规格" width="150">
          <template #default="{ row }">{{ parseSpecs(row.specs) }}</template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.productPrice }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="100">
          <template #default="{ row }">¥{{ row.subtotal }}</template>
        </el-table-column>
      </el-table>

      <el-divider>物流信息</el-divider>
      <div
        v-if="
          (currentOrder &&
            currentOrder.status >= 2 &&
            currentOrder.expressCompany &&
            currentOrder.trackingNo) ||
          (currentTracking &&
            currentTracking.expressCompany &&
            currentTracking.trackingNo)
        "
        class="tracking-info"
      >
        <div class="tracking-header">
          <span class="express-info"
            >物流公司：{{
              (currentTracking && currentTracking.expressCompany) ||
              currentOrder.expressCompany
            }}</span
          >
          <span class="tracking-no"
            >运单号：{{
              (currentTracking && currentTracking.trackingNo) ||
              currentOrder.trackingNo
            }}</span
          >
        </div>
        <el-button
          type="text"
          @click="loadTrackingDetail(currentOrder)"
          :loading="loadingTracking"
        >
          <el-icon><Search /></el-icon>查看物流轨迹
        </el-button>
        <div v-if="trackingDetail.length > 0" class="tracking-list">
          <div class="tracking-route">
            <span class="route-start"
              >📦 发货地：{{
                currentTracking?.shipLocation ||
                trackingDetail[trackingDetail.length - 1]?.location ||
                "未知"
              }}</span
            >
            <span class="route-arrow">→</span>
            <span class="route-end"
              >🏠 收货地：{{
                currentTracking?.shippingAddress ||
                trackingDetail[0]?.location ||
                "未知"
              }}</span
            >
          </div>
          <div
            v-for="(item, index) in trackingDetail"
            :key="index"
            class="tracking-item"
            :class="{ 'tracking-latest': index === 0 }"
          >
            <div class="tracking-dot"></div>
            <div class="tracking-content">
              <div class="tracking-location">{{ item.location }}</div>
              <div class="tracking-status">{{ item.status }}</div>
              <div class="tracking-desc">{{ item.description }}</div>
              <div class="tracking-time">{{ formatTime(item.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="tracking-info empty-tracking">
        <div class="empty-tracking-text">
          <el-icon><Box /></el-icon>
          <span>订单尚未发货，暂无物流信息</span>
        </div>
      </div>

      <el-divider>发票信息</el-divider>
      <div class="invoice-section">
        <div v-if="currentInvoice" class="invoice-info">
          <div class="invoice-row">
            <span class="invoice-label">发票编号：</span>
            <span>{{ currentInvoice.invoiceNo }}</span>
          </div>
          <div class="invoice-row">
            <span class="invoice-label">发票类型：</span>
            <span>{{
              currentInvoice.invoiceType === 1
                ? "电子普通发票"
                : "增值税专用发票"
            }}</span>
          </div>
          <div class="invoice-row">
            <span class="invoice-label">发票抬头：</span>
            <span>{{ currentInvoice.title }}</span>
          </div>
          <div class="invoice-row">
            <span class="invoice-label">金额：</span>
            <span>¥{{ currentInvoice.amount }}</span>
          </div>
          <div class="invoice-row">
            <span class="invoice-label">状态：</span>
            <el-tag :type="currentInvoice.status === 1 ? 'success' : 'danger'">
              {{ currentInvoice.status === 1 ? "有效" : "已作废" }}
            </el-tag>
          </div>
          <div class="invoice-actions">
            <el-button
              v-if="currentInvoice.status === 1"
              type="danger"
              size="small"
              @click="handleCancelInvoice(currentInvoice)"
            >
              作废发票
            </el-button>
            <el-button
              v-if="currentInvoice.status === 2"
              type="primary"
              size="small"
              @click="invoiceModal = true"
            >
              <el-icon><Document /></el-icon>重新开具
            </el-button>
          </div>
        </div>
        <div v-else>
          <el-button type="primary" size="small" @click="invoiceModal = true">
            <el-icon><Document /></el-icon>生成发票
          </el-button>
        </div>
      </div>

      <el-divider>操作日志</el-divider>
      <el-table :data="currentOrderLogs" size="small">
        <el-table-column prop="operationDesc" label="操作描述" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentOrder && currentOrder.status === 0"
          type="warning"
          @click="handleModifyPrice(currentOrder)"
          >修改价格</el-button
        >
        <el-button
          v-if="currentOrder && currentOrder.status === 0"
          type="success"
          @click="handleConfirm(currentOrder)"
          >接单确认</el-button
        >
        <el-button
          v-if="currentOrder && currentOrder.status === 1"
          type="primary"
          @click="handleShip(currentOrder)"
          >确认发货</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="shipVisible" title="订单发货" width="500px" draggable>
      <el-form ref="shipFormRef" :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" prop="expressCompany" required>
          <el-select
            v-model="shipForm.expressCompany"
            placeholder="请选择物流公司"
          >
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="EMS" value="EMS" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="运单号" prop="trackingNo" required>
          <el-input
            v-model="shipForm.trackingNo"
            placeholder="请输入运单号"
            class="tracking-input"
          />
          <el-button size="small" @click="generateTrackingNo" type="text"
            >生成运单号</el-button
          >
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip" :loading="shipping"
          >确认发货</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="closeVisible" title="确认关单" width="400px" draggable>
      <div class="close-warning">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <p>
          确认要关闭订单 <strong>{{ closeOrderNo }}</strong> 吗？
        </p>
        <p class="warning-tip">订单关闭后状态不可回滚，请谨慎操作！</p>
      </div>
      <template #footer>
        <el-button @click="closeVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmClose" :loading="closing"
          >确认关单</el-button
        >
      </template>
    </el-dialog>

    <el-dialog
      v-model="confirmVisible"
      title="接单确认"
      width="450px"
      draggable
    >
      <div class="confirm-content">
        <p>
          确认接单订单 <strong>{{ closeOrderNo }}</strong> 吗？
        </p>
        <p class="confirm-tip">
          接单后订单将流转至待发货状态，请确保商品库存充足。
        </p>
      </div>
      <template #footer>
        <el-button @click="confirmVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder" :loading="confirming"
          >确认接单</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="remarkVisible" title="编辑备注" width="450px" draggable>
      <el-form ref="remarkFormRef" :model="remarkForm" label-width="60px">
        <el-form-item label="备注">
          <el-input
            v-model="remarkForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入备注内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="remarkVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRemark">保存备注</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="priceVisible"
      title="修改订单价格"
      width="450px"
      draggable
    >
      <el-form ref="priceFormRef" :model="priceForm" label-width="80px">
        <el-form-item label="当前金额" label-width="80px">
          <span class="current-price">¥{{ currentOrder?.totalAmount }}</span>
        </el-form-item>
        <el-form-item label="新金额" prop="newAmount" required>
          <el-input
            v-model="priceForm.newAmount"
            type="number"
            placeholder="请输入新金额"
            :min="0.01"
            step="0.01"
          />
        </el-form-item>
        <el-form-item label="修改原因">
          <el-input
            v-model="priceForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入修改原因（选填）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="priceVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmModifyPrice"
          :loading="modifyingPrice"
          >确认修改</el-button
        >
      </template>
    </el-dialog>

    <el-dialog
      v-model="tagVisible"
      title="订单标签管理"
      width="450px"
      draggable
    >
      <div class="tag-modal">
        <div class="tag-section">
          <div class="section-title">已有标签</div>
          <div v-if="currentTags.length > 0" class="tag-list-container">
            <div
              v-for="tag in currentTags"
              :key="tag.id"
              class="tag-item-with-remove"
              :style="{ backgroundColor: tag.tagColor }"
            >
              {{ tag.tagName }}
              <el-button size="mini" @click="removeTag(tag.tagName)"
                >移除</el-button
              >
            </div>
          </div>
          <div v-else class="empty-tags">暂无标签</div>
        </div>

        <div class="tag-section">
          <div class="section-title">添加标签</div>
          <el-form :model="tagForm" class="tag-form">
            <el-form-item class="tag-input-row">
              <el-input
                v-model="tagForm.tagName"
                placeholder="输入标签名称"
                class="tag-input"
              />
              <el-select
                v-model="tagForm.tagColor"
                placeholder="选择标签颜色"
                class="tag-color-select"
              >
                <el-option label="蓝色" value="#409EFF" />
                <el-option label="绿色" value="#67C23A" />
                <el-option label="橙色" value="#E6A23C" />
                <el-option label="红色" value="#F56C6C" />
                <el-option label="紫色" value="#9B59B6" />
                <el-option label="粉色" value="#F789AB" />
              </el-select>
              <el-color-picker
                v-model="tagForm.tagColor"
                size="small"
                class="tag-color-picker"
                show-alpha
              />
              <el-button
                type="primary"
                @click="addTag"
                :disabled="!tagForm.tagName"
                size="small"
                class="add-tag-btn"
                >添加标签</el-button
              >
            </el-form-item>
          </el-form>
        </div>

        <div class="tag-section">
          <div class="section-title">快捷标签</div>
          <div class="quick-tags">
            <span
              v-for="quickTag in quickTags"
              :key="quickTag.name"
              class="quick-tag"
              :style="{ backgroundColor: quickTag.color }"
              @click="addQuickTag(quickTag)"
            >
              {{ quickTag.name }}
            </span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="tagVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchModal" title="批量操作" width="500px" draggable>
      <el-form :model="batchForm" class="batch-form">
        <el-form-item>
          <el-radio-group v-model="batchForm.action">
            <el-radio label="batchRemark">批量备注</el-radio>
            <el-radio label="batchTag">批量标记标签</el-radio>
            <el-radio label="batchClose">批量关闭订单</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="batchForm.action === 'batchRemark'">
          <el-input
            v-model="batchForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注内容"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item v-if="batchForm.action === 'batchTag'">
          <el-input v-model="batchForm.tagName" placeholder="输入标签名称" />
          <el-select
            v-model="batchForm.tagColor"
            placeholder="选择标签颜色"
            class="tag-color-select"
          >
            <el-option label="蓝色" value="#409EFF" />
            <el-option label="绿色" value="#67C23A" />
            <el-option label="橙色" value="#E6A23C" />
            <el-option label="红色" value="#F56C6C" />
          </el-select>
        </el-form-item>

        <div
          v-if="batchForm.action === 'batchClose'"
          class="batch-close-warning"
        >
          <el-icon class="warning-icon"><Warning /></el-icon>
          <p>确认批量关闭选中的 {{ selectedOrders.length }} 个订单？</p>
          <p class="warning-tip">订单关闭后状态不可回滚，请谨慎操作！</p>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="batchModal = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmBatchOperation"
          :loading="batchLoading"
          :disabled="!canBatchSubmit"
        >
          {{ getBatchButtonText() }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="invoiceModal"
      title="生成电子发票"
      width="450px"
      draggable
    >
      <el-form ref="invoiceFormRef" :model="invoiceForm" label-width="80px">
        <el-form-item label="发票类型" prop="invoiceType" required>
          <el-radio-group v-model="invoiceForm.invoiceType">
            <el-radio :label="1">电子普通发票</el-radio>
            <el-radio :label="2">增值税专用发票</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发票抬头" prop="title" required>
          <el-input v-model="invoiceForm.title" placeholder="请输入发票抬头" />
        </el-form-item>
        <el-form-item label="税号">
          <el-input
            v-model="invoiceForm.taxNo"
            placeholder="请输入税号（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="invoiceModal = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmGenerateInvoice"
          :loading="generatingInvoice"
          >生成发票</el-button
        >
      </template>
    </el-dialog>

    <el-dialog
      v-model="profitModal"
      title="订单对账与利润统计"
      width="700px"
      draggable
    >
      <div class="profit-modal">
        <div class="profit-filters">
          <el-date-picker
            v-model="profitForm.startDate"
            type="date"
            placeholder="开始日期"
          />
          <span class="date-separator">至</span>
          <el-date-picker
            v-model="profitForm.endDate"
            type="date"
            placeholder="结束日期"
          />
          <el-button
            type="primary"
            @click="loadProfitStatistics"
            :loading="loadingProfit"
            >查询</el-button
          >
        </div>

        <div v-if="profitData" class="profit-stats">
          <div class="stat-card">
            <div class="stat-label">订单数量</div>
            <div class="stat-value">{{ profitData.orderCount }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">销售额</div>
            <div class="stat-value sales">¥{{ profitData.totalSales }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">成本</div>
            <div class="stat-value cost">¥{{ profitData.totalCost }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">利润</div>
            <div class="stat-value profit">¥{{ profitData.profit }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">利润率</div>
            <div class="stat-value rate">{{ profitData.profitRate }}%</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="profitModal = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  Warning,
  ShoppingCart,
  Clock,
  Document,
  PieChart,
  Box,
} from "@element-plus/icons-vue";
import {
  getOrderList,
  getOrderDetail,
  shipOrder,
  closeOrder,
  addOrderRemark,
  confirmOrderApi,
  modifyOrderPrice,
  addOrderTag,
  removeOrderTag,
  getOrderTags,
  batchCloseOrders,
  batchUpdateRemark,
  batchAddTags,
  getTrackingInfo,
  generateInvoice,
  getInvoice,
  cancelInvoice,
  getOrderProfitStatistics,
  triggerAutoClose,
} from "@/api/merchant";

const loading = ref(false);
const shipping = ref(false);
const closing = ref(false);
const confirming = ref(false);
const modifyingPrice = ref(false);
const loadingTracking = ref(false);
const generatingInvoice = ref(false);
const batchLoading = ref(false);
const loadingProfit = ref(false);
const orderList = ref([]);
const total = ref(0);
const detailVisible = ref(false);
const shipVisible = ref(false);
const closeVisible = ref(false);
const remarkVisible = ref(false);
const confirmVisible = ref(false);
const priceVisible = ref(false);
const tagVisible = ref(false);
const batchModal = ref(false);
const invoiceModal = ref(false);
const profitModal = ref(false);
const currentOrder = ref(null);
const currentOrderLogs = ref([]);
const currentTags = ref([]);
const currentTracking = ref(null);
const trackingDetail = ref([]);
const currentInvoice = ref(null);
const profitData = ref(null);
const shipFormRef = ref(null);
const remarkFormRef = ref(null);
const priceFormRef = ref(null);
const invoiceFormRef = ref(null);
const closeOrderNo = ref("");
const selectedOrders = ref([]);

const quickTags = [
  { name: "加急", color: "#F56C6C" },
  { name: "老客户", color: "#67C23A" },
  { name: "特殊单", color: "#E6A23C" },
  { name: "团购", color: "#409EFF" },
  { name: "定制", color: "#909399" },
];

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  status: "",
  keyword: "",
  startDate: "",
  endDate: "",
});

const shipForm = reactive({
  expressCompany: "",
  trackingNo: "",
});

const remarkForm = reactive({
  content: "",
});

const priceForm = reactive({
  newAmount: "",
  reason: "",
});

const tagForm = reactive({
  tagName: "",
  tagColor: "#409EFF",
});

const batchForm = reactive({
  action: "batchRemark",
  remark: "",
  tagName: "",
  tagColor: "#409EFF",
});

const invoiceForm = reactive({
  invoiceType: 1,
  title: "",
  taxNo: "",
});

const profitForm = reactive({
  startDate: "",
  endDate: "",
});

const canBatchSubmit = computed(() => {
  if (!batchForm.action) return false;
  if (batchForm.action === "batchRemark")
    return batchForm.remark.trim().length > 0;
  if (batchForm.action === "batchTag")
    return batchForm.tagName.trim().length > 0;
  return true;
});

const getStatusText = (status) => {
  const map = {
    0: "待付款",
    1: "待发货",
    2: "已发货",
    3: "已完成",
    4: "已取消",
    "-1": "已关闭",
  };
  return map[status] || "未知";
};

const getStatusType = (status) => {
  const map = {
    0: "danger",
    1: "warning",
    2: "primary",
    3: "success",
    4: "info",
    "-1": "info",
  };
  return map[status] || "info";
};

const parseSpecs = (specs) => {
  if (!specs) return "";
  try {
    const specObj = JSON.parse(specs);
    if (typeof specObj === "object") {
      return Object.values(specObj).join(" / ");
    }
    return specs;
  } catch {
    return specs;
  }
};

const parseTags = (tags) => {
  if (!tags) return [];
  return tags.split(",");
};

const getTagColor = (index) => {
  const colors = [
    "#409EFF",
    "#67C23A",
    "#E6A23C",
    "#F56C6C",
    "#909399",
    "#F789AB",
  ];
  return colors[index % colors.length];
};

const formatTime = (time) => {
  if (!time) return "";
  return time.replace("T", " ");
};

const loadOrders = async () => {
  loading.value = true;
  try {
    const res = await getOrderList(queryParams);
    if (res.code === 200) {
      orderList.value = res.data?.list || [];
      total.value = res.data?.total || orderList.value.length;
    }
  } catch (error) {
    ElMessage.error("加载订单失败");
  } finally {
    loading.value = false;
  }
};

const resetSearch = () => {
  queryParams.page = 1;
  queryParams.status = "";
  queryParams.keyword = "";
  queryParams.startDate = "";
  queryParams.endDate = "";
  loadOrders();
};

const handleSelectionChange = (val) => {
  selectedOrders.value = val.map((item) => item.id);
};

const viewDetail = async (row) => {
  loading.value = true;
  try {
    const [orderRes, tagsRes, invoiceRes, trackingRes] = await Promise.all([
      getOrderDetail(row.id),
      getOrderTags(row.id),
      getInvoice(row.id),
      getTrackingInfo(row.id),
    ]);

    if (orderRes.code === 200) {
      currentOrder.value = orderRes.data.order;
      currentOrderLogs.value = orderRes.data.logs || [];
    }
    if (tagsRes.code === 200) {
      currentTags.value = tagsRes.data || [];
      // 确保订单详情中的标签信息是最新的
      if (currentOrder.value) {
        if (currentTags.value && currentTags.value.length > 0) {
          currentOrder.value.tagList = [...currentTags.value];
          currentOrder.value.tags = currentTags.value
            .map((t) => t.tagName)
            .join(",");
        }
      }
    }
    if (invoiceRes.code === 200) {
      currentInvoice.value = invoiceRes.data;
    }
    if (trackingRes.code === 200 && trackingRes.data) {
      currentTracking.value = trackingRes.data;
      trackingDetail.value = trackingRes.data.trackingList || [];
    } else {
      currentTracking.value = null;
      trackingDetail.value = [];
    }
    detailVisible.value = true;
  } catch (error) {
    ElMessage.error("获取订单详情失败");
  } finally {
    loading.value = false;
  }
};

const loadTrackingDetail = async (order) => {
  loadingTracking.value = true;
  try {
    const res = await getTrackingInfo(order.id);
    if (res.code === 200) {
      currentTracking.value = res.data;
      trackingDetail.value = res.data.trackingList || [];
    }
  } catch (error) {
    ElMessage.error("获取物流信息失败");
  } finally {
    loadingTracking.value = false;
  }
};

const handleShip = (row) => {
  currentOrder.value = row;
  shipForm.expressCompany = "";
  shipForm.trackingNo = "";
  shipVisible.value = true;
  detailVisible.value = false;
};

const generateTrackingNo = () => {
  const prefix = shipForm.expressCompany
    ? shipForm.expressCompany.charAt(0).toUpperCase()
    : "Y";
  const now = new Date();
  const timestamp = now.getTime().toString().slice(-10);
  const random = Math.random().toString(36).slice(-4).toUpperCase();
  shipForm.trackingNo = `${prefix}${timestamp}${random}`;
};

const confirmShip = async () => {
  if (!shipFormRef.value) return;
  await shipFormRef.value.validate(async (valid) => {
    if (!valid) return;

    shipping.value = true;
    try {
      console.log("发货参数:", currentOrder.value.id, shipForm);
      const res = await shipOrder(currentOrder.value.id, shipForm);
      console.log("发货响应:", res);
      if (res.code === 200) {
        ElMessage.success("发货成功");
        shipVisible.value = false;
        loadOrders();
      } else {
        ElMessage.error(res.message || "发货失败");
      }
    } catch (error) {
      console.error("发货错误:", error);
      ElMessage.error(
        "发货失败: " +
          (error.response?.data?.message || error.message || "未知错误"),
      );
    } finally {
      shipping.value = false;
    }
  });
};

const handleClose = (row) => {
  currentOrder.value = row;
  closeOrderNo.value = row.orderNo;
  closeVisible.value = true;
};

const confirmClose = async () => {
  closing.value = true;
  try {
    const res = await closeOrder(currentOrder.value.id, {});
    if (res.code === 200) {
      ElMessage.success("订单已关闭");
      closeVisible.value = false;
      loadOrders();
    } else {
      ElMessage.error(res.message || "关单失败");
    }
  } catch (error) {
    ElMessage.error("关单失败");
  } finally {
    closing.value = false;
  }
};

const handleConfirm = (row) => {
  currentOrder.value = row;
  closeOrderNo.value = row.orderNo;
  confirmVisible.value = true;
};

const confirmOrder = async () => {
  confirming.value = true;
  try {
    const res = await confirmOrderApi(currentOrder.value.id);
    if (res.code === 200) {
      ElMessage.success("接单成功");
      confirmVisible.value = false;
      loadOrders();
    } else {
      ElMessage.error(res.message || "接单失败");
    }
  } catch (error) {
    ElMessage.error("接单失败");
  } finally {
    confirming.value = false;
  }
};

const handleRemark = (row) => {
  currentOrder.value = row;
  remarkForm.content = row.remark || "";
  remarkVisible.value = true;
};

const confirmRemark = async () => {
  try {
    const res = await addOrderRemark(currentOrder.value.id, {
      remark: remarkForm.content,
    });
    if (res.code === 200) {
      ElMessage.success("备注已更新");
      remarkVisible.value = false;
      loadOrders();
    } else {
      ElMessage.error(res.message || "更新备注失败");
    }
  } catch (error) {
    ElMessage.error("更新备注失败");
  }
};

const handleModifyPrice = (row) => {
  currentOrder.value = row;
  priceForm.newAmount = "";
  priceForm.reason = "";
  priceVisible.value = true;
};

const confirmModifyPrice = async () => {
  if (!priceForm.newAmount || parseFloat(priceForm.newAmount) <= 0) {
    ElMessage.error("请输入有效的金额");
    return;
  }

  modifyingPrice.value = true;
  try {
    const res = await modifyOrderPrice(currentOrder.value.id, {
      newAmount: priceForm.newAmount,
      reason: priceForm.reason,
    });
    if (res.code === 200) {
      ElMessage.success("价格修改成功");
      priceVisible.value = false;
      loadOrders();
    } else {
      ElMessage.error(res.message || "修改失败");
    }
  } catch (error) {
    ElMessage.error("修改失败");
  } finally {
    modifyingPrice.value = false;
  }
};

const handleTag = async (row) => {
  currentOrder.value = row;
  tagForm.tagName = "";
  tagForm.tagColor = "#409EFF";
  try {
    const res = await getOrderTags(row.id);
    if (res.code === 200) {
      currentTags.value = res.data || [];
    }
    tagVisible.value = true;
  } catch (error) {
    ElMessage.error("获取标签失败");
  }
};

const addTag = async () => {
  if (!tagForm.tagName.trim()) {
    ElMessage.error("请输入标签名称");
    return;
  }

  try {
    const res = await addOrderTag(currentOrder.value.id, {
      tagName: tagForm.tagName.trim(),
      tagColor: tagForm.tagColor,
    });
    if (res.code === 200) {
      ElMessage.success("标签添加成功");
      tagForm.tagName = "";
      const tagsRes = await getOrderTags(currentOrder.value.id);
      if (tagsRes.code === 200) {
        currentTags.value = tagsRes.data || [];
        // 更新订单详情中的标签显示
        if (currentOrder.value) {
          currentOrder.value.tags = currentTags.value
            .map((t) => t.tagName)
            .join(",");
          currentOrder.value.tagList = [...currentTags.value];
        }
      }
      loadOrders();
    } else {
      ElMessage.error(res.message || "添加失败");
    }
  } catch (error) {
    ElMessage.error("添加失败");
  }
};

const addQuickTag = async (quickTag) => {
  try {
    const res = await addOrderTag(currentOrder.value.id, {
      tagName: quickTag.name,
      tagColor: quickTag.color,
    });
    if (res.code === 200) {
      ElMessage.success("标签添加成功");
      const tagsRes = await getOrderTags(currentOrder.value.id);
      if (tagsRes.code === 200) {
        currentTags.value = tagsRes.data || [];
        // 更新订单详情中的标签显示
        if (currentOrder.value) {
          currentOrder.value.tags = currentTags.value
            .map((t) => t.tagName)
            .join(",");
          currentOrder.value.tagList = [...currentTags.value];
        }
      }
      loadOrders();
    } else {
      ElMessage.error(res.message || "添加失败");
    }
  } catch (error) {
    ElMessage.error("添加失败");
  }
};

const removeTag = async (tagName) => {
  try {
    const res = await removeOrderTag(currentOrder.value.id, { tagName });
    if (res.code === 200) {
      ElMessage.success("标签已移除");
      const tagsRes = await getOrderTags(currentOrder.value.id);
      if (tagsRes.code === 200) {
        currentTags.value = tagsRes.data || [];
        // 更新订单详情中的标签显示
        if (currentOrder.value) {
          currentOrder.value.tags = currentTags.value
            .map((t) => t.tagName)
            .join(",");
          currentOrder.value.tagList = [...currentTags.value];
        }
      }
      loadOrders();
    } else {
      ElMessage.error(res.message || "移除失败");
    }
  } catch (error) {
    ElMessage.error("移除失败");
  }
};

const getBatchButtonText = () => {
  const map = {
    batchRemark: "批量备注",
    batchTag: "批量标记",
    batchClose: "批量关单",
  };
  return map[batchForm.action] || "确认操作";
};

const confirmBatchOperation = async () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.error("请选择订单");
    return;
  }

  batchLoading.value = true;
  try {
    let res;
    if (batchForm.action === "batchRemark") {
      res = await batchUpdateRemark({
        orderIds: selectedOrders.value,
        remark: batchForm.remark,
      });
    } else if (batchForm.action === "batchTag") {
      res = await batchAddTags({
        orderIds: selectedOrders.value,
        tagName: batchForm.tagName,
        tagColor: batchForm.tagColor,
      });
    } else if (batchForm.action === "batchClose") {
      res = await batchCloseOrders({ orderIds: selectedOrders.value });
    }

    if (res && res.code === 200) {
      ElMessage.success("批量操作成功");
      batchModal.value = false;
      selectedOrders.value = [];
      loadOrders();
    } else {
      ElMessage.error(res?.message || "操作失败");
    }
  } catch (error) {
    ElMessage.error("操作失败");
  } finally {
    batchLoading.value = false;
  }
};

const handleAutoClose = async () => {
  try {
    await ElMessageBox.confirm("确认触发超时订单自动关闭？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await triggerAutoClose();
    if (res.code === 200) {
      ElMessage.success("已触发超时订单自动关闭");
      loadOrders();
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败");
    }
  }
};

const confirmGenerateInvoice = async () => {
  if (!invoiceForm.title.trim()) {
    ElMessage.error("请输入发票抬头");
    return;
  }

  generatingInvoice.value = true;
  try {
    const res = await generateInvoice(currentOrder.value.id, {
      title: invoiceForm.title,
      taxNo: invoiceForm.taxNo,
      invoiceType: invoiceForm.invoiceType,
    });
    if (res.code === 200) {
      ElMessage.success("发票生成成功");
      invoiceModal.value = false;
      currentInvoice.value = res.data;
    } else {
      ElMessage.error(res.message || "生成失败");
    }
  } catch (error) {
    ElMessage.error("生成失败");
  } finally {
    generatingInvoice.value = false;
  }
};

const handleCancelInvoice = async (invoice) => {
  try {
    await ElMessageBox.confirm("确认作废此发票？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await cancelInvoice(invoice.id);
    if (res.code === 200) {
      ElMessage.success("发票已作废");
      currentInvoice.value.status = 2;
    } else {
      ElMessage.error(res.message || "作废失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败");
    }
  }
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const loadProfitStatistics = async () => {
  loadingProfit.value = true;
  try {
    const res = await getOrderProfitStatistics({
      startTime: formatDate(profitForm.startDate),
      endTime: formatDate(profitForm.endDate),
    });
    if (res.code === 200) {
      profitData.value = res.data;
    }
  } catch (error) {
    ElMessage.error("获取统计数据失败");
  } finally {
    loadingProfit.value = false;
  }
};

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
  flex-wrap: wrap;
}

.search-input {
  width: 280px;
}

.status-select {
  width: 140px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-input {
  width: 140px;
}

.date-separator {
  color: #999;
}

.empty-state {
  padding: 40px 0;
}

.order-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  &:last-child {
    border-bottom: none;
  }
}

.product-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.product-details {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.brand-tag {
  background-color: #e8f4fd;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.specs-text {
  color: #909399;
  font-size: 13px;
}

.product-quantity {
  color: #666;
  font-size: 13px;
}

.remark-text {
  color: #666;
  font-size: 13px;
}

.remark-text.no-remark {
  color: #c0c4cc;
}

.tag-list {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag-item {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.no-tag {
  color: #c0c4cc;
  font-size: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.close-warning {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  color: #e6a23c;
  margin-bottom: 16px;
}

.warning-tip {
  color: #f56c6c;
  font-size: 14px;
  margin-top: 8px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #67c23a;
}

.tag-modal {
  padding: 10px 0;
}

.tag-section {
  margin-bottom: 20px;
}

.section-title {
  font-weight: 500;
  margin-bottom: 10px;
  color: #666;
}

.tag-list-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item-with-remove button {
  background: rgba(255, 255, 255, 0.3);
  border: none;
  color: #fff;
  padding: 0 4px;
  font-size: 10px;
}

.empty-tags {
  color: #c0c4cc;
  font-size: 13px;
}

.tag-form {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.tag-input-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 0 !important;
}

.tag-input {
  width: 150px;
}

.add-tag-btn {
  flex-shrink: 0;
}

.tag-color-select {
  width: 120px;
}

.tag-color-picker {
  flex-shrink: 0;
}

.tag-item-with-remove {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  border-radius: 4px;
  color: #fff;
  font-size: 12px;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-tag {
  padding: 4px 12px;
  border-radius: 4px;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
}

.batch-form {
  padding: 10px 0;
}

.batch-close-warning {
  text-align: center;
  padding: 20px;
  background: #fef0f0;
  border-radius: 8px;
}

.tracking-info {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.empty-tracking {
  text-align: center;
  padding: 30px;
}

.empty-tracking-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #909399;
  font-size: 14px;
}

.empty-tracking-text .el-icon {
  font-size: 48px;
  color: #c0c4cc;
}

.tracking-header {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.tracking-list {
  margin-top: 10px;
  padding-left: 20px;
  position: relative;
}

.tracking-route {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
  margin-bottom: 16px;
  font-size: 14px;
}

.route-start,
.route-end {
  flex: 1;
}

.route-arrow {
  font-size: 20px;
  font-weight: bold;
}

.tracking-location {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.tracking-list::before {
  content: "";
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e4e7ed;
}

.tracking-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  position: relative;
}

.tracking-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #c0c4cc;
  position: absolute;
  left: -16px;
  top: 16px;
}

.tracking-item.tracking-latest .tracking-dot {
  background: #67c23a;
}

.tracking-content {
  flex: 1;
}

.tracking-status {
  font-weight: 500;
  margin-bottom: 4px;
}

.tracking-desc {
  color: #666;
  font-size: 13px;
  margin-bottom: 4px;
}

.tracking-time {
  color: #909399;
  font-size: 12px;
}

.invoice-section {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.invoice-info {
  padding: 10px 0;
}

.invoice-row {
  margin-bottom: 8px;
}

.invoice-label {
  color: #909399;
}

.profit-modal {
  padding: 10px 0;
}

.profit-filters {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.profit-stats {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.stat-card {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.stat-label {
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-value.sales {
  color: #67c23a;
}
.stat-value.cost {
  color: #f56c6c;
}
.stat-value.profit {
  color: #409eff;
}
.stat-value.rate {
  color: #e6a23c;
}

.profit-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
}
</style>
