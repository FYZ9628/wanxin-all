package com.wanxin.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public class CommonUtil {

    public static String hiddenMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return "";
        }
        //11位的数据，分为三部分，第一部分三位，第二部分4位，第三部分4位，第二部分用****替换
        //例子：18278539578，替换后：182****9578
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

}

