package com.minipay.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {
    
    @Value("${pay.alipay.app-id}")
    private String appId;
    
    @Value("${pay.alipay.private-key}")
    private String privateKey;
    
    @Value("${pay.alipay.alipay-public-key}")
    private String alipayPublicKey;
    
    @Value("${pay.alipay.gateway-url}")
    private String gatewayUrl;
    
    @Value("${pay.alipay.notify-url}")
    private String notifyUrl;
    
    @Value("${pay.alipay.return-url}")
    private String returnUrl;
    
    @Value("${pay.alipay.sign-type}")
    private String signType;
    
    @Value("${pay.alipay.format}")
    private String format;
    
    @Value("${pay.alipay.charset}")
    private String charset;
    
    // Getter methods
    public String getAppId() { return appId; }
    public String getPrivateKey() { return privateKey; }
    public String getAlipayPublicKey() { return alipayPublicKey; }
    public String getGatewayUrl() { return gatewayUrl; }
    public String getNotifyUrl() { return notifyUrl; }
    public String getReturnUrl() { return returnUrl; }
    public String getSignType() { return signType; }
    public String getFormat() { return format; }
    public String getCharset() { return charset; }
}
