package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.*;
import com.mall.module.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/account")
public class MerchantAccountController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantPaymentAccountService merchantPaymentAccountService;

    @Autowired
    private MerchantMessageService merchantMessageService;

    @Autowired
    private MerchantSubAccountService merchantSubAccountService;

    @Autowired
    private MerchantOperationLogService merchantOperationLogService;

    @GetMapping("/info")
    public Result<Merchant> getMerchantInfo(@RequestAttribute("userId") Integer userId) {
        Merchant merchant = merchantService.getMerchantInfo(userId);
        return Result.success(merchant);
    }

    @PutMapping("/info")
    public Result<Void> updateMerchantInfo(@RequestAttribute("userId") Integer userId, @RequestBody Merchant merchant) {
        merchant.setId(userId);
        merchantService.updateMerchantInfo(merchant);
        return Result.success("更新成功", null);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestAttribute("userId") Integer userId, @RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String confirmPassword = params.get("confirmPassword");
        
        if (!newPassword.equals(confirmPassword)) {
            return Result.error("两次输入的新密码不一致");
        }
        
        merchantService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }

    @PutMapping("/shop")
    public Result<Void> updateShopInfo(@RequestAttribute("userId") Integer userId, @RequestBody Merchant merchant) {
        merchant.setId(userId);
        merchantService.updateShopInfo(merchant);
        return Result.success("店铺信息更新成功", null);
    }

    @PutMapping("/business-status")
    public Result<Void> toggleBusinessStatus(@RequestAttribute("userId") Integer userId, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        merchantService.toggleBusinessStatus(userId, status);
        return Result.success("营业状态更新成功", null);
    }

    @GetMapping("/payment-accounts")
    public Result<List<MerchantPaymentAccount>> getPaymentAccounts(@RequestAttribute("userId") Integer userId) {
        List<MerchantPaymentAccount> accounts = merchantPaymentAccountService.getAccountsByMerchantId(userId);
        return Result.success(accounts);
    }

    @PostMapping("/payment-accounts")
    public Result<Void> createPaymentAccount(@RequestAttribute("userId") Integer userId, @RequestBody MerchantPaymentAccount account) {
        account.setMerchantId(userId);
        merchantPaymentAccountService.createAccount(account);
        return Result.success("收款账户添加成功", null);
    }

    @PutMapping("/payment-accounts/{id}")
    public Result<Void> updatePaymentAccount(@RequestAttribute("userId") Integer userId, @PathVariable Integer id, @RequestBody MerchantPaymentAccount account) {
        account.setId(id);
        account.setMerchantId(userId);
        merchantPaymentAccountService.updateAccount(account);
        return Result.success("收款账户更新成功", null);
    }

    @DeleteMapping("/payment-accounts/{id}")
    public Result<Void> deletePaymentAccount(@RequestAttribute("userId") Integer userId, @PathVariable Integer id) {
        merchantPaymentAccountService.deleteAccount(id);
        return Result.success("收款账户删除成功", null);
    }

    @PutMapping("/payment-accounts/{id}/default")
    public Result<Void> setDefaultAccount(@RequestAttribute("userId") Integer userId, @PathVariable Integer id) {
        merchantPaymentAccountService.setDefaultAccount(userId, id);
        return Result.success("默认账户设置成功", null);
    }

    @GetMapping("/messages")
    public Result<Map<String, Object>> getMessages(@RequestAttribute("userId") Integer userId,
                                                    @RequestParam(required = false) String messageType,
                                                    @RequestParam(required = false) Integer isRead) {
        List<MerchantMessage> messages = merchantMessageService.getMessagesByMerchantId(userId, messageType, isRead);
        int unreadCount = merchantMessageService.countUnreadMessages(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("unreadCount", unreadCount);
        return Result.success(result);
    }

    @GetMapping("/messages/{id}")
    public Result<MerchantMessage> getMessageDetail(@RequestAttribute("userId") Integer userId, @PathVariable Long id) {
        merchantMessageService.markAsRead(id);
        MerchantMessage message = merchantMessageService.getMessageById(id);
        return Result.success(message);
    }

    @PutMapping("/messages/{id}/read")
    public Result<Void> markAsRead(@RequestAttribute("userId") Integer userId, @PathVariable Long id) {
        merchantMessageService.markAsRead(id);
        return Result.success("标记成功", null);
    }

    @PutMapping("/messages/batch-read")
    public Result<Void> batchMarkAsRead(@RequestAttribute("userId") Integer userId, @RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        merchantMessageService.batchMarkAsRead(userId, ids);
        return Result.success("批量标记成功", null);
    }

    @DeleteMapping("/messages/{id}")
    public Result<Void> deleteMessage(@RequestAttribute("userId") Integer userId, @PathVariable Long id) {
        merchantMessageService.deleteMessage(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/sub-accounts")
    public Result<List<MerchantSubAccount>> getSubAccounts(@RequestAttribute("userId") Integer userId) {
        List<MerchantSubAccount> subAccounts = merchantSubAccountService.getSubAccountsByMerchantId(userId);
        return Result.success(subAccounts);
    }

    @GetMapping("/sub-accounts/{id}")
    public Result<Map<String, Object>> getSubAccountDetail(@RequestAttribute("userId") Integer userId, @PathVariable Integer id) {
        MerchantSubAccount subAccount = merchantSubAccountService.getSubAccountById(id);
        List<MerchantSubAccountPermission> permissions = merchantSubAccountService.getPermissionsBySubAccountId(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("subAccount", subAccount);
        result.put("permissions", permissions);
        return Result.success(result);
    }

    @PostMapping("/sub-accounts")
    public Result<Void> createSubAccount(@RequestAttribute("userId") Integer userId, @RequestBody Map<String, Object> params) {
        MerchantSubAccount subAccount = new MerchantSubAccount();
        subAccount.setMerchantId(userId);
        subAccount.setUsername((String) params.get("username"));
        subAccount.setPassword((String) params.get("password"));
        subAccount.setRealName((String) params.get("realName"));
        subAccount.setPhone((String) params.get("phone"));
        subAccount.setEmail((String) params.get("email"));
        
        @SuppressWarnings("unchecked")
        List<String> permissionCodes = (List<String>) params.get("permissions");
        
        merchantSubAccountService.createSubAccount(subAccount, permissionCodes);
        return Result.success("子账号创建成功", null);
    }

    @PutMapping("/sub-accounts/{id}")
    public Result<Void> updateSubAccount(@RequestAttribute("userId") Integer userId, @PathVariable Integer id, @RequestBody Map<String, Object> params) {
        MerchantSubAccount subAccount = new MerchantSubAccount();
        subAccount.setId(id);
        subAccount.setMerchantId(userId);
        subAccount.setRealName((String) params.get("realName"));
        subAccount.setPhone((String) params.get("phone"));
        subAccount.setEmail((String) params.get("email"));
        subAccount.setStatus((Integer) params.get("status"));
        
        @SuppressWarnings("unchecked")
        List<String> permissionCodes = (List<String>) params.get("permissions");
        
        merchantSubAccountService.updateSubAccount(subAccount, permissionCodes);
        return Result.success("子账号更新成功", null);
    }

    @PutMapping("/sub-accounts/{id}/password")
    public Result<Void> updateSubAccountPassword(@RequestAttribute("userId") Integer userId, @PathVariable Integer id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("newPassword");
        merchantSubAccountService.updateSubAccountPassword(id, newPassword);
        return Result.success("密码重置成功", null);
    }

    @DeleteMapping("/sub-accounts/{id}")
    public Result<Void> deleteSubAccount(@RequestAttribute("userId") Integer userId, @PathVariable Integer id) {
        merchantSubAccountService.deleteSubAccount(id);
        return Result.success("子账号删除成功", null);
    }

    @GetMapping("/operation-logs")
    public Result<List<MerchantOperationLog>> getOperationLogs(@RequestAttribute("userId") Integer userId,
                                                               @RequestParam(required = false) String operationType,
                                                               @RequestParam(required = false) String startTime,
                                                               @RequestParam(required = false) String endTime) {
        List<MerchantOperationLog> logs = merchantOperationLogService.getLogsByMerchantId(userId, operationType, startTime, endTime);
        return Result.success(logs);
    }
}
