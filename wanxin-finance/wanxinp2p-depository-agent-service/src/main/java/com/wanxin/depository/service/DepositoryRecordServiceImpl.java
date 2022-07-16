package com.wanxin.depository.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.RechargeRequest;
import com.wanxin.api.consumer.model.WithdrawRequest;
import com.wanxin.api.dipository.model.DepositoryBaseResponse;
import com.wanxin.api.dipository.model.DepositoryRecordDTO;
import com.wanxin.api.dipository.model.DepositoryResponseDTO;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.api.dipository.model.LoanRequest;
import com.wanxin.api.dipository.model.ProjectRequestDataDTO;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.api.transaction.model.ModifyProjectStatusDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.BusinessException;
import com.wanxin.common.domain.PreprocessBusinessTypeCode;
import com.wanxin.common.domain.StatusCode;
import com.wanxin.common.util.EncryptUtil;
import com.wanxin.common.util.RSAUtil;
import com.wanxin.depository.common.cache.RedisCache;
import com.wanxin.depository.common.constant.DepositoryErrorCode;
import com.wanxin.depository.common.constant.DepositoryRequestTypeCode;
import com.wanxin.depository.entity.DepositoryRecord;
import com.wanxin.depository.mapper.DepositoryRecordMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author Administrator
 * @Date 2022/6/15 20:26
 */
@Service
public class DepositoryRecordServiceImpl extends ServiceImpl<DepositoryRecordMapper, DepositoryRecord> implements DepositoryRecordService{

    @Autowired
    private ConfigService configService;
    @Autowired
    private OkHttpService okHttpService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String personalRegistry = "PERSONAL_REGISTER";//开户
    private String recharge = "RECHARGE";//充值
    private String withdraw = "WITHDRAW";//提现

    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> confirmRepayment(RepaymentRequest repaymentRequest) {
        //构造交易记录
        DepositoryRecord depositoryRecord = new DepositoryRecord(repaymentRequest.getRequestNo(), PreprocessBusinessTypeCode.REPAYMENT.getCode(), "Repayment", repaymentRequest.getId());
        // 分布式事务幂等性实现
        DepositoryResponseDTO<DepositoryBaseResponse> responseDTO = handleIdempotent(depositoryRecord);
        if (responseDTO != null) {
            return responseDTO;
        }

        // 获取最新交易记录
        depositoryRecord = getEntityByRequestNo(repaymentRequest.getRequestNo());

        // 确认还款(调用银行存管系统)
        final String jsonString = JSON.toJSONString(repaymentRequest);
        // 业务数据报文, base64处理, 方便传输
        String reqData = EncryptUtil.encodeUTF8StringBase64(jsonString);
        // 拼接银行存管系统请求地址
        String url = configService.getDepositoryUrl() + "/service";
        // 封装通用方法, 请求银行存管系统
        return sendHttpGet("CONFIRM_REPAYMENT", url, reqData, depositoryRecord);
    }

    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO) {
        DepositoryRecord depositoryRecord = new DepositoryRecord(modifyProjectStatusDTO.getRequestNo(), DepositoryRequestTypeCode.MODIFY_STATUS.getCode(), "Project", modifyProjectStatusDTO.getId());

        // 幂等性实现
        DepositoryResponseDTO<DepositoryBaseResponse> responseDTO = handleIdempotent(depositoryRecord);
        if (responseDTO != null) {
            return responseDTO;
        }
        // 根据requestNo获取交易记录
        depositoryRecord = getEntityByRequestNo(modifyProjectStatusDTO.getRequestNo());
        // loanRequest 转为 json 进行数据签名
        String jsonString = JSON.toJSONString(modifyProjectStatusDTO);
        // 业务数据报文
        String reqData = EncryptUtil.encodeUTF8StringBase64(jsonString);
        // 拼接银行存管系统请求地址
        String url = configService.getDepositoryUrl() + "/service";
        // 封装通用方法, 请求银行存管系统
        return sendHttpGet("MODIFY_PROJECT", url, reqData, depositoryRecord);
    }

    /**
     * 审核满标放款
     * 扣款：投资人投标花的钱，或者借款人还款
     * 入账：借款人接到的钱，或者投资人从借款人处拿到的还款金额
     *
     * @param loanRequest
     * @return
     */
    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> confirmLoan(LoanRequest loanRequest) {
        DepositoryRecord depositoryRecord = new DepositoryRecord(loanRequest.getRequestNo(), DepositoryRequestTypeCode.FULL_LOAN.getCode(), "LoanRequest", loanRequest.getId());
        // 幂等性实现
        DepositoryResponseDTO<DepositoryBaseResponse> responseDTO = handleIdempotent(depositoryRecord);
        if (responseDTO != null) {
            return responseDTO;
        }
        // 根据requestNo获取交易记录
        depositoryRecord = getEntityByRequestNo(loanRequest.getRequestNo());
        // loanRequest 转为 json 用于数据签名
        String jsonString = JSON.toJSONString(loanRequest);
        // 业务数据报文, json数据base64编码处理方便传输
        String reqData = EncryptUtil.encodeUTF8StringBase64(jsonString);
        // 拼接银行存管系统请求地址
        String url = configService.getDepositoryUrl() + "/service";
        // 封装通用方法, 请求银行存管系统
        return sendHttpGet("CONFIRM_LOAN", url, reqData, depositoryRecord);
    }

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
    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest) {
        //1、保存交易记录（实现幂等性）
        DepositoryRecord depositoryRecord = new DepositoryRecord(userAutoPreTransactionRequest.getRequestNo(), userAutoPreTransactionRequest.getBizType(), "UserAutoPreTransactionRequest", userAutoPreTransactionRequest.getId());
        //幂等性实现
        DepositoryResponseDTO<DepositoryBaseResponse> responseDTO = handleIdempotent(depositoryRecord);
        if (responseDTO != null) {
            return responseDTO;
        }
        //根据requestNo获取交易记录
        depositoryRecord = getEntityByRequestNo(userAutoPreTransactionRequest.getRequestNo());

        //2、签名
        //userAutoPreTransactionRequest 转为 json 用于数据签名
        String jsonString = JSON.toJSONString(userAutoPreTransactionRequest);
        //业务数据报文, 对json数据进行base64编码处理方便传输
        String reqData = EncryptUtil.encodeUTF8StringBase64(jsonString);

        //3、发送数据到银行存管系统
        // 发送请求, 获取结果
        // 拼接银行存管系统请求地址
        String url = configService.getDepositoryUrl() + "/service";
        // 向银行存管系统发送请求
        return sendHttpGet("USER_AUTO_PRE_TRANSACTION", url, reqData, depositoryRecord);
    }

    /**
     * 保存标的信息
     *
     * @param projectDTO 标的信息
     * @return
     */
    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> createProject(ProjectDTO projectDTO) {
        //1、保存交易记录
        DepositoryRecord depositoryRecord = new DepositoryRecord(
                projectDTO.getRequestNo(),
                DepositoryRequestTypeCode.CREATE.getCode(),
                "Project",
                projectDTO.getId());

        // 幂等性实现
        DepositoryResponseDTO<DepositoryBaseResponse> responseDTO = handleIdempotent(depositoryRecord);
        if (responseDTO != null) {
            return responseDTO;
        }

        // 根据requestNo获取交易记录
        depositoryRecord = getEntityByRequestNo(projectDTO.getRequestNo());

        //2、签名数据
        //ProjectDTO 转换为 ProjectRequestDataDTO
        ProjectRequestDataDTO projectRequestDataDTO =
                convertProjectDTOToProjectRequestDataDTO(projectDTO, depositoryRecord.getRequestNo());
        //转换为JSON
        String jsonString = JSON.toJSONString(projectRequestDataDTO);

        //base64加密
        String reqData = EncryptUtil.encodeUTF8StringBase64(jsonString);

        //3、往银行存管系统发送数据(标的信息),根据结果修改状态并返回结果
        // url地址
        String url = configService.getDepositoryUrl() + "/service";

        //4、根据结果修改状态并返回结果
        // OKHttpClient 发送Http请求
        return sendHttpGet("CREATE_PROJECT", url, reqData, depositoryRecord);
    }

    private DepositoryResponseDTO<DepositoryBaseResponse> sendHttpGet(String serviceName, String url, String
            reqData, DepositoryRecord depositoryRecord) {
        // 银行存管系统接收的4大参数: serviceName, platformNo, reqData, signature
        // signature会在okHttp拦截器(SignatureInterceptor)中处理
        // 平台编号
        String platformNo = configService.getP2pCode();
        // redData签名
        // 发送请求, 获取结果, 如果检验签名失败, 拦截器会在结果中放入: "signature", "false"
        String responseBody = okHttpService.doSyncGet(url + "?serviceName=" + serviceName
                + "&platformNo=" + platformNo
                + "&reqData=" + reqData);

        depositoryRecord.setResponseData(responseBody);

        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = JSONObject.parseObject(
                responseBody,
                new TypeReference<DepositoryResponseDTO<DepositoryBaseResponse>>() {
                }
        );

        // 响应后, 根据结果更新数据库( 进行签名判断 )
        // 判断签名(signature)是为 false, 如果是说明验签失败!
        if (!"false".equals(depositoryResponse.getSignature())) {
            // 成功 - 设置数据同步状态
            depositoryRecord.setRequestStatus(StatusCode.STATUS_IN.getCode());
            // 设置消息确认时间
            depositoryRecord.setConfirmDate(LocalDateTime.now());
            // 更新数据库
//            depositoryRecordMapper.updateById(depositoryRecord);
            updateById(depositoryRecord);
        } else {
            // 失败 - 设置数据同步状态
            depositoryRecord.setRequestStatus(StatusCode.STATUS_FAIL.getCode());
            // 设置消息确认时间
            depositoryRecord.setConfirmDate(LocalDateTime.now());
            // 更新数据库
//            depositoryRecordMapper.updateById(depositoryRecord);
            updateById(depositoryRecord);
            // 抛业务异常
            throw new BusinessException(DepositoryErrorCode.E_160101);
        }

        return depositoryResponse;
    }

    private ProjectRequestDataDTO convertProjectDTOToProjectRequestDataDTO(ProjectDTO projectDTO, String requestNo) {
        if (projectDTO == null) {
            return null;
        }

        ProjectRequestDataDTO requestDataDTO = new ProjectRequestDataDTO();
        BeanUtils.copyProperties(projectDTO, requestDataDTO);
        requestDataDTO.setRequestNo(requestNo);
        requestDataDTO.setProjectName(projectDTO.getName());
        requestDataDTO.setProjectType(projectDTO.getType());
        requestDataDTO.setProjectAmount(projectDTO.getAmount());
        requestDataDTO.setProjectDesc(projectDTO.getDescription());
        requestDataDTO.setProjectPeriod(projectDTO.getPeriod());
        return requestDataDTO;
    }

    /**
     * 保存交易记录
     */
    private DepositoryRecord saveDepositoryRecord(String requestNo, String requestType
            , String objectType, Long objectId) {
        DepositoryRecord depositoryRecord = new DepositoryRecord();
        // 设置请求流水号
        depositoryRecord.setRequestNo(requestNo);
        // 设置请求类型
        depositoryRecord.setRequestType(requestType);
        // 设置关联业务实体类型
        depositoryRecord.setObjectType(objectType);
        // 设置关联业务实体标识
        depositoryRecord.setObjectId(objectId);
        // 设置请求时间
        depositoryRecord.setCreateDate(LocalDateTime.now());
        // 设置数据同步状态
        depositoryRecord.setRequestStatus(StatusCode.STATUS_OUT.getCode());

        // 保存数据
//        depositoryRecordMapper.insert(depositoryRecord);
        save(depositoryRecord);
        return depositoryRecord;
    }

    /**
     * 实现幂等性
     *
     * @param depositoryRecord 存管记录信息
     * @return
     */
    private DepositoryResponseDTO<DepositoryBaseResponse> handleIdempotent(DepositoryRecord depositoryRecord) {
        //根据requestNo进行查询
        String requestNo = depositoryRecord.getRequestNo();
        DepositoryRecordDTO depositoryRecordDTO = getByRequestNo(requestNo);

        //1、交易记录不存在
        if (depositoryRecordDTO == null) {
            //保存交易记录
            saveDepositoryRecord(depositoryRecord.getRequestNo(), depositoryRecord.getRequestType(), depositoryRecord.getObjectType(), depositoryRecord.getObjectId());
            return null;
        }
        //2、重复点击，重复请求，交易记录存在并且数据状态为 0-未同步，利用redis的原子性，争夺执行权
        if (StatusCode.STATUS_OUT.getCode() == depositoryRecordDTO.getRequestStatus()) {
            RedisCache cache = new RedisCache(stringRedisTemplate);
            // 如果requestNo不存在则返回1, 如果已经存在, 则会返回(requestNo已存在个数+1)
            Long count = cache.incrBy(requestNo, 1L);
            if (count == 1) {
                // 设置requestNo有效期5秒
                cache.expire(requestNo, 30);
                return null;
            }
            // 若count大于1, 说明已有线程在执行该操作, 直接返回"正在处理"
            if (count > 1) {
                throw new BusinessException(DepositoryErrorCode.E_160103);
            }
        }

        //交易记录已经存在，并且状态是 2-同步失败
        if (StatusCode.STATUS_FAIL.getCode() == depositoryRecordDTO.getRequestStatus()) {
            //重置同步状态为 0-未同步
            // 根据requestNo获取交易记录
            depositoryRecord = getEntityByRequestNo(requestNo);
            depositoryRecord.setResponseData("");
            depositoryRecord.setRequestStatus(StatusCode.STATUS_OUT.getCode());
            updateById(depositoryRecord);
            return null;
        }

        //3、交易记录已经存在，并且状态是 1-已同步
        return JSONObject.parseObject(
                depositoryRecordDTO.getResponseData(),
                new TypeReference<DepositoryResponseDTO<DepositoryBaseResponse>>() {}
        );
    }

    private DepositoryRecordDTO getByRequestNo(String requestNo) {
        DepositoryRecord depositoryRecord = getEntityByRequestNo(requestNo);
        if (depositoryRecord == null) {
            return null;
        }
        DepositoryRecordDTO depositoryRecordDTO = new DepositoryRecordDTO();
        BeanUtils.copyProperties(depositoryRecord, depositoryRecordDTO);
        return depositoryRecordDTO;
    }

    private DepositoryRecord getEntityByRequestNo(String requestNo) {
//        return depositoryRecordMapper.selectOne(new LambdaQueryWrapper<DepositoryRecord>().eq(DepositoryRecord::getRequestNo, requestNo));
        return getOne(new LambdaQueryWrapper<DepositoryRecord>().eq(DepositoryRecord::getRequestNo, requestNo));
    }

    @Override
    public GatewayRequest withdrawRecords(WithdrawRequest withdrawRequest) {
        return encapsulatedGatewayRequest(this.withdraw, withdrawRequest);
    }

    @Override
    public GatewayRequest rechargeRecords(RechargeRequest rechargeRequest) {
        return encapsulatedGatewayRequest(this.recharge, rechargeRequest);
    }

    @Override
    public Boolean modifyRequestStatus(String requestNo, Integer requestsStatus) {
        return update(Wrappers.<DepositoryRecord>lambdaUpdate()
                .eq(DepositoryRecord::getRequestNo, requestNo)
                .set(DepositoryRecord::getRequestStatus, requestsStatus)
                .set(DepositoryRecord::getConfirmDate, LocalDateTime.now()));
    }

    @Override
    public GatewayRequest createConsumer(ConsumerRequest consumerRequest) {
        // 1、保存交易记录
        saveDepositoryRecord(consumerRequest);

        // 2、签名（加密）数据并返回
//        String reqData = JSON.toJSONString(consumerRequest);
//        String sign = RSAUtil.sign(reqData, configService.getP2pPrivateKey() , "utf-8");
//
//        GatewayRequest gatewayRequest = new GatewayRequest();
//        gatewayRequest.setServiceName("PERSONAL_REGISTER");
//        gatewayRequest.setPlatformNo(configService.getP2pCode());
//        gatewayRequest.setReqData(EncryptUtil.encodeURL(EncryptUtil.encodeUTF8StringBase64(reqData)));
//        gatewayRequest.setSignature(EncryptUtil.encodeURL(sign));
//        gatewayRequest.setDepositoryUrl(configService.getDepositoryUrl() + "/gateway");
//        return gatewayRequest;

        return encapsulatedGatewayRequest(this.recharge, consumerRequest);
    }

    private GatewayRequest encapsulatedGatewayRequest(String serviceName, Object data) {
        String depositoryUrl = configService.getDepositoryUrl() + "/gateway";
        String p2pCode = configService.getP2pCode();
        String p2pPrivateKeyKey = configService.getP2pPrivateKey();

        // 签名数据
        String reqData = JSON.toJSONString(data);
        String sign = RSAUtil.sign(reqData, p2pPrivateKeyKey, "utf-8");

        // 加密签名数据
        reqData = EncryptUtil.encodeURL(EncryptUtil.encodeUTF8StringBase64(reqData));
        sign = EncryptUtil.encodeURL(sign);

        GatewayRequest gatewayRequest = new GatewayRequest();
        gatewayRequest.setServiceName(serviceName);
        gatewayRequest.setPlatformNo(p2pCode);
        gatewayRequest.setReqData(reqData);
        gatewayRequest.setSignature(sign);
        gatewayRequest.setDepositoryUrl(depositoryUrl);

        return gatewayRequest;
    }

    private void saveDepositoryRecord(ConsumerRequest consumerRequest) {
        DepositoryRecord depositoryRecord = new DepositoryRecord();
        depositoryRecord.setRequestNo(consumerRequest.getRequestNo());
        depositoryRecord.setRequestType(DepositoryRequestTypeCode.CONSUMER_CREATE.getCode());
        depositoryRecord.setObjectType("Consumer");
        depositoryRecord.setObjectId(consumerRequest.getId());
        depositoryRecord.setCreateDate(LocalDateTime.now());
        depositoryRecord.setRequestStatus(StatusCode.STATUS_OUT.getCode());
        save(depositoryRecord);
    }
}
