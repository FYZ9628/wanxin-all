package com.wanxin.repayment.agent;

import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author Administrator
 * @Date 2022/6/29 15:47
 */
@FeignClient(
        value = "depository-agent-service",
        fallback = DepositoryAgentApiAgentFallback.class,
        configuration = {DepositoryAgentApiAgentConfiguration.class})
public interface DepositoryAgentApiAgent {

    /**
     * 确认还款
     *
     * @param repaymentRequest
     * @return
     */
    @PostMapping("/depository/l/confirm-repayment")
    RestResponse<String> confirmRepayment(RepaymentRequest repaymentRequest);

    /**
     * 还款预处理
     *
     * @param userAutoPreTransactionRequest
     * @return
     */
    @PostMapping("/depository/l/user-auto-pre-transaction")
    RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest);
}

class DepositoryAgentApiAgentConfiguration {
    @Bean
    public DepositoryAgentApiAgentFallback depositoryAgentApiAgentFallback() {
        return new DepositoryAgentApiAgentFallback();
    }
}

class DepositoryAgentApiAgentFallback implements DepositoryAgentApiAgent {
    @Override
    public RestResponse<String> confirmRepayment(RepaymentRequest repaymentRequest) {
        return new RestResponse<String>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }

    @Override
    public RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest) {
        return new RestResponse<String>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }
}

