package com.minipay.payment.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payments")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paymentId;

    private String orderId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;
}
