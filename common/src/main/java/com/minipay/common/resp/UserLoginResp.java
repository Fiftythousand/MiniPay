package com.minipay.common.resp;

import lombok.Data;

@Data
public class UserLoginResp {
    private Long id;

    private String mobile;

    private String token;
}
