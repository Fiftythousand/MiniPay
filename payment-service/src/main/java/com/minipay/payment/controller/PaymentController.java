package com.minipay.payment.controller;

import com.minipay.payment.mapper.PaymentMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) {
        // TODO: 发起支付
        return null;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getPayment(@PathVariable String orderId) {
        // TODO: 查询支付状态
        return null;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        // TODO: 健康检查
        return null;
    }
}
