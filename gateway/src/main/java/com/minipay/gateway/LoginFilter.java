package com.minipay.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class LoginFilter implements GlobalFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // 排除不需要拦截的请求
        if (path.equals("/admin")) {
            LOG.info("控台端不需要登录验证：{}", path);
            // 继续执行下一个过滤器
            return chain.filter(exchange);
        } else if (path.equals("/admin")) {
            LOG.info("该请求不需要登录验证：{}", path);
        } else {
            LOG.info("该请求需要登录验证：{}", path);
        }
        // 获取当前token
        String token = exchange.getRequest().getHeaders().getFirst("token");
        LOG.info("当前请求的token为：{}", token);
        // 校验token
        if (token == null || token.isEmpty()) {
            LOG.info( "token为空，请求被拦截" );
            // 返回401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            // 结束请求处理
            return exchange.getResponse().setComplete();
        }
        boolean validate = JwtUtil.validate(token);
        if (!validate) {
            LOG.info( "token无效，请求被拦截" );
            // 返回401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            // 结束请求处理
            return exchange.getResponse().setComplete();
        } else {
            LOG.info( "token有效，请求继续" );
            // 继续执行下一个过滤器
            return chain.filter(exchange);
        }
    }
}
