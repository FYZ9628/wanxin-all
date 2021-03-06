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
     * ????????????
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

    @Bean //pre???????????????????????????????????????????????????
    public ZuulFilter sentinelZuulPreFilter() {
        return new SentinelZuulPreFilter();
    }
    @Bean //post????????????????????????????????????
    public ZuulFilter sentinelZuulPostFilter() {
        return new SentinelZuulPostFilter();
    }
    @Bean //error??????????????????????????????
    public ZuulFilter sentinelZuulErrorFilter() {
        return new SentinelZuulErrorFilter();
    }

    /**
     * ???????????????????????????????????????
     */
    @PostConstruct
    public void doInit() {
//        initGatewayRules();
        //??????FallbackProvider
        ZuulBlockFallbackManager.registerProvider(new UaaBlockFallBackProvider());
    }

    /**
     * ??????????????????
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        /**
         * resource: ????????????????????????????????????route??????????????????????????????API????????????
         * count???????????????
         * IntervalSec????????????????????????????????????????????????1???
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
        //????????????????????????
        GatewayRuleManager.loadRules(rules);
    }

    /**
     * ????????? nacos ?????????
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
