package com.wanxin.common.domain;

/**
 * 错误编码
 *
 * @Author Administrator
 * @Date 2022/6/4 10:33
 */
public interface ErrorCode {

    /**
     * 错误代码
     * @return
     */
    int getCode();

    /**
     * 错误提示信息
     * @return
     */
    String getDesc();
}
