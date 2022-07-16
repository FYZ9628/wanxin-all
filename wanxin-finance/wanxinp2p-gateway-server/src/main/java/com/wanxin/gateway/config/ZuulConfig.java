package com.wanxin.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulErrorFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPostFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPreFilter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.netflix.zuul.ZuulFilter;
import com.wanxin.gateway.fallback.UaaBlockFallBackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @Author Administrator
 * @Date 2022/6/7 21:51
 */
@Configuration
public class ZuulConfig {

    @Value("${spring.cloud.sentinel.datasource.ds.nacos.server-addr}")
    private String remoteAddress;
    @Value("${spring.cloud.sentinel.datasource.ds.nacos.namespace}")
    private String namespaceId;
    @Value("${spring.cloud.sentinel.datasource.ds.nacos.group-id}")
    private String groupId;
    @Value("${spring.cloud.sentinel.datasource.ds.nacos.data-id}")
    private String dataId;

    /**
     * 跨域设置
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(18000L);
        source.registerCorsConfiguration("/**", config);
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean bean = new FilterRegistrationBean(corsFilter);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean //pre过滤器，在请求路由之前进行限流操作
    public ZuulFilter sentinelZuulPreFilter() {
        return new SentinelZuulPreFilter();
    }
    @Bean //post过滤器，路由之后恢复资源
    public ZuulFilter sentinelZuulPostFilter() {
        return new SentinelZuulPostFilter();
    }
    @Bean //error过滤器，异常后的处理
    public ZuulFilter sentinelZuulErrorFilter() {
        return new SentinelZuulErrorFilter();
    }

    /**
     * 容器初始化的时候执行该方法
     */
    @PostConstruct
    public void doInit() {
//        initGatewayRules();
        //注册FallbackProvider
        ZuulBlockFallbackManager.registerProvider(new UaaBlockFallBackProvider());
    }

    /**
     * 网关限流规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        /**
         * resource: 资源名称，可以是网关中的route名称或者用户自定义的API分组名称
         * count：限流阈值
         * IntervalSec：统计时间窗口，单位是秒，默认是1秒
         */
        rules.add(new GatewayFlowRule("uaa-service-router")
                .setCount(2)
                .setIntervalSec(60));
        rules.add(new GatewayFlowRule("consumer-service-router")
                .setCount(2)
                .setIntervalSec(60));
        rules.add(new GatewayFlowRule("account-service-router")
                .setCount(2)
                .setIntervalSec(60));
        //加载网关限流规则
        GatewayRuleManager.loadRules(rules);
    }

    /**
     * 加载在 nacos 的规则
     */
    private void loadMyNamespaceRules() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, remoteAddress);
        properties.put(PropertyKeyConst.NAMESPACE, namespaceId);

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(properties,
                groupId, dataId, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

}
