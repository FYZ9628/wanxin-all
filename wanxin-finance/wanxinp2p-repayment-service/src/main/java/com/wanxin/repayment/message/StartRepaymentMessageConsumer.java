package com.wanxin.repayment.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.repayment.service.RepaymentService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 开始还款-消息消费者
 *
 * @Author Administrator
 * @Date 2022/6/29 10:43
 */
@Component
@RocketMQMessageListener(topic = "TP_START_REPAYMENT", consumerGroup = "CID_START_REPAYMENT")
public class StartRepaymentMessageConsumer implements RocketMQListener<String> {

    @Autowired
    private RepaymentService repaymentService;

    @Override
    public void onMessage(String msg) {
        System.out.println("消费消息：" + msg);
        //1、解析消息
        final JSONObject jsonObject = JSON.parseObject(msg);
        ProjectWithTendersDTO projectWithTendersDTO =
                JSONObject.parseObject(jsonObject.getString("projectWithTendersDTO"), ProjectWithTendersDTO.class);

        //2、调用业务层执行本地事务
        repaymentService.startRepayment(projectWithTendersDTO);
    }
}
