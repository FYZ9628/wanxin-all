package com.wanxin.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxin.api.consumer.model.BorrowerDTO;
import com.wanxin.api.consumer.model.ConsumerDTO;
import com.wanxin.api.consumer.model.ConsumerRegisterDTO;
import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.dipository.model.DepositoryConsumerResponse;
import com.wanxin.api.dipository.model.DepositoryRechargeResponse;
import com.wanxin.api.dipository.model.DepositoryWithdrawResponse;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.consumer.entity.Consumer;

import java.io.IOException;

/**
 * @Author Administrator
 * @Date 2022/6/7 17:22
 */
public interface ConsumerService extends IService<Consumer> {

    /**
     * 获取借款人基本信息
     *
     * @param id
     * @return
     */
    BorrowerDTO getBorrower(Long id);

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return
     */
    ConsumerDTO getByMobile(String mobile);

    /**
     * 更新提现回调回调状态（提现回调）
     *
     * @param depositoryWithdrawResponse
     * @return
     */
    Boolean modifyWithdrawRecordResult(DepositoryWithdrawResponse depositoryWithdrawResponse);

    /**
     * 生成用户提现数据
     *
     * @param amount      提现金额
     * @param fallbackUrl 回调地址
     * @param mobile      手机号
     * @return
     */
    RestResponse<GatewayRequest> createWithdrawRecord(String amount, String fallbackUrl, String mobile);

    /**
     * 更新充值回调状态（充值回调）
     *
     * @param depositoryRechargeResponse
     * @return
     */
    Boolean modifyRechargeRecordResult(DepositoryRechargeResponse depositoryRechargeResponse);

    /**
     * 生成用户充值数据
     *
     * @param amount   充值金额
     * @param fallback 回调地址
     * @param mobile   手机号
     * @return
     */
    RestResponse<GatewayRequest> createRechargeRecord(String amount, String fallback, String mobile);

    /**
     * 更新开户结果（开户回调）
     *
     * @param response
     * @return
     */
    Boolean modifyResult(DepositoryConsumerResponse response);

    /**
     * 生成开户数据
     *
     * @param consumerRequest
     * @return
     */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest) throws IOException;

    /**
     * 检测用户是否存在
     *
     * @param mobile 用户手机号
     * @return
     */
    Integer checkMobile(String mobile);

    /**
     * 用户注册
     *
     * @param consumerRegisterDTO 用户注册信息
     * @return
     */
    void register(ConsumerRegisterDTO consumerRegisterDTO);
}
