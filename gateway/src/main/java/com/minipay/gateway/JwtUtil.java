package com.minipay.gateway;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.GlobalBouncyCastleProvider;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

/**
 *  JWT工具类，负责生成和验证JWT令牌
 *  1. generateToken(String userId): 根据用户ID生成JWT令牌
 *  2. validateToken(String token): 验证JWT令牌的有效性
 */
public class JwtUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JwtUtil.class);
    /**
     * 盐值
     */
    private static final String KEY_CN = "原神牛逼";
    private static final String KEY = PinyinUtil.getPinyin(KEY_CN, "").replace(" ", "");

    private static String createToken(Long userId) {
        DateTime nowTime = new DateTime();
        DateTime expTime = nowTime.offsetNew(DateField.HOUR, 24);
        LOG.info("开始生成JWT令牌，用户id{}", userId);
        Map<String, Object> paymap = new HashMap<>();
        // 设置用户ID
        paymap.put("userId", userId);
        // 设置创建时间
        paymap.put("createTime", nowTime);
        // 过期时间
        paymap.put(JWTPayload.EXPIRES_AT, expTime);
        // 生效时间
        paymap.put(JWTPayload.NOT_BEFORE, nowTime);
        // 使用HMAC算法和盐值生成JWT令牌
        String token = JWTUtil.createToken(paymap, KEY.getBytes());
        LOG.info("成功生成JWT token：{}", token);
        return token;
    }

    public static boolean validate(String token){
        try {
            LOG.info("开始JWT token校验，token：{}", token);
            GlobalBouncyCastleProvider.setUseBouncyCastle(false);
            JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes());
            // validate包含了verify
            boolean validate = jwt.validate(0);
            LOG.info("JWT token校验结果：{}", validate);
            return validate;
        } catch (Exception e){
            LOG.error("JWT token校验异常", e);
            return false;
        }
    }
}
