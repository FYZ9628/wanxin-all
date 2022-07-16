package com.wanxin.repayment.sms;

import java.math.BigDecimal;

/**
 * 短信服务
 *
 * @Author Administrator
 * @Date 2022/6/30 16:27
 */
public interface SmsService {

    /**
     * 发送还款短信通知
     *
     * @param mobile 还款人手机号
     * @param date 日期
     * @param amount 应还金额
     */
    void sendRepaymentNotify(String mobile, String date, BigDecimal amount);
}
