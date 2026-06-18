package com.minipay.order.controller;

import com.minipay.common.req.OrderReq;
import com.minipay.common.resp.CommonResp;
import com.minipay.order.model.Order;
import com.minipay.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping
    public CommonResp<Order> createOrder(@RequestBody OrderReq req) {
        Order order = orderService.createOrder(BigDecimal.valueOf(req.getAmount()), "");
        return new CommonResp<>(200, "创建成功", order, true);
    }

    @GetMapping("/{orderId}")
    public CommonResp<Order> getOrder(@PathVariable("orderId") String orderId) {
        Order order = orderService.getOrder(orderId);
        if (order == null) {
            return new CommonResp<>(404, "订单不存在", null, false);
        }
        return new CommonResp<>(200, "查询成功", order, true);
    }

    @GetMapping("/health")
    public CommonResp<String> health() {
        String result = orderService.health();
        return new CommonResp<>(200, "success", result, true);
    }
}