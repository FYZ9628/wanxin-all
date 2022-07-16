package com.wanxin.depository.controller;

import com.alibaba.fastjson.JSON;
import com.wanxin.api.dipository.model.DepositoryConsumerResponse;
import com.wanxin.api.dipository.model.DepositoryRechargeResponse;
import com.wanxin.api.dipository.model.DepositoryWithdrawResponse;
import com.wanxin.common.util.EncryptUtil;
import com.wanxin.depository.message.GatewayMessageProducer;
import com.wanxin.depository.service.DepositoryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收从银行存管系统发送过来的请求（http请求）
 *
 * @Author Administrator
 * @Date 2022/6/17 19:02
 */
@RestController
@Api(value = "银行存管系统通知服务", tags = "depository-agent")
public class DepositoryNotifyController {

    @Autowired
    private DepositoryRecordService depositoryRecordService;
    @Autowired
    private GatewayMessageProducer gatewayMessageProducer;

    @GetMapping(value = "/gateway", params = "serviceName=WITHDRAW")
    @ApiOperation("接受银行存管系统提现返回结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "请求的存管接口名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platformNo", value = "平台编号, 平台与存管系统签约时获取", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signature", value = "对reqData参数的签名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "reqData", value = "业务数据报文, json格式", required = true, dataType = "String", paramType = "query")
    })
    public String receiveDepositoryWithdrawResult(
            @RequestParam(name = "serviceName") String serviceName,
            @RequestParam("platformNo") String platformNo,
            @RequestParam("signature") String signature,
            @RequestParam("reqData") String reqData) {

        DepositoryWithdrawResponse depositoryWithdrawResponse = JSON.parseObject(EncryptUtil.decodeUTF8StringBase64(reqData), DepositoryWithdrawResponse.class);
        gatewayMessageProducer.personalWithdraw(depositoryWithdrawResponse);
        return "OK";
    }

    @GetMapping(value = "/gateway", params = "serviceName=RECHARGE")
    @ApiOperation("接受银行存管系统充值返回结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "请求的存管接口名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platformNo", value = "平台编号, 平台与存管系统签约时获取", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signature", value = "对reqData参数的签名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "reqData", value = "业务数据报文, json格式", required = true, dataType = "String", paramType = "query")
    })
    public String receiveDepositoryRechargeResult(
            @RequestParam(name = "serviceName") String serviceName,
            @RequestParam("platformNo") String platformNo,
            @RequestParam("signature") String signature,
            @RequestParam("reqData") String reqData) {

            DepositoryRechargeResponse depositoryRechargeResponse = JSON.parseObject(EncryptUtil.decodeUTF8StringBase64(reqData), DepositoryRechargeResponse.class);
            gatewayMessageProducer.personalRecharge(depositoryRechargeResponse);
            return "OK";
    }

    @GetMapping(value = "/gateway", params = "serviceName=PERSONAL_REGISTER")
    @ApiOperation("接受银行存管系统开户回调结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "请求的存管接口名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platformNo", value = "平台编号, 平台与存管系统签约时获取", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signature", value = "对reqData参数的签名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "reqData", value = "业务数据报文, json格式", required = true, dataType = "String", paramType = "query")
    })
    public String receiveDepositoryCreateConsumerResult(
            @RequestParam(name = "serviceName") String serviceName,
            @RequestParam("platformNo") String platformNo,
            @RequestParam("signature") String signature,
            @RequestParam("reqData") String reqData) {

        // 1、更新数据
        DepositoryConsumerResponse response = JSON.parseObject(
                EncryptUtil.decodeUTF8StringBase64(reqData), DepositoryConsumerResponse.class);
        depositoryRecordService.modifyRequestStatus(response.getRequestNo(), response.getStatus());

        // 2、发送消息给用户中心
        gatewayMessageProducer.personalRegister(response);

        // 3、给银行存管系统返回结果
        return "OK";
    }
}
