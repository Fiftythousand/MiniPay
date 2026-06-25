package com.minipay.common.exception;

public enum MinipayExceptionEnum {
    USER_MOBILE_EXIST("手机号已注册"),
    USER_MOBILE_NOT_EXIST("请先获取短信验证码"),
    USER_MOBILE_CODE_ERROR("短信验证码错误"),
    ;

    private String desc;

    MinipayExceptionEnum(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }
}
