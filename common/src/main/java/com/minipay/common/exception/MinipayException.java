package com.minipay.common.exception;

public class MinipayException extends RuntimeException {

    private MinipayExceptionEnum e;

    public MinipayException(MinipayExceptionEnum e) {
        super(e.getDesc());  // 设置异常消息
        this.e = e;
    }

    public MinipayExceptionEnum getE() {
        return e;
    }

    public void setE(MinipayExceptionEnum e) {
        this.e = e;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
