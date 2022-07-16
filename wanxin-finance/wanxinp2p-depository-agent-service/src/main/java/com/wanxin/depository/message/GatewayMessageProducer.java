package com.wanxin.depository.message;

import com.wanxin.api.dipository.model.DepositoryConsumerResponse;
import com.wanxin.api.dipository.model.DepositoryRechargeResponse;
import com.wanxin.api.dipository.model.DepositoryWithdrawResponse;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * （开户、充值、提现）-消息生产者
 * 发送消息给用户中心服务
 *
 * @Author Administrator
 * @Date 2022/6/17 19:29
 */
@Component
public class GatewayMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 提现
     *
     * @param response
     */
    public void personalWithdraw(DepositoryWithdrawResponse response) {
        rocketMQTemplate.convertAndSend("TP_GATEWAY_NOTIFY_AGENT:WITHDRAW", response);
    }

    /**
     * 充值
     *
     * @param response
     */
    public void personalRecharge(DepositoryRechargeResponse response) {
        rocketMQTemplate.convertAndSend("TP_GATEWAY_NOTIFY_AGENT:RECHARGE", response);
    }

    /**
     * 开户
     * 存管代理服务中心给用户中心发送的消息
     *
     * @param response
     */
    public void personalRegister(DepositoryConsumerResponse response) {
        rocketMQTemplate.convertAndSend("TP_GATEWAY_NOTIFY_AGENT:PERSONAL_REGISTER", response);
    }

}
