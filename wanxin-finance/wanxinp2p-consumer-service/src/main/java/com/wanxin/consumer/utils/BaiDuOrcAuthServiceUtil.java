package com.wanxin.consumer.utils;

import com.wanxin.common.domain.BusinessException;
import com.wanxin.consumer.common.ConsumerErrorCode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>
 * 获取百度云ORC接口token
 * 官方使用教程：
 * 获取token：https://ai.baidu.com/ai-doc/OCR/Ck3h7y2ia
 * 身份证识别：https://ai.baidu.com/ai-doc/OCR/rk3h7xzck
 * </p>
 *
 * @author yuelimin
 * @since 1.8
 */
public class BaiDuOrcAuthServiceUtil {
    private static final String AK = "KsPM4GPq72TAt2aFnyEuMBnR";
    private static final String SK = "lG15XUBs6i42cmObrd2quScofsDUrP0C";

    /**
     * 获取API访问token
     * 该token有一定的有效期, 需要自行管理, 当失效时需重新获取.
     *
     * @return
     */
    public static String getAuth() {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + AK
                + "&client_secret=" + SK;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 定义 BufferedReader 输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.getString("access_token");
        } catch (Exception e) {
            throw new BusinessException(ConsumerErrorCode.E_140161);
        }
    }
}

