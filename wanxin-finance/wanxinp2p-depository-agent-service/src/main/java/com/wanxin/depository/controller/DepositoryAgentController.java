package com.wanxin.depository.controller;

import com.alibaba.fastjson.JSONObject;
import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.RechargeRequest;
import com.wanxin.api.consumer.model.WithdrawRequest;
import com.wanxin.api.dipository.DepositoryAgentAPI;
import com.wanxin.api.dipository.model.DepositoryBaseResponse;
import com.wanxin.api.dipository.model.DepositoryResponseDTO;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.api.dipository.model.LoanRequest;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.api.transaction.model.ModifyProjectStatusDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.depository.service.DepositoryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 存管代理服务
 *
 * @Author Administrator
 * @Date 2022/6/13 23:13
 */
@Api(value = "存管代理服务", tags = "depository-agent")
@RestController
public class DepositoryAgentController implements DepositoryAgentAPI {

    @Autowired
    private DepositoryRecordService depositoryRecordService;

    @Override
    @PostMapping("l/confirm-repayment")
    @ApiOperation(value = "确认还款")
    @ApiImplicitParam(name = "repaymentRequest", value = "还款信息", required = true, dataType = "RepaymentRequest", paramType = "body")
    public RestResponse<String> confirmRepayment(@RequestBody RepaymentRequest repaymentRequest) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = depositoryRecordService.confirmRepayment(repaymentRequest);
        return getRestResponse(depositoryResponse);
    }

    @Override
    @PostMapping("l/modify-project-status")
    @ApiOperation(value = "修改标的状态")
    @ApiImplicitParam(name = "modifyProjectStatusDTO", value = "修改标的状态DTO", required = true, dataType = "ModifyProjectStatusDTO", paramType = "body")
    public RestResponse<String> modifyProjectStatus(@RequestBody ModifyProjectStatusDTO modifyProjectStatusDTO) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = depositoryRecordService.modifyProjectStatus(modifyProjectStatusDTO);
        return getRestResponse(depositoryResponse);
    }

    @Override
    @PostMapping("l/confirm-loan")
    @ApiOperation(value = "审核标的满标放款")
    @ApiImplicitParam(name = "loanRequest", value = "标的满标放款信息", required = true, dataType = "LoanRequest", paramType = "body")
    public RestResponse<String> confirmLoan(@RequestBody LoanRequest loanRequest) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = depositoryRecordService.confirmLoan(loanRequest);
        return getRestResponse(depositoryResponse);
    }

    @Override
    @PostMapping("/l/user-auto-pre-transaction")
    @ApiOperation(value = "预授权处理(写记录，冻结金额)")
    @ApiImplicitParam(name = "userAutoPreTransactionRequest", value = "平台向存管系统发送标的信息", required = true, dataType = "UserAutoPreTransactionRequest", paramType = "body")
    public RestResponse<String> userAutoPreTransaction(@RequestBody UserAutoPreTransactionRequest userAutoPreTransactionRequest) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = depositoryRecordService.userAutoPreTransaction(userAutoPreTransactionRequest);
        return getRestResponse(depositoryResponse);
    }

    @Override
    @PostMapping("/l/create-project")
    @ApiOperation(value = "保存标的信息(向银行存管系统发送标的信息)")
    @ApiImplicitParam(name = "projectDTO", value = "向银行存管系统发送标的信息", required = true, dataType = "ProjectDTO", paramType = "body")
    public RestResponse<String> createProject(@RequestBody ProjectDTO projectDTO) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = depositoryRecordService.createProject(projectDTO);
        RestResponse<String> restResponse = new RestResponse<String>();
        DepositoryBaseResponse depositoryBaseResponse = JSONObject.parseObject(JSONObject.toJSONString(depositoryResponse.getRespData()), DepositoryBaseResponse.class);
        restResponse.setResult(depositoryBaseResponse.getRespCode());
        restResponse.setMsg(depositoryBaseResponse.getRespMsg());
        return restResponse;
    }

    @Override
    @PostMapping("/l/withdraws")
    @ApiOperation("生成提现请求数据")
    @ApiImplicitParam(name = "withdrawRequest", value = "提现信息", required = true, dataType = "WithdrawRequest", paramType = "body")
    public RestResponse<GatewayRequest> createWithdrawRecord(@RequestBody WithdrawRequest withdrawRequest) {
        return RestResponse.success(depositoryRecordService.withdrawRecords(withdrawRequest));
    }

    @Override
    @PostMapping("/l/recharges")
    @ApiOperation("生成充值请求数据")
    @ApiImplicitParam(name = "rechargeRequest", value = "充值信息", required = true, dataType = "RechargeRequest", paramType = "body")
    public RestResponse<GatewayRequest> createRechargeRecord(@RequestBody RechargeRequest rechargeRequest) {
        return RestResponse.success(depositoryRecordService.rechargeRecords(rechargeRequest));
    }

    @Override
    @PostMapping("/l/consumers")
    @ApiOperation("生成开户请求数据")
    @ApiImplicitParam(name = "consumerRequest", value = "开户信息", required = true, dataType = "ConsumerRequest", paramType = "body")
    public RestResponse<GatewayRequest> createConsumer(@RequestBody ConsumerRequest consumerRequest) {
        return RestResponse.success(depositoryRecordService.createConsumer(consumerRequest));
    }

    /**
     * 统一处理响应信息
     *
     * @param depositoryResponse
     * @return
     */
    private RestResponse<String> getRestResponse(DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse) {
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setResult(depositoryResponse.getRespData().getRespCode());
        restResponse.setMsg(depositoryResponse.getRespData().getRespMsg());
        return restResponse;
    }
}
