package com.wanxin.consumer.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wanxin.api.account.model.LoginUser;
import com.wanxin.common.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 * 到达controller之前的拦截
 * Token拦截处理
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) {
        //将数据放到 parameter 参数中才可以传到 controller中的请求参数中，若放到 header 中则传不到controller中的请求参数中
        String jsonToken = httpServletRequest.getParameter("jsonToken");
        if (StringUtils.isNotBlank(jsonToken)) {
            LoginUser loginUser = JSON.parseObject(EncryptUtil.decodeUTF8StringBase64(jsonToken),
                            new TypeReference<LoginUser>() {});
            httpServletRequest.setAttribute("jsonToken", loginUser);
        }

        //获取通过 feign 请求发送过来的用户信息 token
        String jsonToken2 = httpServletRequest.getHeader("jsonToken"); //requestContext.addZuulRequestHeader()
        if (StringUtils.isNotBlank(jsonToken2)) {
            LoginUser loginUser = JSON.parseObject(EncryptUtil.decodeUTF8StringBase64(jsonToken2),
                    new TypeReference<LoginUser>() {});
            httpServletRequest.setAttribute("jsonToken", loginUser);
        }
        return true;//返回true则请求可以放行到controller，否则请求就到不了controller
    }
}
