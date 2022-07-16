package com.wanxin.consumer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanxin.api.consumer.model.BankCardDTO;
import com.wanxin.consumer.entity.BankCard;
import com.wanxin.consumer.mapper.BankCardMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Administrator
 * @Date 2022/6/13 23:32
 */
@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper, BankCard> implements BankCardService {
    @Autowired
    private BankCardMapper bankCardMapper;
    @Autowired
    private ConsumerService consumerService;

    @Override
    public BankCardDTO getByUserMobile(String mobile) {
        BankCard bankCard = bankCardMapper.selectOne(new LambdaQueryWrapper<BankCard>().eq(BankCard::getMobile, mobile));
        if (bankCard == null) {
            return null;
        }

        String name = consumerService.getByMobile(mobile).getFullname();
        BankCardDTO bankCardDTO = convertBankCardEntityToDTO(bankCard);
        bankCardDTO.setFullName(name);
        return bankCardDTO;
    }

    /**
     * 根据用户id
     * 获取银行卡信息
     *
     * @param consumerId 用户id
     * @return
     */
    @Override
    public BankCardDTO getByConsumerId(Long consumerId) {
//        LambdaQueryWrapper<BankCard> eq = new LambdaQueryWrapper<BankCard>().eq(BankCard::getConsumerId, consumerId);
//        return convertBankCardEntityToDTO(bankCardMapper.selectOne(eq));

        BankCard bankCard = getOne(new QueryWrapper<BankCard>().lambda().eq(BankCard::getConsumerId, consumerId));
        return convertBankCardEntityToDTO(bankCard);
    }

    /**
     * 根据银行卡号
     * 获取银行卡信息
     *
     * @param cardNumber 卡号
     * @return
     */
    @Override
    public BankCardDTO getByCardNumber(String cardNumber) {
//        LambdaQueryWrapper<BankCard> eq = new LambdaQueryWrapper<BankCard>().eq(BankCard::getCardNumber, cardNumber);
//        return convertBankCardEntityToDTO(bankCardMapper.selectOne(eq));

        BankCard bankCard = getOne(new QueryWrapper<BankCard>().lambda().eq(BankCard::getCardNumber, cardNumber));
        return convertBankCardEntityToDTO(bankCard);
    }

    /**
     * entity 2 dto
     *
     * @param entity
     * @return
     */
    private BankCardDTO convertBankCardEntityToDTO(BankCard entity) {
        if (entity == null) {
            return null;
        }

        BankCardDTO dto = new BankCardDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
