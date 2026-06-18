package com.minipay.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minipay.order.mapper.OrderMapper;
import com.minipay.order.model.Order;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    @Resource
    private OrderMapper orderMapper;

    // 创建订单
    public Order createOrder(BigDecimal amount, String description) {
        Order order = new Order();
        // 使用 UUID 生成唯一的订单 ID，并去掉其中的连字符
        order.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        order.setAmount(amount);
        order.setDescription(description);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);
        LOG.info("创建订单");
        return order;
    }

    // 查询订单
    public Order getOrder(String orderId) {
        LOG.info("查询订单, orderId: {}", orderId);
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 来构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderId, orderId);
        return orderMapper.selectOne(wrapper);
    }

    // 订单服务健康检查
    public String health() {
        LOG.info("订单服务健康检查");
        return "order-service is running";
    }
}