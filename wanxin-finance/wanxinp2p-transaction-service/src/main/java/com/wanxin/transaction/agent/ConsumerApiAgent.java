package com.wanxin.transaction.agent;

import com.wanxin.api.consumer.model.BalanceDetailsDTO;
import com.wanxin.api.consumer.model.ConsumerDTO;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Administrator
 * @Date 2022/6/21 23:47
 */
@FeignClient(
        value = "consumer-service",
        fallback = ConsumerApiAgentFallback.class,
        configuration = {ConsumerApiAgentConfiguration.class})
public interface ConsumerApiAgent {
    /**
     * 获取用户余额信息
     *
     * @param userNo 用户流水号
     * @return
     */
    @GetMapping("/consumer/l/balances/{userNo}")
    RestResponse<BalanceDetailsDTO> getBalance(@PathVariable("userNo") String userNo);

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
//    @GetMapping("/consumer/l/currConsumer/{mobile}")
    @GetMapping("/consumer/l/currConsumer")
    RestResponse<ConsumerDTO> getCurrConsumer();

}

class ConsumerApiAgentConfiguration {
    @Bean
    public ConsumerApiAgentFallback consumerApiAgentFallback() {
        return new ConsumerApiAgentFallback();
    }
}

class ConsumerApiAgentFallback implements ConsumerApiAgent {
    @Override
    public RestResponse<BalanceDetailsDTO> getBalance(String userNo) {
        return new RestResponse<BalanceDetailsDTO>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }

    @Override
    public RestResponse<ConsumerDTO> getCurrConsumer() {
        return new RestResponse<ConsumerDTO>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }
}