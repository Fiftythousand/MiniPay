package com.minipay.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minipay.payment.mapper.PaymentMapper;
import com.minipay.payment.model.Payment;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    @Resource
    private PaymentMapper paymentMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String ORDER_SERVICE_URL = "http://localhost:8081/api/orders";

    public Payment createPayment(String orderId, BigDecimal amount) {
        LOG.info("创建支付订单, orderId: {}, amount: {}", orderId, amount);

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString().replace("-", ""));
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        payment = simulatePayment(payment);

        updateOrderStatus(orderId, payment.getStatus());

        return payment;
    }

    private Payment simulatePayment(Payment payment) {
        Random random = new Random();
        int result = random.nextInt(100);

        if (result < 80) {
            payment.setStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());
            LOG.info("支付成功, paymentId: {}", payment.getPaymentId());
        } else {
            payment.setStatus("FAILED");
            payment.setPaidAt(LocalDateTime.now());
            LOG.info("支付失败, paymentId: {}", payment.getPaymentId());
        }

        paymentMapper.updateById(payment);
        return payment;
    }

    private void updateOrderStatus(String orderId, String paymentStatus) {
        try {
            String orderStatus = "SUCCESS".equals(paymentStatus) ? "PAID" : paymentStatus;
            String url = ORDER_SERVICE_URL + "/" + orderId + "/status";
            Map<String, String> request = new HashMap<>();
            request.put("status", orderStatus);

            LOG.info("通知订单服务更新状态, orderId: {}, status: {}", orderId, orderStatus);
            restTemplate.put(url, request);
        } catch (Exception e) {
            LOG.error("通知订单服务失败, orderId: {}, error: {}", orderId, e.getMessage());
        }
    }

    public Payment getPaymentByOrderId(String orderId) {
        LOG.info("查询支付订单, orderId: {}", orderId);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        wrapper.orderByDesc(Payment::getCreatedAt);
        List<Payment> payments = paymentMapper.selectList(wrapper);
        return payments.isEmpty() ? null : payments.get(0);
    }

    public Payment getPaymentByPaymentId(String paymentId) {
        LOG.info("查询支付订单, paymentId: {}", paymentId);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getPaymentId, paymentId);
        return paymentMapper.selectOne(wrapper);
    }

    public String health() {
        LOG.info("支付服务健康检查");
        return "payment-service is running";
    }
}