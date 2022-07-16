package com.wanxin.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 配置Spring Security OAuth2采用jwt令牌方式
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Configuration
public class JWTConfig {

    private String SIGNING_KEY = "wanxin123";

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 对称秘钥, 资源服务器使用该秘钥来解密
        converter.setSigningKey(SIGNING_KEY);
        converter.setAccessTokenConverter(new ClientDefaultAccessTokenConverter());
        return converter;
    }

}
