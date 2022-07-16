package com.wanxin.transaction.message;

import com.alibaba.fastjson.JSONObject;
import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.transaction.entity.Project;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 开始还款-消息生产者
 *
 * @Author Administrator
 * @Date 2022/6/29 9:37
 */
@Component
public class TransactionProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void updateProjectStatusAndStartRepayment(Project project, ProjectWithTendersDTO projectWithTendersDTO) {
        //1、构造消息
        JSONObject object = new JSONObject();
        object.put("project", project);
        object.put("projectWithTendersDTO", projectWithTendersDTO);
        Message<String> msg = MessageBuilder.withPayload(object.toJSONString()).build();

        //2、发送消息
        rocketMQTemplate.sendMessageInTransaction("PID_START_REPAYMENT", "TP_START_REPAYMENT", msg, null);
    }
}
