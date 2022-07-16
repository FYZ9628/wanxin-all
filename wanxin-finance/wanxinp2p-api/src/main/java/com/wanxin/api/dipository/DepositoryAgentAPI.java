package com.wanxin.api.dipository;

import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.RechargeRequest;
import com.wanxin.api.consumer.model.WithdrawRequest;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.api.dipository.model.LoanRequest;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.api.transaction.model.ModifyProjectStatusDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.RestResponse;

/**
 * 银行存管系统代理服务API
 *
 * @Author Administrator
 * @Date 2022/6/13 23:10
 */
public interface DepositoryAgentAPI {

    /**
     * 还款确认
     *
     * @param repaymentRequest 还款信息
     * @return
     */
    RestResponse<String> confirmRepayment(RepaymentRequest repaymentRequest);

    /**
     * 修改标的状态
     *
     * @param modifyProjectStatusDTO
     * @return
     */
    RestResponse<String> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO);

    /**
     * 审核标的满标放款
     *
     * @param loanRequest
     * @return
     */
    RestResponse<String> confirmLoan(LoanRequest loanRequest);

    /**
     * 预授权处理
     *
     * @param userAutoPreTransactionRequest 预授权处理信息
     * @return
     */
    RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest);

    /**
     * 保存标的信息(向银行存管系统发送标的信息)
     *
     * @param projectDTO 标的信息
     * @return 返回提示信息
     */
    RestResponse<String> createProject(ProjectDTO projectDTO);

    /**
     * 生成用户提现数据
     *
     * @param withdrawRequest
     * @return
     */
    RestResponse<GatewayRequest> createWithdrawRecord(WithdrawRequest withdrawRequest);

    /**
     * 生成用户充值数据
     *
     * @param rechargeRequest
     * @return
     */
    RestResponse<GatewayRequest> createRechargeRecord(RechargeRequest rechargeRequest);

    /**
     * 开通存管账户
     *
     * @param consumerRequest 开户信息
     * @return
     */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);
}
