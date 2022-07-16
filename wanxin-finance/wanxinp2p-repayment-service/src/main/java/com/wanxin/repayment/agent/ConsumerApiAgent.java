package com.wanxin.repayment.agent;

import com.wanxin.api.consumer.model.BorrowerDTO;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Administrator
 * @Date 2022/6/30 16:39
 */
@FeignClient(
        value = "consumer-service",
        fallback = ConsumerApiAgentFallback.class,
        configuration = {ConsumerApiAgentConfiguration.class})
public interface ConsumerApiAgent {

    /**
     * 获取借款人用户信息
     *
     * @param id 用户id
     * @return
     */
    @GetMapping(value = "/consumer/l/borrowers/{id}")
    RestResponse<BorrowerDTO> getBorrowerMobile(@PathVariable("id") Long id);
}

class ConsumerApiAgentConfiguration {
    @Bean
    public ConsumerApiAgentFallback consumerApiAgentFallback() {
        return new ConsumerApiAgentFallback();
    }
}

class ConsumerApiAgentFallback implements ConsumerApiAgent {
    @Override
    public RestResponse<BorrowerDTO> getBorrowerMobile(Long id) {
//        throw new BusinessException(CommonErrorCode.E_999995);
        return new RestResponse<BorrowerDTO>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }
}