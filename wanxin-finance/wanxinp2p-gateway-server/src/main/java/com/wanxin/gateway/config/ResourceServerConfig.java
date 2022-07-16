package com.wanxin.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 统一管理可访问资源
 *
 * Spring Security OAuth2 资源服务实现，用于网关对接入客户端的权限拦截，可以指定某个接入客户端只允许访问部分微服务
 * 配置哪个微服务是可以被访问的资源
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Configuration
public class ResourceServerConfig {

    public static final String RESOURCE_ID = "wanxin-resource";

    private AuthenticationEntryPoint point = new RestOAuth2AuthExceptionEntryPoint();

    private RestAccessDeniedHandler handler = new RestAccessDeniedHandler();

    /**
     * 统一认证中心 资源拦截
     */
    @Configuration
    @EnableResourceServer
    public class UAAServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
                    .stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/uaa/druid/**").denyAll()
                    .antMatchers("/uaa/**").permitAll();
        }

    }


    /**
     * c端用户服务 资源拦截
     * 主要配置的内容就是定义一些匹配规则，描述某个接入客户端需要什么样的权限才能访问某个微服务
     */
    @Configuration
    @EnableResourceServer
    public class ConsumerServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore)
                    .resourceId(RESOURCE_ID)
                    .stateless(true);

            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/consumer/l/**").denyAll()// l 开头的路径都不允许访问
                    .antMatchers("/consumer/my/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CONSUMER')")
                    .antMatchers("/consumer/m/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_ADMIN')")
                    .antMatchers("/consumer/**").permitAll();//其他的路径都可以访问

            //1. 受保护的c端用户接口(C端用户登录后可访问)
            //Url格式：/服务名称/my/资源名称/*
            //访问方式：需要携带C端用户认证所获取的Access Token才可访问。scope为read且authorities含ROLE_CONSUMER
            //2. 受保护的b端用户接口(B端管理员用户登录后可访问)
            //Url格式：/服务名称/m/资源名称/*
            //访问方式：需要携带B端用户认证所获取的Access Token才可访问。scope为read且authorities含ROLE_ADMIN
            //3. 公开资源
            //Url格式：/服务名称/资源名称/*
            //访问方式：无限制
        }

    }

}
