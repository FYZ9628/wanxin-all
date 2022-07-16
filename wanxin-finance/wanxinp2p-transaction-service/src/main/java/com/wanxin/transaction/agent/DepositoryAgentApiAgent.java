package com.wanxin.transaction.agent;

import com.wanxin.api.dipository.model.LoanRequest;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.transaction.model.ModifyProjectStatusDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Administrator
 * @Date 2022/6/22 23:16
 */
@FeignClient(value = "depository-agent-service",
        fallback = DepositoryAgentApiAgentFallback.class,
        configuration = {DepositoryAgentApiAgentConfiguration.class})
public interface DepositoryAgentApiAgent {

    /**
     * 修改标的状态
     *
     * @param modifyProjectStatusDTO
     * @return
     */
    @PostMapping("/depository/l/modify-project-status")
    RestResponse<String> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO);

    /**
     * 放款
     *
     * @param loanRequest
     * @return
     */
    @PostMapping("/depository/l/confirm-loan")
    RestResponse<String> confirmLoan(LoanRequest loanRequest);

    /**
     * 预处理冻结远程调用
     *
     * @param userAutoPreTransactionRequest 预处理信息
     * @return
     */
    @PostMapping("/depository/l/user-auto-pre-transaction")
    RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest);

    /**
     * 保存标的信息(向银行存管系统发送标的信息)
     *
     * @param projectDTO 标的信息
     * @return 提示信息
     */
    @PostMapping("/depository/l/create-project")
    RestResponse<String> createProject(@RequestBody ProjectDTO projectDTO);
}

class DepositoryAgentApiAgentConfiguration {
    @Bean
    public DepositoryAgentApiAgentFallback depositoryAgentApiAgentFallback() {
        return new DepositoryAgentApiAgentFallback();
    }
}

class DepositoryAgentApiAgentFallback implements DepositoryAgentApiAgent {

    @Override
    public RestResponse<String> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO) {
        return new RestResponse<String>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }

    @Override
    public RestResponse<String> confirmLoan(LoanRequest loanRequest) {
        return new RestResponse<String>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }

    @Override
    public RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest) {
        return new RestResponse<String>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }

    @Override
    public RestResponse<String> createProject(ProjectDTO projectDTO) {
        return new RestResponse<String>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }
}
