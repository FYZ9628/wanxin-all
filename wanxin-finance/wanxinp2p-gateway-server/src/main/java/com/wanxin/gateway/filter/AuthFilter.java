package com.wanxin.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wanxin.common.util.EncryptUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 */
@Component
public class AuthFilter extends ZuulFilter {
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        // 前置过滤器, 可以在请求被路由之前调用（即微服务被调用之前，在网关认证拦截之后）
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

//    /**
//     * 这种方式是将用户信息 token 放到 header 中，则在微服务中也要从 header 中取出来
//     *
//     * String jsonToken2 = httpServletRequest.getHeader("jsonToken");
//     *
//     * @return
//     */
//    @Override
//    public Object run() {
//        // 获得请求的上下文
//        RequestContext requestContext = RequestContext.getCurrentContext();
//        // 获取Spring Security OAuth2的认证信息对象
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication instanceof OAuth2Authentication)) {
//            // 无token访问网关内资源, 直接返回null
//            return null;
//        }
//
//        // 将当前登录的用户以及接入客户端的信息放入Map中
//        OAuth2Authentication oauth2Authentication = (OAuth2Authentication) authentication;
//        Map<String, String> jsonToken = new HashMap<>(oauth2Authentication.getOAuth2Request().getRequestParameters());
//        if (jsonToken.get("mobile") != null) {
//            requestContext.addZuulRequestHeader("jsonToken", EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));
//        }
//        return null;
//    }

    /**
     * 判断token是否为空，不为空则将用户信息从 OAuth2 取出并存到上下文中，然后传递给微服务
     *
     * 将数据放到 parameter 参数中才可以传到 controller中的请求参数中，
     * 若放到 header 中则传不到controller中的请求参数中
     * 上面的 run() 方法中用到的 requestContext.addZuulRequestHeader() 是错误的
     * @return
     */
    @Override
    public Object run() {
        //1.获取Spring Security OAuth2的认证信息对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //从Security上下文中拿到用户身份对象
        if(authentication==null || !(authentication instanceof OAuth2Authentication)){
            return null;// 无token访问网关内资源，直接返回null
        }
        //2.将当前登录的用户以及接入客户端的信息放入Map中
        OAuth2Authentication oauth2Authentication=(OAuth2Authentication)authentication;
        Map<String,String> jsonToken = new HashMap<>
                (oauth2Authentication.getOAuth2Request().getRequestParameters());
        /*3.将jsonToken写入转发微服务的request中，这样微服务就能通过
            httpServletRequest.getParameter("jsonToken");获取到了*/

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        request.getParameterMap();// 关键步骤，一定要get一下，下面这行代码才能取到值
        Map<String, List<String>> requestQueryParams = ctx.getRequestQueryParams();
        if (requestQueryParams == null) {
            requestQueryParams = new HashMap<>();
        }

        List<String> arrayList = new ArrayList<>();
        arrayList.add(EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));
        requestQueryParams.put("jsonToken", arrayList);
        ctx.setRequestQueryParams(requestQueryParams);//放到请求参数中，而不是 header中
        return null;
    }
}
