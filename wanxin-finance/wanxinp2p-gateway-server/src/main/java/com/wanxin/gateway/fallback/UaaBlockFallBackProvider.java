package com.wanxin.gateway.fallback;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * 达到限流点时响应的信息
 *
 * @Author Administrator
 * @Date 2022/7/3 2:09
 */
@Slf4j
public class UaaBlockFallBackProvider implements ZuulBlockFallbackProvider {
    @Override
    public String getRoute() {
        return "uaa-service-router";
    }

    @Override
    public BlockResponse fallbackResponse(String route, Throwable throwable) {
        log.error("{} 服务触发限流", route);
        if (throwable instanceof BlockException) {
            return new BlockResponse(429, "服务访问压力过大，请稍后再试。", route);
        } else {
            return new BlockResponse(500, "系统错误，请联系管理员。", route);
        }
    }
}
