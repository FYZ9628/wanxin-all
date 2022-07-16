package com.wanxin.repayment.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.repayment.entity.RepaymentPlan;
import com.wanxin.repayment.service.RepaymentService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 确认还款-消息消费者
 *
 * @Author Administrator
 * @Date 2022/6/29 18:23
 */
@Component
@RocketMQMessageListener(topic = "TP_CONFIRM_REPAYMENT", consumerGroup = "CID_CONFIRM_REPAYMENT")
public class ConfirmRepaymentConsumer implements RocketMQListener<String> {

    @Autowired
    private RepaymentService repaymentService;

    /*
    * RocketMQ的默认重试次数为16次，
    * 且默认重试间隔时间为（1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h）
    *
    */

    @Override
    public void onMessage(String msg) {
        //1、解析消息
        JSONObject jsonObject = JSON.parseObject(msg);
        RepaymentPlan repaymentPlan = JSONObject.parseObject(jsonObject.getString("repaymentPlan"), RepaymentPlan.class);
        RepaymentRequest repaymentRequest = JSONObject.parseObject(jsonObject.getString("repaymentRequest"), RepaymentRequest.class);

        //2、执行本地业务
        repaymentService.invokeConfirmRepayment(repaymentPlan, repaymentRequest);
    }
}
