package com.minipay.common.req;

public class PaymentReq {
    private String orderId;
    private Long amount;
    private String payType;  // 支付方式: alipay/wechat

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "PaymentReq{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", payType='" + payType + '\'' +
                '}';
    }
}
