package com.wanxin.consumer.agent;

import com.wanxin.api.account.model.AccountDTO;
import com.wanxin.api.account.model.AccountRegisterDTO;
import com.wanxin.common.domain.BusinessException;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.RestResponse;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程调用统一账户中心微服务
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@FeignClient(
        value = "account-service",
        fallback = AccountApiAgentFallback.class,
        configuration = {AccountApiAgentConfiguration.class})
public interface AccountApiAgent {
    /**
     * 用户注册
     *
     * @param accountRegisterDTO 用户注册信息
     * @return
     */
    @Hmily
    @PostMapping(value = "/account/l/accounts")
    RestResponse<AccountDTO> register(@RequestBody AccountRegisterDTO accountRegisterDTO);
}

class AccountApiAgentConfiguration {
    @Bean
    public AccountApiAgentFallback accountApiAgentFallback() {
        return new AccountApiAgentFallback();
    }
}

class AccountApiAgentFallback implements AccountApiAgent {
    @Override
    public RestResponse<AccountDTO> register(AccountRegisterDTO accountRegisterDTO) {
//        throw new BusinessException(CommonErrorCode.E_999995);
        return new RestResponse<AccountDTO>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }
}
