package com.wanxin.repayment.message;

import com.alibaba.fastjson.JSONObject;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.repayment.entity.RepaymentPlan;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 确认还款-消息生产者
 *
 * @Author Administrator
 * @Date 2022/6/29 16:36
 */
@Component
public class RepaymentProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest) {
        //1、构造消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repaymentPlan", repaymentPlan);
        jsonObject.put("repaymentRequest", repaymentRequest);

        Message<String> msg = MessageBuilder.withPayload(jsonObject.toJSONString()).build();

        //2、发送消息
        rocketMQTemplate.sendMessageInTransaction("PID_CONFIRM_REPAYMENT",
                "TP_CONFIRM_REPAYMENT", msg, null);
    }
}
