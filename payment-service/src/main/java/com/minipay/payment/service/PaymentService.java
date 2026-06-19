package com.minipay.payment.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minipay.payment.mapper.PaymentMapper;
import com.minipay.payment.model.Payment;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    @Resource
    private PaymentMapper paymentMapper;

    // 创建订单
    public Payment createpayment(BigDecimal amount) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString().replace("-", ""));
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setPaidAt(LocalDateTime.now().plusMinutes(30));
        paymentMapper.insert(payment);
        LOG.info("创建订单");
        return payment;
    }

    // 使用orderId查询支付订单
    public Payment getPaymentByOrderId(String orderId) {
        LOG.info("查询支付订单, orderId: {}", orderId);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        return paymentMapper.selectOne(wrapper);
    }

    // 使用paymentId查询支付订单
    public Payment getPaymentByPaymentId(String paymentId) {
        LOG.info("查询支付订单, paymentId: {}", paymentId);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getPaymentId, paymentId);
        return paymentMapper.selectOne(wrapper);
    }

    // 订单服务健康检查
    public String health() {
        LOG.info("订单服务健康检查");
        return "payment-service is running";
    }
}