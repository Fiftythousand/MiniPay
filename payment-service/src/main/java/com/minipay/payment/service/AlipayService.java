package com.minipay.payment.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.minipay.payment.config.AlipayConfig;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayService {
    private static final Logger LOG = LoggerFactory.getLogger(AlipayService.class);

    @Resource
    private AlipayConfig alipayConfig;

    private AlipayClient alipayClient;

    private AlipayClient getAlipayClient() {
        if (alipayClient == null) {
            String gatewayUrl = alipayConfig.getGatewayUrl().trim();
            if (!gatewayUrl.startsWith("https://")) {
                gatewayUrl = "https://" + gatewayUrl.replaceFirst("http://", "");
            }
            alipayClient = new DefaultAlipayClient(
                gatewayUrl,
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
            );
        }
        return alipayClient;
    }

    public Map<String, String> createPayment(String outTradeNo, String subject, String totalAmount) {
        LOG.info("调用支付宝创建支付, 订单号: {}, 金额: {}", outTradeNo, totalAmount);
        Map<String, String> result = new HashMap<>();

        // 最多重试2次
        for (int i = 0; i < 2; i++) {
            try {
                AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
                request.setNotifyUrl(alipayConfig.getNotifyUrl());
                request.setReturnUrl(alipayConfig.getReturnUrl());
                request.setBizContent(String.format(
                    "{\"out_trade_no\":\"%s\",\"total_amount\":\"%s\",\"subject\":\"%s\"}",
                    outTradeNo, totalAmount, subject
                ));

                LOG.info("发送支付宝API请求 (第{}次)...", i + 1);
                AlipayTradePrecreateResponse response = getAlipayClient().execute(request);
                LOG.info("支付宝API响应: code={}, sub_code={}", response.getCode(), response.getSubCode());

                if (response.isSuccess()) {
                    LOG.info("支付宝预下单成功, qr_code: {}", response.getQrCode());
                    result.put("code", "1");
                    result.put("qr_code", response.getQrCode());
                    result.put("out_trade_no", response.getOutTradeNo());
                    return result;
                } else {
                    LOG.error("支付宝预下单失败: {} - {}", response.getCode(), response.getMsg());
                    result.put("code", "0");
                    result.put("msg", response.getSubMsg() != null ? response.getSubMsg() : response.getMsg());
                    return result;
                }
            } catch (AlipayApiException e) {
                LOG.error("调用支付宝API异常 (第{}次): {}", i + 1, e.getMessage());
                if (i < 1) {
                    LOG.info("等待2秒后重试...");
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                } else {
                    result.put("code", "0");
                    result.put("msg", "网络超时，请稍后重试");
                }
            }
        }
        return result;
    }

    public boolean verifyNotify(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getCharset(),
                alipayConfig.getSignType()
            );
        } catch (AlipayApiException e) {
            LOG.error("验证支付宝签名失败", e);
            return false;
        }
    }
}
