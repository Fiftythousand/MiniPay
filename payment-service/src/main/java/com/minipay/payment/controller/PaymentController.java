package com.minipay.payment.controller;

import com.minipay.common.req.PaymentReq;
import com.minipay.common.resp.CommonResp;
import com.minipay.payment.model.Payment;
import com.minipay.payment.service.PaymentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @PostMapping
    public CommonResp<Payment> createPayment(@RequestBody PaymentReq req) {
        if (req.getOrderId() == null || req.getOrderId().isEmpty()) {
            return new CommonResp<>(400, "订单ID不能为空", null, false);
        }
        if (req.getAmount() == null || req.getAmount() <= 0) {
            return new CommonResp<>(400, "金额必须大于0", null, false);
        }
        
        BigDecimal amount = new BigDecimal(req.getAmount().toString());
        Payment payment = paymentService.createPayment(req.getOrderId(), amount);
        
        String message = "SUCCESS".equals(payment.getStatus()) ? "支付成功" : "支付失败";
        return new CommonResp<>(200, message, payment, true);
    }

    @GetMapping("/{orderId}")
    public CommonResp<Payment> getPaymentByOrderId(@PathVariable("orderId") String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return paymentResult(payment);
    }

    @GetMapping("/detail/{paymentId}")
    public CommonResp<Payment> getPaymentByPaymentId(@PathVariable("paymentId") String paymentId) {
        Payment payment = paymentService.getPaymentByPaymentId(paymentId);
        return paymentResult(payment);
    }

    @GetMapping("/health")
    public CommonResp<String> health() {
        String result = paymentService.health();
        return new CommonResp<>(200, "success", result, true);
    }

    private CommonResp<Payment> paymentResult(Payment payment) {
        if (payment == null) {
            return new CommonResp<>(404, "该支付订单不存在", null, false);
        }
        String status = payment.getStatus();
        if ("SUCCESS".equals(status)) {
            return new CommonResp<>(200, "支付成功", payment, true);
        } else if ("FAILED".equals(status)) {
            return new CommonResp<>(200, "支付失败", payment, true);
        } else {
            return new CommonResp<>(200, "待支付", payment, true);
        }
    }
}