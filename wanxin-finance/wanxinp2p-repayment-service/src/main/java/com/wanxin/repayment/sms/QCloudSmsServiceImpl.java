package com.wanxin.repayment.sms;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author Administrator
 * @Date 2022/6/30 16:56
 */
@Service
@Slf4j
public class QCloudSmsServiceImpl implements SmsService {

    @Value("${sms.qcloud.appId}")
    private int appId;

    @Value("${sms.qcloud.appKey}")
    private String appKey;

    @Value("${sms.qcloud.templateId}")
    private int templateId;

    @Value("${sms.qcloud.sign}")
    private String sign;

    @Override
    public void sendRepaymentNotify(String mobile, String date, BigDecimal amount) {
        log.info("给手机号{}，发送还款提醒：{},金额:{}",mobile,date,amount);
        System.out.println("【万信金融】尊敬的用户您好，您本次还款日期是" + date + "，本次应还金额是" + amount + "。");
//        SmsSingleSender sender = new SmsSingleSender(appId, appKey);
//        try {
//            SmsSingleSenderResult result = sender.sendWithParam("86", mobile,
//                    templateId, new String[] {date, amount.toString()},sign, "", "");
//        } catch (Exception ex) {
//            log.error("发送失败：{}", ex.getMessage());
//        }
    }
}
