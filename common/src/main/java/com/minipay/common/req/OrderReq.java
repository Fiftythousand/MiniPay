package com.minipay.common.req;

public class OrderReq {
    private Long memberId;
    private Long amount;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OrderReq{" +
                "memberId=" + memberId +
                ", amount=" + amount +
                '}';
    }
}
