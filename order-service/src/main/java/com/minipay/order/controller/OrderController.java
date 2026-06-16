package com.minipay.order.controller;

import com.minipay.order.mapper.OrderMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderMapper orderMapper;

    public OrderController(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        // TODO: 创建订单
        return null;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String orderId) {
        // TODO: 查询订单
        return null;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        // TODO: 健康检查
        return null;
    }
}
