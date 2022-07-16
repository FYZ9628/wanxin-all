package com.wanxin.consumer.agent;

import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.RechargeRequest;
import com.wanxin.api.consumer.model.WithdrawRequest;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程调用银行存管代理微服务
 *
 * @Author Administrator
 * @Date 2022/6/13 23:22
 */

@FeignClient(value = "depository-agent-service")
public interface DepositoryAgentApiAgent {

    /**
     * 生成提现请求数据
     *
     * @param withdrawRequest
     * @return
     */
    @PostMapping("/depository/l/withdraws")
    RestResponse<GatewayRequest> createWithdrawRecord(@RequestBody WithdrawRequest withdrawRequest);

    /**
     * 生成充值请求数据
     *
     * @param rechargeRequest
     * @return
     */
    @PostMapping("/depository/l/recharges")
    RestResponse<GatewayRequest> createRechargeRecord(@RequestBody RechargeRequest rechargeRequest);

    /**
     * 生成用户数据远程调用（开户）
     *
     * @param consumerRequest
     * @return
     */
    @PostMapping("/depository/l/consumers")
    RestResponse<GatewayRequest> createConsumer(@RequestBody ConsumerRequest consumerRequest);
}
