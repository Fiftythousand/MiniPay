package com.minipay.order.controller.feign;

import com.minipay.common.exception.MinipayException;
import com.minipay.common.exception.MinipayExceptionEnum;
import com.minipay.common.req.UserLoginReq;
import com.minipay.common.resp.CommonResp;
import com.minipay.common.resp.UserLoginResp;
import com.minipay.order.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    private CommonResp<UserLoginResp> login(@Valid @RequestBody UserLoginReq req) {
        try {
            UserLoginResp resp = userService.login(req);
            return new CommonResp<>(200, "登录成功", resp, true);
        } catch (MinipayException e) {
            LOG.warn("登录失败: {}", e.getMessage());
            return new CommonResp<>(400, e.getMessage(), null, false);
        }
    }
}
