package com.minipay.payment.controller;

import com.minipay.common.req.OrderReq;
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
    public CommonResp<Payment> createPayment(@RequestBody OrderReq req) {
        BigDecimal amount = new BigDecimal(req.getAmount().toString());
        Payment payment = paymentService.createpayment(amount);
        return new CommonResp<>(200, "创建成功", payment, true);
    }

    // 根据orderId查询支付订单
    @GetMapping("/{orderId}")
    public CommonResp<Payment> getPaymentByOrderId(@PathVariable("orderId") String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return paymentResult(payment);
    }

    // 根据paymentId查询支付订单
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

    // 统一支付结果判断
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