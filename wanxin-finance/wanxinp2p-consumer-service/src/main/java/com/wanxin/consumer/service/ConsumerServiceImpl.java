package com.wanxin.consumer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanxin.api.account.model.AccountDTO;
import com.wanxin.api.account.model.AccountRegisterDTO;
import com.wanxin.api.account.model.LoginUser;
import com.wanxin.api.consumer.model.BankCardDTO;
import com.wanxin.api.consumer.model.BorrowerDTO;
import com.wanxin.api.consumer.model.ConsumerDTO;
import com.wanxin.api.consumer.model.ConsumerRegisterDTO;
import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.RechargeRequest;
import com.wanxin.api.consumer.model.WithdrawRequest;
import com.wanxin.api.dipository.model.DepositoryConsumerResponse;
import com.wanxin.api.dipository.model.DepositoryRechargeResponse;
import com.wanxin.api.dipository.model.DepositoryWithdrawResponse;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.common.domain.BusinessException;
import com.wanxin.common.domain.CodePrefixCode;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.DepositoryReturnCode;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.common.domain.StatusCode;
import com.wanxin.common.util.CodeNoUtil;
import com.wanxin.consumer.agent.AccountApiAgent;
import com.wanxin.consumer.agent.DepositoryAgentApiAgent;
import com.wanxin.consumer.common.ConsumerErrorCode;
import com.wanxin.consumer.common.SecurityUtil;
import com.wanxin.consumer.entity.BankCard;
import com.wanxin.consumer.entity.Consumer;
import com.wanxin.consumer.entity.RechargeRecord;
import com.wanxin.consumer.entity.WithdrawRecord;
import com.wanxin.consumer.mapper.BankCardMapper;
import com.wanxin.consumer.mapper.ConsumerMapper;
import com.wanxin.consumer.mapper.RechargeRecordMapper;
import com.wanxin.consumer.mapper.WithdrawRecordMapper;
import com.wanxin.consumer.utils.CheckBankCardUtil;
import com.wanxin.consumer.utils.IdCardUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author Administrator
 * @Date 2022/6/7 17:30
 */
@Slf4j
@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService {
    @Autowired
    private ConsumerMapper consumerMapper;
    @Autowired
    private AccountApiAgent accountApiAgent;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private CheckBankCardUtil checkBankCardUtil;
    @Autowired
    private BankCardMapper bankCardMapper;
    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    private WithdrawRecordMapper withdrawRecordMapper;

    @Override
    public BorrowerDTO getBorrower(Long id) {
        ConsumerDTO consumerDTO = get(id);
        BorrowerDTO borrowerDTO = new BorrowerDTO();
        BeanUtils.copyProperties(consumerDTO, borrowerDTO);
        Map<String, String> cardInfo = IdCardUtil.getBirAgeSex(borrowerDTO.getIdNumber());
        borrowerDTO.setAge(new Integer(cardInfo.get("age")));
        borrowerDTO.setBirthday(cardInfo.get("birthday"));
        borrowerDTO.setGender(cardInfo.get("sexCode"));

        return borrowerDTO;
    }

    private ConsumerDTO get(Long id) {
        Consumer entity = getById(id);
        if (entity == null) {
            log.info("id为{}的用户信息不存在", id);
            throw new BusinessException(ConsumerErrorCode.E_140101);
        }
        return convertConsumerEntityToDTO(entity);
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return
     */
    @Override
    public ConsumerDTO getByMobile(String mobile) {
        Consumer consumer = consumerMapper.selectOne(new LambdaQueryWrapper<Consumer>().eq(Consumer::getMobile, mobile));

        //用 ServiceImpl 中的方法
//        Consumer consumer = getOne(new QueryWrapper<Consumer>().lambda().eq(Consumer::getMobile, mobile));

        if (consumer != null) {
            return convertConsumerEntityToDTO(consumer);
        }

        return null;
    }

    @Override
    public Boolean modifyWithdrawRecordResult(DepositoryWithdrawResponse depositoryWithdrawResponse) {
        if (!"SUCCESS".equals(depositoryWithdrawResponse.getTransactionStatus().toLowerCase())) {
            throw new BusinessException(ConsumerErrorCode.E_140141);
        }

        String requestNo = depositoryWithdrawResponse.getRequestNo();
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setCallbackStatus(1);

        return withdrawRecordMapper.update(withdrawRecord, new LambdaQueryWrapper<WithdrawRecord>().eq(WithdrawRecord::getRequestNo, requestNo)) == 1;
    }

    @Override
    public RestResponse<GatewayRequest> createWithdrawRecord(String amount, String fallbackUrl, String mobile) {
        ConsumerDTO consumer = getByMobile(mobile);
        assert consumer != null;

        //未开户的报错
        if (consumer.getIsBindCard() == 0) {
            throw new BusinessException(ConsumerErrorCode.E_140104);
        }

        String userNo = consumer.getUserNo();
        Long id = consumer.getId();
        String requestNo = CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX);

        saveWithdrawRecord(id, userNo, amount, requestNo);

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAmount(new BigDecimal(amount));
        withdrawRequest.setCardNumber(consumer.getIdNumber());
        withdrawRequest.setMobile(mobile);
        withdrawRequest.setUserNo(userNo);
        withdrawRequest.setRequestNo(requestNo);
        withdrawRequest.setCallbackURL(fallbackUrl);
        withdrawRequest.setCommission(new BigDecimal("0"));
        return depositoryAgentApiAgent.createWithdrawRecord(withdrawRequest);
    }

    private void saveWithdrawRecord(Long cid, String userNo, String amount, String requestNo) {
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setConsumerId(cid);
        withdrawRecord.setUserNo(userNo);
        withdrawRecord.setAmount(new BigDecimal(amount));
        withdrawRecord.setCreateDate(LocalDateTime.now());
        withdrawRecord.setRequestNo(requestNo);
        withdrawRecord.setCallbackStatus(0);

        withdrawRecordMapper.insert(withdrawRecord);
    }

    @Override
    public Boolean modifyRechargeRecordResult(DepositoryRechargeResponse depositoryRechargeResponse) {
        if (!"SUCCESS".equals(depositoryRechargeResponse.getTransactionStatus().toLowerCase())) {
            throw new BusinessException(ConsumerErrorCode.E_140131);
        }

        String requestNo = depositoryRechargeResponse.getRequestNo();
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setCallbackStatus(1);
        return rechargeRecordMapper.update(rechargeRecord, new LambdaQueryWrapper<RechargeRecord>().eq(RechargeRecord::getRequestNo, requestNo)) == 1;
    }

    @Override
    public RestResponse<GatewayRequest> createRechargeRecord(String amount, String fallback, String mobile) {
        ConsumerDTO consumer = getByMobile(mobile);
        assert consumer != null;

        //未开户的报错
        if (consumer.getIsBindCard() == 0) {
            throw new BusinessException(ConsumerErrorCode.E_140104);
        }

        String userNo = consumer.getUserNo();
        Long id = consumer.getId();
        String requestNo = CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX);

        saveRechargeRecord(amount, userNo, requestNo, id);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal(amount));
        rechargeRequest.setCallbackUrl(fallback);
        rechargeRequest.setUserNo(userNo);
        rechargeRequest.setRequestNo(requestNo);
        return depositoryAgentApiAgent.createRechargeRecord(rechargeRequest);
    }

    private void saveRechargeRecord(String amount, String userNo, String requestNo, Long id) {
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setAmount(new BigDecimal(amount));
        rechargeRecord.setUserNo(userNo);
        rechargeRecord.setRequestNo(requestNo);
        rechargeRecord.setCallbackStatus(0);
        rechargeRecord.setConsumerId(id);
        rechargeRecord.setCreateDate(LocalDateTime.now());

        rechargeRecordMapper.insert(rechargeRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyResult(DepositoryConsumerResponse response) {
        // 1、获取状态（1-成功，2-失败）
        int status = response.getRespCode().equals(
                DepositoryReturnCode.RETURN_CODE_00000.getCode()) ? StatusCode.STATUS_IN.getCode() : StatusCode.STATUS_FAIL.getCode();

        // 2、更新开户结果
        Consumer consumer = getByRequestNo(response.getRequestNo());
        update(Wrappers.<Consumer>lambdaUpdate()
                .eq(Consumer::getId, consumer.getId())
                .set(Consumer::getIsBindCard, status)
                .set(Consumer::getStatus, status));

        // 3、更新银行卡信息
        return bankCardService.update(Wrappers.<BankCard>lambdaUpdate()
                .eq(BankCard::getConsumerId, consumer.getId())
                .set(BankCard::getStatus, status)
                .set(BankCard::getBankCode, response.getBankCode())
                .set(BankCard::getBankName, response.getBankName()));
    }

    private Consumer getByRequestNo(String requestNo) {
        return getOne(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getRequestNo, requestNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest) throws IOException {
        // 1、判断当前用户是否已经开户
        ConsumerDTO consumerDTO = getByMobile(consumerRequest.getMobile());
        if (consumerDTO.getIsBindCard() == 1) {
            throw new BusinessException(ConsumerErrorCode.E_140105);
        }

        // 2、判断提交过来的银行卡是否已被绑定
        BankCardDTO bankCardDTO = bankCardService.getByCardNumber(consumerRequest.getCardNumber());
        if (bankCardDTO != null && bankCardDTO.getStatus() == StatusCode.STATUS_IN.getCode()) {
            throw new BusinessException(ConsumerErrorCode.E_140151);
        }

        // 3、更新用户开户信息
        consumerRequest.setId(consumerDTO.getId());
        // 产生请求流水号和用户编号
        consumerRequest.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumerRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));

//        UpdateWrapper<Consumer> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.lambda().eq(Consumer::getUsername, consumerDTO.getUsername());
//        updateWrapper.lambda().set(Consumer::getUserNo, consumerRequest.getUserNo());
//        updateWrapper.lambda().set(Consumer::getRequestNo, consumerRequest.getRequestNo());
//        updateWrapper.lambda().set(Consumer::getFullname, consumerRequest.getFullname());
//        updateWrapper.lambda().set(Consumer::getIdNumber, consumerRequest.getIdNumber());
//        updateWrapper.lambda().set(Consumer::getAuthList, "ALL");
//        update(updateWrapper);

        // 设置查询条件和需要更新的数据
        Consumer consumer = new Consumer();
        consumer.setMobile(consumerDTO.getMobile());
        consumer.setUserNo(consumerRequest.getUserNo());
        consumer.setRequestNo(consumerRequest.getRequestNo());
        consumer.setFullname(consumerRequest.getFullname());
        // 设置默认可贷额度
        consumer.setLoanAmount(new BigDecimal("2000"));
//        if (!IdCardUtil.isValidatedAllIdcard(consumerRequest.getIdNumber())) {
//            throw new BusinessException(ConsumerErrorCode.E_140110);
//        }
        consumer.setStatus(0);
        consumer.setIsCardAuth(0);
        consumer.setIdNumber(consumerRequest.getIdNumber());
        consumer.setAuthList("ALL");
        consumer.setIsBindCard(1);
        consumerMapper.update(consumer, new LambdaQueryWrapper<Consumer>().eq(Consumer::getMobile, consumerDTO.getMobile()));


        // 4、保存银行卡信息
        BankCard bankCard = new BankCard();
        bankCard.setConsumerId(consumerDTO.getId());
//        String[] split = checkBankCardUtil.checkBankCard(consumerRequest.getCardNumber()).split("-");
//        bankCard.setBankCode(split[0]);
//        bankCard.setBankName(split[1]);
        bankCard.setBankCode(consumerRequest.getBankCode());
        bankCard.setCardNumber(consumerRequest.getCardNumber());
        bankCard.setMobile(consumerRequest.getMobile());
        bankCard.setStatus(StatusCode.STATUS_OUT.getCode());

        BankCardDTO existbankCard = bankCardService.getByConsumerId(bankCard.getConsumerId());
        if (existbankCard == null) {
            bankCardMapper.insert(bankCard);
        } else {
            bankCard.setId(existbankCard.getId());
            bankCardMapper.updateById(bankCard);
        }

//        if (existbankCard != null) {
//            bankCard.setId(existbankCard.getId());
//        }
//        bankCardService.saveOrUpdate(bankCard);

        // 5、准备数据，发起远程调用，把数据发到存管代理服务
        return depositoryAgentApiAgent.createConsumer(consumerRequest);
    }

    @Override
    public Integer checkMobile(String mobile) {
        return getByMobile(mobile) != null ? 1 : 0;
    }

    private ConsumerDTO convertConsumerEntityToDTO(Consumer consumer) {
        if (consumer == null) {
            return null;
        }

        ConsumerDTO dto = new ConsumerDTO();
        BeanUtils.copyProperties(consumer, dto);
        return dto;
    }

    @Override
    @HmilyTCC(confirmMethod = "confirmRegister", cancelMethod = "cancelRegister")
    public void register(ConsumerRegisterDTO consumerRegisterDTO) {
        if (checkMobile(consumerRegisterDTO.getMobile()) == 1) {
            throw new BusinessException(ConsumerErrorCode.E_140107);
        }

        consumerRegisterDTO.setUsername(CodeNoUtil.getNo(CodePrefixCode.CODE_NO_PREFIX));
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerRegisterDTO, consumer);
//        consumer.setUsername(CodeNoUtil.getNo(CodePrefixCode.CODE_NO_PREFIX));
        consumer.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        consumer.setIsBindCard(0);
        //添加用户
        consumerMapper.insert(consumer);

        //ServiceImpl 中的方法
//        save(consumer);

        //远程调用account
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        BeanUtils.copyProperties(consumerRegisterDTO, accountRegisterDTO);
        RestResponse<AccountDTO> restResponse = accountApiAgent.register(accountRegisterDTO);
        if (restResponse.getCode() != CommonErrorCode.SUCCESS.getCode()) {
            throw new BusinessException(ConsumerErrorCode.E_140106);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void confirmRegister(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("confirm register");
    }

    @Transactional(rollbackFor = {Exception.class})
    public void cancelRegister(ConsumerRegisterDTO consumerRegisterDTO) {
        // 删除用户信息
        consumerMapper.delete(new LambdaQueryWrapper<Consumer>().eq(Consumer::getMobile, consumerRegisterDTO.getMobile()));
        log.info("cancel register");
    }
}
