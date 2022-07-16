package com.wanxin.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.wanxin.api.consumer.ConsumerAPI;
import com.wanxin.api.consumer.model.BalanceDetailsDTO;
import com.wanxin.api.consumer.model.BankCardDTO;
import com.wanxin.api.consumer.model.BorrowerDTO;
import com.wanxin.api.consumer.model.ConsumerDTO;
import com.wanxin.api.consumer.model.ConsumerDetailsDTO;
import com.wanxin.api.consumer.model.ConsumerRegisterDTO;
import com.wanxin.api.consumer.model.ConsumerRequest;
import com.wanxin.api.consumer.model.FileTokenDTO;
import com.wanxin.api.consumer.model.IdCardDTO;
import com.wanxin.api.dipository.model.GatewayRequest;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.common.util.EncryptUtil;
import com.wanxin.consumer.common.SecurityUtil;
import com.wanxin.consumer.service.BankCardService;
import com.wanxin.consumer.service.ConsumerDetailsService;
import com.wanxin.consumer.service.ConsumerService;
import com.wanxin.consumer.utils.BaiDuOrcIdCardUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 注册、开户、充值、提现、查余额、查用户信息
 *
 * @Author Administrator
 * @Date 2022/6/7 17:52
 */
@Slf4j
@RestController
@Api(value = "用户服务API", tags = "Consumer")
@RefreshScope //实时刷新nacos配置信息
public class ConsumerController implements ConsumerAPI {
    private final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private ConsumerDetailsService consumerDetailsService;

    @Value("${depository.url}")
    private String depositoryUrl;
    @Value("${minio.appId}")
    private String appId;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Override
    @PostMapping("/my/saveConsumerDetails")
    @ApiOperation(value = "保存用户详细信息", notes = "主要存储身份证文件标识")
    @ApiImplicitParam(name = "consumerDetailsDTO", value = "用户详细信息", dataType = "ConsumerDetailsDTO", paramType = "body")
    public RestResponse<String> saveConsumerDetails(@RequestBody ConsumerDetailsDTO consumerDetailsDTO) {
        consumerDetailsService.createConsumerDetails(consumerDetailsDTO, SecurityUtil.getUser().getMobile());
        return RestResponse.success("保存成功");
    }

    @Override
    @GetMapping("/my/applyUploadCertificate")
    @ApiOperation("获取文件上传密钥对")
    public RestResponse<FileTokenDTO> applyUploadCertificate() {
        FileTokenDTO fileTokenDTO = new FileTokenDTO();
        fileTokenDTO.setAppId(appId);
        fileTokenDTO.setAccessKey(accessKey);
        fileTokenDTO.setSecretKey(secretKey);

        return RestResponse.success(fileTokenDTO);
    }
    @Override
    @PostMapping("/my/imageRecognition")
    @ApiOperation("提交身份证图片给百度AI进行识别")
    @ApiImplicitParam(name = "flag", value = "正反面", required = true, dataType = "string", paramType = "query")
    public RestResponse<IdCardDTO> imageRecognition(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("flag") String flag) throws IOException {
        String info = null;
        if ("front".equals(flag)) {
            info = BaiDuOrcIdCardUtil.idCardFront(multipartFile.getBytes());
        }

        if ("back".equals(flag)) {
            // 我们并不需要识别国徽面.
            // info = BaiDuOrcIdCardUtil.idCardBack(multipartFile.getBytes());
            return RestResponse.success();
        }

        JSONObject jsonObject = new JSONObject(info);

        IdCardDTO idCardDTO = new IdCardDTO();
        idCardDTO.setFlag(flag);
        idCardDTO.setIdCardNo(jsonObject.getJSONObject("words_result").getJSONObject("公民身份号码").getString("words"));
        idCardDTO.setIdCardName(jsonObject.getJSONObject("words_result").getJSONObject("姓名").getString("words"));
        idCardDTO.setIdCardAddress(jsonObject.getJSONObject("words_result").getJSONObject("住址").getString("words"));

        return RestResponse.success(idCardDTO);
    }

    @Override
    @GetMapping("/my/bank-cards")
    @ApiOperation("获取用户银行卡信息")
    public RestResponse<BankCardDTO> getBankCard() {
        return RestResponse.success(bankCardService.getByUserMobile(SecurityUtil.getUser().getMobile()));
    }

    @Override
    @ApiOperation("获取借款人用户信息-供微服务访问")
    @ApiImplicitParam(name = "id", value = "用户标识", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/l/borrowers/{id}")
    public RestResponse<BorrowerDTO> getBorrowerMobile(@PathVariable Long id) {
        return RestResponse.success(consumerService.getBorrower(id));
    }

    @Override
    @GetMapping("/my/balances")
    @ApiOperation("获取当前登录用户余额信息（前端调用）")
    public RestResponse<BalanceDetailsDTO> getMyBalance() {
        ConsumerDTO consumerDTO = consumerService.getByMobile(SecurityUtil.getUser().getMobile());
        return getBalanceFromDepository(consumerDTO.getUserNo());
    }

    @Override
    @GetMapping("/l/balances/{userNo}")
    @ApiOperation("获取用户可用余额（给其他微服务调用）")
    @ApiImplicitParam(name = "userNo", value = "用户编码", required = true, dataType = "String")
    public RestResponse<BalanceDetailsDTO> getBalance(@PathVariable("userNo") String userNo) {
        return getBalanceFromDepository(userNo);
    }

    @Override
    @GetMapping("/my/borrowers/{id}")
    @ApiOperation("获取借款人用户信息")
    @ApiImplicitParam(name = "id", value = "用户标识", required = true, dataType = "Long", paramType = "path")
    public RestResponse<BorrowerDTO> getBorrower(@PathVariable("id") Long id) {
        return RestResponse.success(consumerService.getBorrower(id));
    }

    @Override
    @GetMapping("/my/consumers")
    @ApiOperation("获取当前登录用户信息(给前端用)")
    public RestResponse<ConsumerDTO> getMyConsumer() {
        ConsumerDTO consumerDTO = consumerService.getByMobile(SecurityUtil.getUser().getMobile());
        return RestResponse.success(consumerDTO);
    }

    @Override
    @GetMapping("/l/currConsumer")
    @ApiOperation("获取当前登录用户信息(给其他微服务用)")
    public RestResponse<ConsumerDTO> getCurrConsumer() {
        ConsumerDTO consumerDTO = consumerService.getByMobile(SecurityUtil.getUser().getMobile());
        return RestResponse.success(consumerDTO);
    }

    @Override
    @GetMapping("/my/withdraw-records")
    @ApiOperation("生成提现请求数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "金额", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "callbackUrl", value = "通知结果回调URL", required = true, dataType = "string", paramType = "query")
    })
    public RestResponse<GatewayRequest> createWithdrawRecord(
            @RequestParam("amount") String amount,
            @RequestParam("callbackUrl") String callbackUrl) {
        return consumerService.createWithdrawRecord(amount, callbackUrl, SecurityUtil.getUser().getMobile());
    }

    @Override
    @GetMapping("/my/recharge-records")
    @ApiOperation("生成充值请求数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "金额", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "callbackUrl", value = "通知结果回调URL", required = true, dataType = "string", paramType = "query")
    })
    public RestResponse<GatewayRequest> createRechargeRecord(
            @RequestParam("amount") String amount,
            @RequestParam("callbackUrl") String callbackUrl) {
        return consumerService.createRechargeRecord(amount, callbackUrl, SecurityUtil.getUser().getMobile());
    }

    @Override
    @PostMapping("/my/consumers")
    @ApiOperation("（开户）生成开户请求数据")
    @ApiImplicitParam(name = "consumerRequest", value = "开户信息", required = true, dataType = "ConsumerRequest", paramType = "body")
    public RestResponse<GatewayRequest> createConsumer(@RequestBody ConsumerRequest consumerRequest) throws IOException {
        consumerRequest.setMobile(SecurityUtil.getUser().getMobile());
        return consumerService.createConsumer(consumerRequest);
    }

    @Override
    @PostMapping("/consumers")
    @ApiOperation("用户注册")
    @ApiImplicitParam(name = "consumerRegisterDTO", value = "注册信息", required = true, dataType = "AccountRegisterDTO", paramType = "body")
    public RestResponse register(@RequestBody ConsumerRegisterDTO consumerRegisterDTO) {
        consumerService.register(consumerRegisterDTO);
        return RestResponse.success();
    }

    @ApiOperation("过网关受保护资源，进行认证拦截测试")
    @ApiImplicitParam(name = "jsonToken", value = "访问令牌", required = true, dataType = "String")
    @GetMapping(value = "/m/consumers/test")
    public RestResponse<String> testResources(String jsonToken) {
        return RestResponse.success(EncryptUtil.decodeUTF8StringBase64(jsonToken));
    }

    /**
     * 直接调用银行存管系统查询用户可用余额
     *
     * @param userNo
     * @return
     */
    private RestResponse<BalanceDetailsDTO> getBalanceFromDepository(String userNo) {
        String url = depositoryUrl + "/balance-details/" + userNo;
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                BalanceDetailsDTO balanceDetailsDTO = JSON.parseObject(responseBody, BalanceDetailsDTO.class);
                return RestResponse.success(balanceDetailsDTO);
            }
        } catch (IOException e) {
            log.warn("调用存管系统{}获取余额失败 ", url);
        }
        return RestResponse.validfail("获取失败");
    }
}
