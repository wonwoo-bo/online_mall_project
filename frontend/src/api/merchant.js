import request from "@/utils/merchantRequest";

export function register(data) {
  return request({
    url: "/register",
    method: "post",
    data,
  });
}

export function login(data) {
  return request({
    url: "/login",
    method: "post",
    data,
  });
}

export function getMerchantInfo() {
  return request({
    url: "/account/info",
    method: "get",
  });
}

export function updateMerchantInfo(data) {
  return request({
    url: "/account/info",
    method: "put",
    data,
  });
}

export function updatePassword(data) {
  return request({
    url: "/account/password",
    method: "put",
    data,
  });
}

export function updateBusinessStatus(data) {
  return request({
    url: "/account/business-status",
    method: "put",
    data,
  });
}

export function updateContactInfo(data) {
  return request({
    url: "/account/info",
    method: "put",
    data,
  });
}

export function updatePaymentAccount(data) {
  return request({
    url: "/account/payment-accounts/" + data.id,
    method: "put",
    data,
  });
}

export function getSettlementStatus() {
  return request({
    url: "/settlement/status",
    method: "get",
  });
}

export function getMessageList(params) {
  return request({
    url: "/messages",
    method: "get",
    params,
  });
}

export function readMessage(id) {
  return request({
    url: `/account/messages/${id}/read`,
    method: "put",
  });
}

export function readAllMessages() {
  return request({
    url: "/account/messages/read-all",
    method: "put",
  });
}

export function getDashboardData() {
  return request({
    url: "/dashboard",
    method: "get",
  });
}

export function getProductList(params) {
  return request({
    url: "/products",
    method: "get",
    params,
  });
}

export function getProductDetail(id) {
  return request({
    url: `/products/${id}`,
    method: "get",
  });
}

export function createProduct(data) {
  return request({
    url: "/products",
    method: "post",
    data,
  });
}

export function updateProduct(id, data) {
  return request({
    url: `/products/${id}`,
    method: "put",
    data,
  });
}

export function updateProductStatus(id, status) {
  return request({
    url: `/products/${id}/status`,
    method: "put",
    params: { status },
  });
}

export function deleteProduct(id) {
  return request({
    url: `/products/${id}`,
    method: "delete",
  });
}

export function uploadProductImages(id, formData) {
  return request({
    url: `/products/${id}/images`,
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function getOrderList(params) {
  return request({
    url: "/orders",
    method: "get",
    params,
  });
}

export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`,
    method: "get",
  });
}

export function shipOrder(id, data) {
  return request({
    url: `/orders/${id}/ship`,
    method: "post",
    data,
  });
}

export function confirmOrder(id) {
  return request({
    url: `/orders/${id}/confirm`,
    method: "put",
  });
}

export function confirmOrderApi(id) {
  return request({
    url: `/orders/${id}/confirm`,
    method: "post",
  });
}

export function closeOrder(id, data) {
  return request({
    url: `/orders/${id}/close`,
    method: "post",
    data,
  });
}

export function addOrderRemark(id, data) {
  return request({
    url: `/orders/${id}/remark`,
    method: "put",
    data,
  });
}

export function getOrderStatistics() {
  return request({
    url: "/orders/statistics",
    method: "get",
  });
}

export function getProfitStatistics() {
  return request({
    url: "/orders/profit",
    method: "get",
  });
}

// ========== 订单中心接口 ==========

export function modifyOrderPrice(id, data) {
  return request({
    url: `/order-center/orders/${id}/price`,
    method: "put",
    data,
  });
}

export function addOrderTag(id, data) {
  return request({
    url: `/order-center/orders/${id}/tag`,
    method: "post",
    data,
  });
}

export function removeOrderTag(id, data) {
  return request({
    url: `/order-center/orders/${id}/tag`,
    method: "delete",
    data,
  });
}

export function getOrderTags(id) {
  return request({
    url: `/order-center/orders/${id}/tags`,
    method: "get",
  });
}

export function getTagStatistics() {
  return request({
    url: "/order-center/tags/statistics",
    method: "get",
  });
}

export function batchAddTags(data) {
  return request({
    url: "/order-center/orders/batch/tags",
    method: "post",
    data,
  });
}

export function batchCloseOrders(data) {
  return request({
    url: "/order-center/orders/batch/close",
    method: "post",
    data,
  });
}

export function batchUpdateRemark(data) {
  return request({
    url: "/order-center/orders/batch/remark",
    method: "post",
    data,
  });
}

export function getTrackingInfo(orderId) {
  return request({
    url: `/order-center/orders/${orderId}/tracking`,
    method: "get",
  });
}

export function generateInvoice(orderId, data) {
  return request({
    url: `/order-center/orders/${orderId}/invoice`,
    method: "post",
    data,
  });
}

export function getInvoice(orderId) {
  return request({
    url: `/order-center/orders/${orderId}/invoice`,
    method: "get",
  });
}

export function cancelInvoice(id) {
  return request({
    url: `/order-center/invoices/${id}/cancel`,
    method: "put",
  });
}

export function getInvoices() {
  return request({
    url: "/order-center/invoices",
    method: "get",
  });
}

export function getInvoiceStatistics() {
  return request({
    url: "/order-center/invoices/statistics",
    method: "get",
  });
}

export function getOrderProfitStatistics(params) {
  return request({
    url: "/order-center/profit",
    method: "get",
    params,
  });
}

export function triggerAutoClose() {
  return request({
    url: "/order-center/orders/auto-close",
    method: "post",
  });
}

export function getReviewList(params) {
  return request({
    url: "/reviews",
    method: "get",
    params,
  });
}

export function replyReview(id, data) {
  return request({
    url: `/reviews/${id}/reply`,
    method: "put",
    data,
  });
}

export function getReviewStatistics() {
  return request({
    url: "/reviews/statistics",
    method: "get",
  });
}

export function reportReview(id, data) {
  return request({
    url: `/reviews/${id}/report`,
    method: "post",
    data,
  });
}

export function getReviewReportList() {
  return request({
    url: "/reviews/reports",
    method: "get",
  });
}

export function getPromotionList(params) {
  return request({
    url: "/promotions",
    method: "get",
    params,
  });
}

export function createPromotion(data) {
  return request({
    url: "/promotions",
    method: "post",
    data,
  });
}

export function updatePromotion(id, data) {
  return request({
    url: `/promotions/${id}`,
    method: "put",
    data,
  });
}

export function deletePromotion(id) {
  return request({
    url: `/promotions/${id}`,
    method: "delete",
  });
}

export function addPromotionProduct(id, data) {
  return request({
    url: `/promotions/${id}/products`,
    method: "post",
    data,
  });
}

export function removePromotionProduct(id, productId) {
  return request({
    url: `/promotions/${id}/products/${productId}`,
    method: "delete",
  });
}

export function getCouponList(params) {
  return request({
    url: "/coupons",
    method: "get",
    params,
  });
}

export function createCoupon(data) {
  return request({
    url: "/coupons",
    method: "post",
    data,
  });
}

export function updateCoupon(id, data) {
  return request({
    url: `/coupons/${id}`,
    method: "put",
    data,
  });
}

export function deleteCoupon(id) {
  return request({
    url: `/coupons/${id}`,
    method: "delete",
  });
}

export function publishCoupon(id) {
  return request({
    url: `/coupons/${id}/publish`,
    method: "post",
  });
}

export function updateCouponStatus(id, status) {
  return request({
    url: `/coupons/${id}/status`,
    method: "put",
    params: { status },
  });
}

export function getReturnList(params) {
  return request({
    url: "/returns",
    method: "get",
    params,
  });
}

export function handleReturn(id, data) {
  return request({
    url: `/returns/${id}/handle`,
    method: "put",
    data,
  });
}

export function getReturnDetail(id) {
  return request({
    url: `/returns/${id}`,
    method: "get",
  });
}

export function receiveReturn(id) {
  return request({
    url: `/returns/${id}/receive`,
    method: "put",
  });
}

export function getReturnStatistics() {
  return request({
    url: "/returns/statistics",
    method: "get",
  });
}

export function getReturnLogs(id) {
  return request({
    url: `/returns/${id}/logs`,
    method: "get",
  });
}

export function confirmRefund(id) {
  return request({
    url: `/returns/${id}/refund`,
    method: "put",
  });
}

export function getReturnAddress() {
  return request({
    url: "/return-address",
    method: "get",
  });
}

export function updateReturnAddress(data) {
  return request({
    url: "/return-address",
    method: "put",
    data,
  });
}

export function getStatisticsOverview(params) {
  return request({
    url: "/statistics/overview",
    method: "get",
    params,
  });
}

export function getStatisticsSales(params) {
  return request({
    url: "/statistics/sales",
    method: "get",
    params,
  });
}

export function getStatisticsProducts(params) {
  return request({
    url: "/statistics/products",
    method: "get",
    params,
  });
}

export function getStatisticsRevenue(params) {
  return request({
    url: "/statistics/revenue",
    method: "get",
    params,
  });
}

export function getStatisticsOrders(params) {
  return request({
    url: "/statistics/orders",
    method: "get",
    params,
  });
}

export function getStatisticsTraffic(params) {
  return request({
    url: "/statistics/traffic",
    method: "get",
    params,
  });
}

export function getStatisticsSalesTrend(params) {
  return request({
    url: "/statistics/trend/sales",
    method: "get",
    params,
  });
}

export function getStatisticsOrderTrend(params) {
  return request({
    url: "/statistics/trend/orders",
    method: "get",
    params,
  });
}

export function getStatisticsCoreMetrics(params) {
  return request({
    url: "/statistics/core-metrics",
    method: "get",
    params,
  });
}

export function getStatisticsProductConversion(params) {
  return request({
    url: "/statistics/product-conversion",
    method: "get",
    params,
  });
}

export function getStatisticsOverstock(params) {
  return request({
    url: "/statistics/overstock",
    method: "get",
    params,
  });
}

export function getStatisticsCustomerRegion(params) {
  return request({
    url: "/statistics/customer-region",
    method: "get",
    params,
  });
}

export function getStatisticsFinancial(params) {
  return request({
    url: "/statistics/financial",
    method: "get",
    params,
  });
}

export function exportStatisticsFinancial(params) {
  return request({
    url: "/statistics/financial/export",
    method: "get",
    params,
    responseType: "blob",
  });
}

export function getDsrScore() {
  return request({
    url: "/dsr",
    method: "get",
  });
}

export function getCategoryTree() {
  return request({
    url: "/categories",
    method: "get",
  });
}

export function getFirstLevelCategories() {
  return request({
    url: "/categories/first-level",
    method: "get",
  });
}

export function getSecondLevelCategories(parentId) {
  return request({
    url: `/categories/second-level/${parentId}`,
    method: "get",
  });
}

export function getCategoryById(id) {
  return request({
    url: `/categories/${id}`,
    method: "get",
  });
}

export function createCategory(data) {
  return request({
    url: "/category",
    method: "post",
    data,
  });
}

export function updateCategory(id, data) {
  return request({
    url: `/category/${id}`,
    method: "put",
    data,
  });
}

export function deleteCategory(id) {
  return request({
    url: `/category/${id}`,
    method: "delete",
  });
}

export function updateCategorySort(id, sortOrder) {
  return request({
    url: `/categories/${id}/sort`,
    method: "put",
    params: { sortOrder },
  });
}

export function getBrandList() {
  return request({
    url: "/brands",
    method: "get",
  });
}

export function getBrandById(id) {
  return request({
    url: `/brands/${id}`,
    method: "get",
  });
}

export function createBrand(data) {
  return request({
    url: "/brands",
    method: "post",
    data,
  });
}

export function updateBrand(id, data) {
  return request({
    url: `/brands/${id}`,
    method: "put",
    data,
  });
}

export function deleteBrand(id) {
  return request({
    url: `/brands/${id}`,
    method: "delete",
  });
}

export function updateBrandStatus(id, status) {
  return request({
    url: `/brands/${id}/status`,
    method: "put",
    params: { status },
  });
}

export function getSpecTypeList() {
  return request({
    url: "/specs/types",
    method: "get",
  });
}

export function getSpecTypeTree() {
  return request({
    url: "/specs/types/tree",
    method: "get",
  });
}

export function getSpecTypeById(id) {
  return request({
    url: `/specs/types/${id}`,
    method: "get",
  });
}

export function createSpecType(data) {
  return request({
    url: "/specs/types",
    method: "post",
    data,
  });
}

export function updateSpecType(id, data) {
  return request({
    url: `/specs/types/${id}`,
    method: "put",
    data,
  });
}

export function deleteSpecType(id) {
  return request({
    url: `/specs/types/${id}`,
    method: "delete",
  });
}

export function getSpecValueList(typeId) {
  return request({
    url: `/specs/values/${typeId}`,
    method: "get",
  });
}

export function getSpecValueById(id) {
  return request({
    url: `/specs/values/item/${id}`,
    method: "get",
  });
}

export function createSpecValue(data) {
  return request({
    url: "/specs/values",
    method: "post",
    data,
  });
}

export function updateSpecValue(id, data) {
  return request({
    url: `/specs/values/${id}`,
    method: "put",
    data,
  });
}

export function deleteSpecValue(id) {
  return request({
    url: `/specs/values/${id}`,
    method: "delete",
  });
}

export function getRecycleList(type = "product") {
  return request({
    url: "/recycle",
    method: "get",
    params: { type },
  });
}

export function getRecycleById(id) {
  return request({
    url: `/recycle/${id}`,
    method: "get",
  });
}

export function restoreItem(type, id) {
  return request({
    url: `/recycle/${type}/${id}/restore`,
    method: "put",
  });
}

export function deleteFromRecycle(type, id) {
  return request({
    url: `/recycle/${type}/${id}`,
    method: "delete",
  });
}

export function clearRecycle() {
  return request({
    url: "/recycle/clear",
    method: "delete",
  });
}

// 分类回收站API - 为了保持向后兼容
export function getRecycleCategoryList() {
  return getRecycleList("category");
}

export function restoreCategory(id) {
  return restoreItem("category", id);
}

export function deleteCategoryFromRecycle(id) {
  return deleteFromRecycle("category", id);
}

// 品牌回收站API - 为了保持向后兼容
export function getRecycleBrandList() {
  return getRecycleList("brand");
}

export function restoreBrand(id) {
  return restoreItem("brand", id);
}

export function deleteBrandFromRecycle(id) {
  return deleteFromRecycle("brand", id);
}

// 规格回收站API - 为了保持向后兼容
export function getRecycleSpecList() {
  return getRecycleList("spec");
}

export function restoreSpec(id) {
  return restoreItem("spec", id);
}

export function deleteSpecFromRecycle(id) {
  return deleteFromRecycle("spec", id);
}

// 商品回收站API - 为了保持向后兼容
export function restoreProduct(id) {
  return restoreItem("product", id);
}

export function deleteFromRecycleProduct(id) {
  return deleteFromRecycle("product", id);
}

// 清空指定类型回收站 - 暂时不需要这个功能
export function clearRecycleByType(type) {
  return request({
    url: `/recycle/clear/${type}`,
    method: "delete",
  });
}

export function clearDrafts() {
  return request({
    url: "/advanced/drafts/clear",
    method: "delete",
  });
}

export function getBusinessHours() {
  return request({
    url: "/business/hours",
    method: "get",
  });
}

export function updateBusinessHours(data) {
  return request({
    url: "/business/hours",
    method: "put",
    data,
  });
}

export function getSubaccountList() {
  return request({
    url: "/subaccounts",
    method: "get",
  });
}

export function createSubaccount(data) {
  return request({
    url: "/subaccounts",
    method: "post",
    data,
  });
}

export function updateSubaccount(id, data) {
  return request({
    url: `/subaccounts/${id}`,
    method: "put",
    data,
  });
}

export function deleteSubaccount(id) {
  return request({
    url: `/subaccounts/${id}`,
    method: "delete",
  });
}

export function updateSubaccountStatus(id, status) {
  return request({
    url: `/subaccounts/${id}/status`,
    method: "put",
    data: { status },
  });
}

export function getViolationList() {
  return request({
    url: "/violations",
    method: "get",
  });
}

export function handleViolation(id, data) {
  return request({
    url: `/violations/${id}/handle`,
    method: "put",
    data,
  });
}

export function getShopDecoration() {
  return request({
    url: "/decoration",
    method: "get",
  });
}

export function updateShopDecoration(data) {
  return request({
    url: "/decoration",
    method: "put",
    data,
  });
}

export function uploadBanner(data) {
  return request({
    url: "/decoration/banner",
    method: "post",
    data,
  });
}

export function deleteBanner(id) {
  return request({
    url: `/decoration/banner/${id}`,
    method: "delete",
  });
}

export function getDepositInfo() {
  return request({
    url: "/deposit",
    method: "get",
  });
}

export function rechargeDeposit(data) {
  return request({
    url: "/deposit/recharge",
    method: "post",
    data,
  });
}

export function getDepositRecords() {
  return request({
    url: "/deposit/records",
    method: "get",
  });
}

export function batchUpdateStatus(productIds, status) {
  return request({
    url: "/advanced/batch/status",
    method: "post",
    data: { productIds, status },
  });
}

export function batchUpdatePrice(productIds, price) {
  return request({
    url: "/advanced/batch/price",
    method: "post",
    data: { productIds, price },
  });
}

export function batchUpdateStock(productIds, stock) {
  return request({
    url: "/advanced/batch/stock",
    method: "post",
    data: { productIds, stock },
  });
}

export function batchUpdateCategory(productIds, categoryId) {
  return request({
    url: "/advanced/batch/category",
    method: "post",
    data: { productIds, categoryId },
  });
}

export function createSku(data) {
  return request({
    url: "/advanced/sku",
    method: "post",
    data,
  });
}

export function updateSku(id, data) {
  return request({
    url: `/advanced/sku/${id}`,
    method: "put",
    data,
  });
}

export function deleteSku(id) {
  return request({
    url: `/advanced/sku/${id}`,
    method: "delete",
  });
}

export function getSkuList(productId) {
  return request({
    url: `/advanced/sku/list/${productId}`,
    method: "get",
  });
}

export function getShippingTemplates() {
  return request({
    url: "/advanced/shipping/templates",
    method: "get",
  });
}

export function createShippingTemplate(data) {
  return request({
    url: "/advanced/shipping/template",
    method: "post",
    data,
  });
}

export function updateShippingTemplate(id, data) {
  return request({
    url: `/advanced/shipping/template/${id}`,
    method: "put",
    data,
  });
}

export function deleteShippingTemplate(id) {
  return request({
    url: `/advanced/shipping/template/${id}`,
    method: "delete",
  });
}

export function createSchedule(data) {
  return request({
    url: "/advanced/schedule",
    method: "post",
    data,
  });
}

export function updateSchedule(id, data) {
  return request({
    url: `/advanced/schedule/${id}`,
    method: "put",
    data,
  });
}

export function cancelSchedule(id) {
  return request({
    url: `/advanced/schedule/${id}`,
    method: "delete",
  });
}

export function getScheduleList() {
  return request({
    url: "/advanced/schedules",
    method: "get",
  });
}

export function saveDraft(data) {
  return request({
    url: "/advanced/draft",
    method: "post",
    data: data,
  });
}

export function updateDraft(id, data) {
  return request({
    url: `/advanced/draft/${id}`,
    method: "put",
    data: { productData: data },
  });
}

export function deleteDraft(id) {
  return request({
    url: `/advanced/draft/${id}`,
    method: "delete",
  });
}

export function getDraftList() {
  return request({
    url: "/advanced/drafts",
    method: "get",
  });
}

export function publishDraft(id) {
  return request({
    url: `/advanced/draft/${id}/publish`,
    method: "post",
  });
}

export function getRelatedProducts(productId) {
  return request({
    url: `/advanced/related/${productId}`,
    method: "get",
  });
}

export function addRelation(data) {
  return request({
    url: "/advanced/related",
    method: "post",
    data,
  });
}

export function removeRelation(id) {
  return request({
    url: `/advanced/related/${id}`,
    method: "delete",
  });
}

export function checkSensitiveWords(content) {
  return request({
    url: "/advanced/check-sensitive",
    method: "post",
    data: { content },
  });
}

export function createPresell(data) {
  return request({
    url: "/advanced/presell",
    method: "post",
    data,
  });
}

export function updatePresell(productId, data) {
  return request({
    url: `/advanced/presell/${productId}`,
    method: "put",
    data,
  });
}

export function cancelPresell(productId) {
  return request({
    url: `/advanced/presell/${productId}`,
    method: "delete",
  });
}

export function getPresellInfo(productId) {
  return request({
    url: `/advanced/presell/${productId}`,
    method: "get",
  });
}

export function getPresellList() {
  return request({
    url: "/advanced/presells",
    method: "get",
  });
}

export function getReturnReasonStatistics(params) {
  return request({
    url: "/advanced/returns/statistics/reasons",
    method: "get",
    params,
  });
}

export function getTimeoutConfig() {
  return request({
    url: "/advanced/returns/timeout/config",
    method: "get",
  });
}

export function updateTimeoutConfig(data) {
  return request({
    url: "/advanced/returns/timeout/config",
    method: "put",
    data,
  });
}

export function handleTimeoutReturns() {
  return request({
    url: "/advanced/returns/timeout/handle",
    method: "post",
  });
}

export function getBlacklist() {
  return request({
    url: "/advanced/returns/blacklist",
    method: "get",
  });
}

export function addToBlacklist(data) {
  return request({
    url: "/advanced/returns/blacklist",
    method: "post",
    data,
  });
}

export function removeFromBlacklist(id) {
  return request({
    url: `/advanced/returns/blacklist/${id}`,
    method: "delete",
  });
}

export function getShippingRule() {
  return request({
    url: "/advanced/returns/shipping/rule",
    method: "get",
  });
}

export function saveShippingRule(data) {
  return request({
    url: "/advanced/returns/shipping/rule",
    method: "put",
    data,
  });
}

export function getShippingInsurance() {
  return request({
    url: "/advanced/returns/shipping/insurance",
    method: "get",
  });
}

export function saveShippingInsurance(data) {
  return request({
    url: "/advanced/returns/shipping/insurance",
    method: "put",
    data,
  });
}

// 纠纷管理API
export function getDisputeList() {
  return request({
    url: "/merchant/dispute/list",
    method: "get",
  });
}

export function applyDispute(data) {
  return request({
    url: "/merchant/dispute/apply",
    method: "post",
    data,
  });
}

export function updateDispute(id, data) {
  return request({
    url: `/merchant/dispute/${id}`,
    method: "put",
    data,
  });
}

export function deleteDispute(id) {
  return request({
    url: `/merchant/dispute/${id}`,
    method: "delete",
  });
}

export function replyAppendReview(id, data) {
  return request({
    url: `/reviews/${id}/reply-append`,
    method: "put",
    data,
  });
}

export function toggleReviewTop(id, data) {
  return request({
    url: `/reviews/${id}/top`,
    method: "put",
    data,
  });
}

export function getAdvancedStatistics() {
  return request({
    url: "/reviews/statistics/advanced",
    method: "get",
  });
}

export function getExplanationByReviewId(id) {
  return request({
    url: `/reviews/${id}/explanation`,
    method: "get",
  });
}

export function getExplanationList() {
  return request({
    url: "/reviews/explanations",
    method: "get",
  });
}

export function createExplanation(id, data) {
  return request({
    url: `/reviews/${id}/explanation`,
    method: "post",
    data,
  });
}

export function updateExplanation(id, data) {
  return request({
    url: `/reviews/${id}/explanation`,
    method: "put",
    data,
  });
}

export function getTopByReviewId(id) {
  return request({
    url: `/reviews/${id}/top`,
    method: "get",
  });
}

export function getTopList() {
  return request({
    url: "/reviews/tops",
    method: "get",
  });
}

export function getAppealById(appealId) {
  return request({
    url: `/reviews/appeals/${appealId}`,
    method: "get",
  });
}

export function getAppealByReviewId(id) {
  return request({
    url: `/reviews/${id}/appeal`,
    method: "get",
  });
}

export function getAppealList() {
  return request({
    url: "/reviews/appeals",
    method: "get",
  });
}

export function createAppeal(id, data) {
  return request({
    url: `/reviews/${id}/appeal`,
    method: "post",
    data,
  });
}

export function createFullTestData() {
  return request({
    url: "/products/create-full-test-data",
    method: "post",
  });
}

export function getProductRanking(params) {
  return request({
    url: "/statistics/product-ranking",
    method: "get",
    params,
  });
}

// ========== 售后退款管理模块 ==========

export function getRefundStatistics() {
  return request({
    url: "/refunds/statistics",
    method: "get",
  });
}

export function getRefundList(params) {
  return request({
    url: "/refunds",
    method: "get",
    params,
  });
}

export function getRefundDetail(id) {
  return request({
    url: `/refunds/${id}`,
    method: "get",
  });
}

export function auditRefund(id, data) {
  return request({
    url: `/refunds/${id}/audit`,
    method: "post",
    data,
  });
}

export function confirmReceiveRefund(id) {
  return request({
    url: `/refunds/${id}/confirm-receive`,
    method: "post",
  });
}

export function confirmExecuteRefund(id) {
  return request({
    url: `/refunds/${id}/confirm-refund`,
    method: "post",
  });
}

export function getRefundLogs(id) {
  return request({
    url: `/refunds/${id}/logs`,
    method: "get",
  });
}

// ========== 退货地址管理 ==========

export function getReturnAddressList() {
  return request({
    url: "/refunds/return-addresses",
    method: "get",
  });
}

export function getDefaultReturnAddress() {
  return request({
    url: "/refunds/return-addresses/default",
    method: "get",
  });
}

export function addReturnAddress(data) {
  return request({
    url: "/refunds/return-addresses",
    method: "post",
    data,
  });
}

export function updateMerchantReturnAddress(id, data) {
  return request({
    url: `/refunds/return-addresses/${id}`,
    method: "put",
    data,
  });
}

export function deleteReturnAddress(id) {
  return request({
    url: `/refunds/return-addresses/${id}`,
    method: "delete",
  });
}

export function setDefaultReturnAddress(id) {
  return request({
    url: `/refunds/return-addresses/${id}/set-default`,
    method: "post",
  });
}

export function getRefundReasonStatistics(params) {
  return request({
    url: "/refunds/reason-statistics",
    method: "get",
    params,
  });
}

// ========== 商家账号与店铺管理模块 ==========

// 店铺信息
export function updateShopInfo(data) {
  return request({
    url: "/account/shop",
    method: "put",
    data,
  });
}

// 营业状态
export function toggleBusinessStatus(status) {
  return request({
    url: "/account/business-status",
    method: "put",
    data: { status },
  });
}

// 收款账户
export function getPaymentAccounts() {
  return request({
    url: "/account/payment-accounts",
    method: "get",
  });
}

export function createPaymentAccount(data) {
  return request({
    url: "/account/payment-accounts",
    method: "post",
    data,
  });
}

export function updatePaymentAccountById(id, data) {
  return request({
    url: `/account/payment-accounts/${id}`,
    method: "put",
    data,
  });
}

export function deletePaymentAccountById(id) {
  return request({
    url: `/account/payment-accounts/${id}`,
    method: "delete",
  });
}

export function setDefaultAccount(id) {
  return request({
    url: `/account/payment-accounts/${id}/default`,
    method: "put",
  });
}

// 商家消息
export function getMessageListFull(params) {
  return request({
    url: "/account/messages",
    method: "get",
    params,
  });
}

export function getMessageDetail(id) {
  return request({
    url: `/account/messages/${id}`,
    method: "get",
  });
}

export function batchMarkAsRead(ids) {
  return request({
    url: "/account/messages/batch-read",
    method: "put",
    data: { ids },
  });
}

export function deleteMessageById(id) {
  return request({
    url: `/account/messages/${id}`,
    method: "delete",
  });
}

// 子账号管理
export function getSubAccounts() {
  return request({
    url: "/account/sub-accounts",
    method: "get",
  });
}

export function getSubAccountDetail(id) {
  return request({
    url: `/account/sub-accounts/${id}`,
    method: "get",
  });
}

export function createSubAccountFull(data) {
  return request({
    url: "/account/sub-accounts",
    method: "post",
    data,
  });
}

export function updateSubAccountFull(id, data) {
  return request({
    url: `/account/sub-accounts/${id}`,
    method: "put",
    data,
  });
}

export function updateSubAccountPassword(id, newPassword) {
  return request({
    url: `/account/sub-accounts/${id}/password`,
    method: "put",
    data: { newPassword },
  });
}

export function deleteSubAccountById(id) {
  return request({
    url: `/account/sub-accounts/${id}`,
    method: "delete",
  });
}

// 操作日志
export function getOperationLogs(params) {
  return request({
    url: "/account/operation-logs",
    method: "get",
    params,
  });
}

// ========== 商品中心管理模块接口 ==========

// 商品价格更新
export function updateProductPrice(id, data) {
  return request({
    url: `/products/${id}/price`,
    method: "put",
    data,
  });
}

// 商品库存调整
export function adjustProductStock(id, data) {
  return request({
    url: `/products/${id}/stock`,
    method: "put",
    data,
  });
}

// 批量更新商品状态
export function batchUpdateProductStatus(data) {
  return request({
    url: "/products/batch/status",
    method: "put",
    data,
  });
}

// 恢复商品
export function restoreProductFromRecycle(id) {
  return request({
    url: `/products/${id}/restore`,
    method: "put",
  });
}

// 彻底删除商品
export function forceDeleteProduct(id) {
  return request({
    url: `/products/${id}/forever`,
    method: "delete",
  });
}

// 获取商品操作日志
export function getProductOperationLogs(id) {
  return request({
    url: `/products/${id}/logs`,
    method: "get",
  });
}

// 获取库存操作日志
export function getStockOperationLogs(id) {
  return request({
    url: `/products/${id}/stock-logs`,
    method: "get",
  });
}

// 获取商品统计
export function getProductStatistics() {
  return request({
    url: "/products/count",
    method: "get",
  });
}

// 商品发布（新增）
export function publishProduct(data) {
  return request({
    url: "/products",
    method: "post",
    data,
  });
}

// 商品编辑更新
export function editProduct(id, data) {
  return request({
    url: `/products/${id}`,
    method: "put",
    data,
  });
}

// 商品详情（商家）
export function getMerchantProductDetail(id) {
  return request({
    url: `/products/${id}`,
    method: "get",
  });
}

// 商品筛选查询
export function searchMerchantProducts(params) {
  return request({
    url: "/products",
    method: "get",
    params,
  });
}

// 商品图片上传
export function uploadProductImage(id, file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: `/products/${id}/images`,
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

// 删除商品图片
export function deleteProductImage(id, imageId) {
  return request({
    url: `/products/${id}/images/${imageId}`,
    method: "delete",
  });
}

// 批量删除商品图片
export function batchDeleteProductImages(id, imageIds) {
  return request({
    url: `/products/${id}/images/batch`,
    method: "delete",
    data: imageIds,
  });
}

// 更新商品图片排序
export function sortProductImages(id, imageIds) {
  return request({
    url: `/products/${id}/images/sort`,
    method: "put",
    data: { imageIds },
  });
}

// 批量上架
export function batchOnShelf(productIds) {
  return request({
    url: "/products/batch/status",
    method: "put",
    data: { ids: productIds, status: 1 },
  });
}

// 批量下架
export function batchOffShelf(productIds) {
  return request({
    url: "/products/batch/status",
    method: "put",
    data: { ids: productIds, status: 0 },
  });
}

// 商品复制
export function copyProduct(id) {
  return request({
    url: `/products/${id}/copy`,
    method: "post",
  });
}

// 商品预览
export function previewProduct(id) {
  return request({
    url: `/products/${id}/preview`,
    method: "get",
  });
}

// 获取商品SKU列表
export function getProductSkuList(id) {
  return request({
    url: `/products/${id}/skus`,
    method: "get",
  });
}

// 创建商品SKU
export function createProductSku(id, data) {
  return request({
    url: `/products/${id}/skus`,
    method: "post",
    data,
  });
}

// 更新商品SKU
export function updateProductSku(skuId, data) {
  return request({
    url: `/products/sku/${skuId}`,
    method: "put",
    data,
  });
}

// 删除商品SKU
export function deleteProductSku(skuId) {
  return request({
    url: `/products/sku/${skuId}`,
    method: "delete",
  });
}

// 批量设置商品属性
export function batchSetProductAttribute(productIds, data) {
  return request({
    url: "/products/batch/attribute",
    method: "put",
    data: { productIds, ...data },
  });
}

// 商品标签管理
export function getProductTags() {
  return request({
    url: "/products/tags",
    method: "get",
  });
}

export function createProductTag(data) {
  return request({
    url: "/products/tags",
    method: "post",
    data,
  });
}

export function updateProductTag(id, data) {
  return request({
    url: `/products/tags/${id}`,
    method: "put",
    data,
  });
}

export function deleteProductTag(id) {
  return request({
    url: `/products/tags/${id}`,
    method: "delete",
  });
}

export function setProductTags(productId, tagIds) {
  return request({
    url: `/products/${productId}/tags`,
    method: "put",
    data: { tagIds },
  });
}

// 商品违规下架
export function reportProductViolation(productId, data) {
  return request({
    url: `/products/${productId}/violation`,
    method: "post",
    data,
  });
}

// 获取商品违规记录
export function getProductViolationList(productId) {
  return request({
    url: `/products/${productId}/violations`,
    method: "get",
  });
}

// 商品举报
export function reportProduct(productId, data) {
  return request({
    url: `/products/${productId}/report`,
    method: "post",
    data,
  });
}

// 获取商品举报列表
export function getProductReportList(params) {
  return request({
    url: "/products/reports",
    method: "get",
    params,
  });
}

// 批量设置商品分类
export function batchSetCategory(productIds, categoryId) {
  return request({
    url: "/products/batch/category",
    method: "put",
    data: { productIds, categoryId },
  });
}

// 批量设置商品品牌
export function batchSetBrand(productIds, brandId) {
  return request({
    url: "/products/batch/brand",
    method: "put",
    data: { productIds, brandId },
  });
}

// 批量设置商品价格
export function batchSetPrice(productIds, price) {
  return request({
    url: "/products/batch/price",
    method: "put",
    data: { productIds, price },
  });
}

// 批量设置商品库存
export function batchSetStock(productIds, stock) {
  return request({
    url: "/products/batch/stock",
    method: "put",
    data: { productIds, stock },
  });
}

// 批量设置商品运费
export function batchSetShipping(productIds, shippingTemplateId) {
  return request({
    url: "/products/batch/shipping",
    method: "put",
    data: { productIds, shippingTemplateId },
  });
}
