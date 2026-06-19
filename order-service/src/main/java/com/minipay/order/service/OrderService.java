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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    // 允许的状态值
    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList("PENDING", "PAID", "FAILED"));

    @Resource
    private OrderMapper orderMapper;

    // 创建订单
    public Order createOrder(BigDecimal amount, String description) {
        Order order = new Order();
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
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderId, orderId);
        return orderMapper.selectOne(wrapper);
    }

    // 更新订单状态
    public Order updateOrderStatus(String orderId, String status) {
        LOG.info("更新订单状态, orderId: {}, status: {}", orderId, status);
        if (!VALID_STATUSES.contains(status)) {
            LOG.warn("非法状态: {}", status);
            return null;
        }
        Order order = getOrder(orderId);
        if (order == null) {
            LOG.warn("订单不存在, orderId: {}", orderId);
            return null;
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return order;
    }

    // 订单服务健康检查
    public String health() {
        LOG.info("订单服务健康检查");
        return "order-service is running";
    }
}