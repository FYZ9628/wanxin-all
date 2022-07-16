package com.wanxin.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxin.api.consumer.model.BankCardDTO;
import com.wanxin.consumer.entity.BankCard;

/**
 * @Author Administrator
 * @Date 2022/6/13 23:29
 */
public interface BankCardService extends IService<BankCard> {

    /**
     * 根据用户手机号码获取银行卡信息
     *
     * @param mobile 用户手机号
     * @return
     */
    BankCardDTO getByUserMobile(String mobile);

    /**
     * 根据用户id
     * 获取银行卡信息
     *
     * @param consumerId 用户id
     * @return
     */
    BankCardDTO getByConsumerId(Long consumerId);

    /**
     * 根据银行卡号
     * 获取银行卡信息
     *
     * @param cardNumber 卡号
     * @return
     */
    BankCardDTO getByCardNumber(String cardNumber);
}
