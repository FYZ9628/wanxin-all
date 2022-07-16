package com.wanxin.depository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.RechargeRequest;
import com.wanxin.api.consumer.model.WithdrawRequest;
import com.wanxin.api.dipository.model.DepositoryBaseResponse;
import com.wanxin.api.dipository.model.DepositoryResponseDTO;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.api.dipository.model.LoanRequest;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.api.transaction.model.ModifyProjectStatusDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.depository.entity.DepositoryRecord;

/**
 * @Author Administrator
 * @Date 2022/6/15 20:25
 */
public interface DepositoryRecordService extends IService<DepositoryRecord> {

    /**
     * 还款确认
     *
     * @param repaymentRequest
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> confirmRepayment(RepaymentRequest repaymentRequest);

    /**
     * 修改标的状态
     *
     * @param modifyProjectStatusDTO
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO);

    /**
     * 审核满标放款
     * 扣款：投资人投标花的钱，或者借款人还款
     * 入账：借款人接到的钱，或者投资人从借款人处拿到的还款金额
     *
     * @param loanRequest
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> confirmLoan(LoanRequest loanRequest);

    /**
     * 投标预处理/用户还款预处理
     * 就是往存管代理系统的 depository_record表（存管交易记录表）写一条数据
     * 并且做了幂等处理（不会存在重复的数据，即使是程序运行过程中因网络中断导致的程序异常，恢复正常后也是先查表，查不到才会生成新数据）
     * 银行的存管系统那边则会冻结对应的账户（借款人还款时或者投资人投标时）相应金额
     * 只冻结，还没有真正的完成扣款
     *
     * @param userAutoPreTransactionRequest
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest);

    /**
     * 保存标的信息
     *
     * @param projectDTO 标的信息
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> createProject(ProjectDTO projectDTO);

    /**
     * 生成用户提现数据
     *
     * @param withdrawRequest
     * @return
     */
    GatewayRequest withdrawRecords(WithdrawRequest withdrawRequest);

    /**
     * 生成用户充值数据
     *
     * @param rechargeRequest
     * @return
     */
    GatewayRequest rechargeRecords(RechargeRequest rechargeRequest);

    /**
     * 根据请求流水号更新请求状态
     *
     * @param requestNo
     * @param requestsStatus
     * @return
     */
    Boolean modifyRequestStatus(String requestNo, Integer requestsStatus);

    /**
     * 开通存管账户
     *
     * @param consumerRequest 开户信息
     * @return
     */
    GatewayRequest createConsumer(ConsumerRequest consumerRequest);
}
