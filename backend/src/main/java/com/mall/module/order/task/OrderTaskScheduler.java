package com.mall.module.order.task;

import com.mall.module.order.service.OrderCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderTaskScheduler {

    @Autowired
    private OrderCenterService orderCenterService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void autoCloseTimeoutOrders() {
        orderCenterService.autoCloseTimeoutOrders();
    }
}