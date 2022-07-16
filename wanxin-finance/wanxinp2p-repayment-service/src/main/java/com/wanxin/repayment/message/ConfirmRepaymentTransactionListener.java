package com.wanxin.repayment.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.repayment.entity.RepaymentPlan;
import com.wanxin.repayment.mapper.RepaymentPlanMapper;
import com.wanxin.repayment.service.RepaymentService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 确认还款-消息生产者本地事务监听器
 *
 * @Author Administrator
 * @Date 2022/6/29 17:19
 */
@Component
@RocketMQTransactionListener(txProducerGroup = "PID_CONFIRM_REPAYMENT")
public class ConfirmRepaymentTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;

    /**
     * 执行本地事务
     *
     * @param message
     * @param o
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        //1、解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) message.getPayload()));
        RepaymentPlan repaymentPlan = JSONObject.parseObject(jsonObject.getString("repaymentPlan"), RepaymentPlan.class);
        RepaymentRequest repaymentRequest = JSONObject.parseObject(jsonObject.getString("repaymentRequest"), RepaymentRequest.class);

        //2、执行本地事务
        Boolean result = repaymentService.confirmRepayment(repaymentPlan, repaymentRequest);

        //3、返回结果
        if (result) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 执行事务回查
     *
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        //1、解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) message.getPayload()));
        RepaymentPlan repaymentPlan = JSONObject.parseObject(jsonObject.getString("repaymentPlan"), RepaymentPlan.class);

        //2、回查事务状态
        RepaymentPlan newRepaymentPlan = repaymentPlanMapper.selectById(repaymentPlan.getId());

        //3、返回结果
        if (newRepaymentPlan != null && "1".equals(newRepaymentPlan.getRepaymentStatus())) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
