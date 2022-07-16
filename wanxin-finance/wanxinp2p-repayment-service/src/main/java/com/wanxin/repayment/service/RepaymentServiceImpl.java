package com.wanxin.repayment.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wanxin.api.consumer.model.BorrowerDTO;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.repayment.model.EqualInterestRepayment;
import com.wanxin.api.repayment.model.RepaymentDetailRequest;
import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.api.transaction.model.TenderDTO;
import com.wanxin.common.domain.BusinessException;
import com.wanxin.common.domain.CodePrefixCode;
import com.wanxin.common.domain.DepositoryReturnCode;
import com.wanxin.common.domain.PreprocessBusinessTypeCode;
import com.wanxin.common.domain.RepaymentWayCode;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.common.domain.StatusCode;
import com.wanxin.common.util.CodeNoUtil;
import com.wanxin.common.util.DateUtil;
import com.wanxin.repayment.agent.ConsumerApiAgent;
import com.wanxin.repayment.agent.DepositoryAgentApiAgent;
import com.wanxin.repayment.entity.ReceivableDetail;
import com.wanxin.repayment.entity.ReceivablePlan;
import com.wanxin.repayment.entity.RepaymentDetail;
import com.wanxin.repayment.entity.RepaymentPlan;
import com.wanxin.repayment.mapper.ReceivableDetailMapper;
import com.wanxin.repayment.mapper.ReceivablePlanMapper;
import com.wanxin.repayment.mapper.RepaymentDetailMapper;
import com.wanxin.repayment.mapper.RepaymentPlanMapper;
import com.wanxin.repayment.message.RepaymentProducer;
import com.wanxin.repayment.sms.SmsService;
import com.wanxin.repayment.utils.RepaymentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Administrator
 * @Date 2022/6/28 12:54
 */
@Service
@Slf4j
public class RepaymentServiceImpl implements RepaymentService {

    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;
    @Autowired
    private ReceivablePlanMapper receivablePlanMapper;
    @Autowired
    private RepaymentDetailMapper repaymentDetailMapper;
    @Autowired
    private ReceivableDetailMapper receivableDetailMapper;
    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;
    @Autowired
    private RepaymentProducer repaymentProducer;
    @Autowired
    private ConsumerApiAgent consumerApiAgent;
    @Autowired
    private SmsService smsService;

    /**
     * 查询还款人相关信息，并调用发送短信接口进行还款提醒
     *
     * @param date
     */
    @Override
    public void sendRepaymentNotify(String date) {
        //1、查询到期的还款计划
        List<RepaymentPlan> repaymentPlanList = selectDueRepaymentList(date);
        //2、遍历还款计划
        repaymentPlanList.forEach(repaymentPlan -> {
            //3、得到还款人的信息
            RestResponse<BorrowerDTO> consumerResponse = consumerApiAgent.getBorrowerMobile(repaymentPlan.getConsumerId());
            //4、得到还款人的手机号
            String mobile = consumerResponse.getResult().getMobile();
            //5、发送还款短信
            smsService.sendRepaymentNotify(mobile, date, repaymentPlan.getAmount());
        });
    }

    @Override
    public void executeRepayment(String date, int shardingCount, int shardingItem) {
        //1、查询到期的还款计划
        List<RepaymentPlan> repaymentPlanList = selectDueRepayment(date, shardingCount, shardingItem);

        //2、生成还款明细
        repaymentPlanList.forEach(repaymentPlan -> {
            RepaymentDetail repaymentDetail = saveRepaymentDetail(repaymentPlan);
            System.out.println("当前分片：" + shardingItem + "\n" + repaymentPlan);

            //3、还款预处理
            String preRequestNo = repaymentDetail.getRequestNo();
            //还款预处理-冻结借款人应还金额，同时记录一条交易记录
            Boolean preRepaymentResult = preRepayment(repaymentPlan, preRequestNo);
            if (preRepaymentResult) {
                // 构造还款信息请求数据
                RepaymentRequest repaymentRequest = generateRepaymentRequest(repaymentPlan, preRequestNo);
                // 发送确认还款事务消息
                repaymentProducer.confirmRepayment(repaymentPlan, repaymentRequest);
            }
        });
    }

    /**
     * 远程调用确认还款接口
     *
     * @param repaymentPlan
     * @param repaymentRequest
     */
    @Override
    public void invokeConfirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest) {
        RestResponse<String> repaymentResponse = depositoryAgentApiAgent.confirmRepayment(repaymentRequest);

        if (!DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(repaymentResponse.getResult())) {
            throw new RuntimeException("还款失败");
        }
    }

    /**
     * 构造还款信息请求数据
     *
     * @param repaymentPlan
     * @param preRequestNo
     * @return
     */
    private RepaymentRequest generateRepaymentRequest(RepaymentPlan repaymentPlan, String preRequestNo) {
        // 根据还款计划id, 获取应收计划
        final List<ReceivablePlan> receivablePlanList =
                receivablePlanMapper.selectList(Wrappers.<ReceivablePlan>lambdaQuery()
                        .eq(ReceivablePlan::getRepaymentId, repaymentPlan.getId()));
        // 封装请求数据
        RepaymentRequest repaymentRequest = new RepaymentRequest();
        // 还款总额
        repaymentRequest.setAmount(repaymentPlan.getAmount());
        // 业务实体id
        repaymentRequest.setId(repaymentPlan.getId());
        // 向借款人收取的佣金
        repaymentRequest.setCommission(repaymentPlan.getCommission());
        // 标的编码
        repaymentRequest.setProjectNo(repaymentPlan.getProjectNo());
        // 请求流水号
        repaymentRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        // 预处理业务流水号
        repaymentRequest.setPreRequestNo(preRequestNo);
        // 借款人还款明细（投资人收款明细）
        List<RepaymentDetailRequest> detailRequests = new ArrayList<>();
        receivablePlanList.forEach(receivablePlan -> {
            RepaymentDetailRequest detailRequest = new RepaymentDetailRequest();
            // 投资人用户编码
            detailRequest.setUserNo(receivablePlan.getUserNo());
            // 向投资人收取的佣金
            detailRequest.setCommission(receivablePlan.getCommission());
            // 派息 - 无
            // 投资人应得本金
            detailRequest.setAmount(receivablePlan.getPrincipal());
            // 投资人应得利息
            detailRequest.setInterest(receivablePlan.getInterest());
            // 添加到集合
            detailRequests.add(detailRequest);
        });
        // 还款明细请求信息
        repaymentRequest.setDetails(detailRequests);
        return repaymentRequest;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest) {
        //1、更新借款人还款明细repayment_detail：已同步
        String preRequestNo = repaymentRequest.getPreRequestNo();//预处理流水号
        repaymentDetailMapper.update(null,
                Wrappers.<RepaymentDetail>lambdaUpdate().set(RepaymentDetail::getStatus,
                StatusCode.STATUS_IN.getCode()).eq(RepaymentDetail::getRequestNo, preRequestNo));

        //2、更新投资人应收计划receivable_plan：已收
        //根据还款计划id，查询应收计划
        List<ReceivablePlan> receivablePlanList = receivablePlanMapper.selectList(
                Wrappers.<ReceivablePlan>lambdaQuery()
                        .eq(ReceivablePlan::getRepaymentId, repaymentPlan.getId()));
        receivablePlanList.forEach(receivablePlan -> {
            receivablePlan.setReceivableStatus(1);
            receivablePlanMapper.updateById(receivablePlan);

            //3、保存数据到投资人实收明细receivable_detail
            // 构造应收明细
            ReceivableDetail receivableDetail = new ReceivableDetail();
            // 应收项标识
            receivableDetail.setReceivableId(receivablePlan.getId());
            // 实收本息
            receivableDetail.setAmount(receivablePlan.getAmount());
            // 实收时间
            receivableDetail.setReceivableDate(DateUtil.now());
            // 保存投资人实收明细
            receivableDetailMapper.insert(receivableDetail);
        });


        //4、更新借款人还款计划repayment_plan：已还款
        repaymentPlan.setRepaymentStatus("1");
        int rows = repaymentPlanMapper.updateById(repaymentPlan);//受影响的行数
        return rows > 0;
    }

    /**
     * 还款预处理-冻结借款人应还金额，同时记录一条交易记录
     *
     * @param repaymentPlan 还款计划
     * @param preRequestNo 流水号
     * @return
     */
    @Override
    public Boolean preRepayment(RepaymentPlan repaymentPlan, String preRequestNo) {
        //1、构造请求数据
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = generateUserAutoPreTransactionRequest(repaymentPlan, preRequestNo);
        //2、请求存管代理服务
        final RestResponse<String> restResponse = depositoryAgentApiAgent.userAutoPreTransaction(userAutoPreTransactionRequest);
        //3、返回结果
        return DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(restResponse.getResult());
    }

    /**
     * 构造存管代理服务预处理请求数据
     *
     * @param repaymentPlan
     * @param preRequestNo
     * @return
     */
    private UserAutoPreTransactionRequest generateUserAutoPreTransactionRequest(RepaymentPlan repaymentPlan, String preRequestNo) {
        // 构造请求数据
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = new UserAutoPreTransactionRequest();
        // 冻结金额
        userAutoPreTransactionRequest.setAmount(repaymentPlan.getAmount());
        // 预处理业务类型
        userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.REPAYMENT.getCode());
        // 标的号
        userAutoPreTransactionRequest.setProjectNo(repaymentPlan.getProjectNo());
        // 请求流水号
        userAutoPreTransactionRequest.setRequestNo(preRequestNo);
        // 标的用户编码
        userAutoPreTransactionRequest.setUserNo(repaymentPlan.getUserNo());
        // 关联业务实体标识
        userAutoPreTransactionRequest.setId(repaymentPlan.getId());
        // 返回结果
        return userAutoPreTransactionRequest;
    }

    @Override
    public RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan) {
        //1、进行查询
        RepaymentDetail repaymentDetail = repaymentDetailMapper.selectOne(
                Wrappers.<RepaymentDetail>lambdaQuery().eq(RepaymentDetail::getRepaymentPlanId, repaymentPlan.getId()));
        //2、查不到数据才进行保存
        if (repaymentDetail == null) {
            repaymentDetail = new RepaymentDetail();
            // 还款计划项标识
            repaymentDetail.setRepaymentPlanId(repaymentPlan.getId());
            // 实还本息
            repaymentDetail.setAmount(repaymentPlan.getAmount());
            // 实际还款时间
            repaymentDetail.setRepaymentDate(LocalDateTime.now());
            // 请求流水号
            repaymentDetail.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
            // 未同步
            repaymentDetail.setStatus(StatusCode.STATUS_OUT.getCode());
            // 保存数据
            repaymentDetailMapper.insert(repaymentDetail);
        }

        return repaymentDetail;
    }


    @Override
    public List<RepaymentPlan> selectDueRepayment(String date, int shardingCount, int shardingItem) {
        return repaymentPlanMapper.selectDueRepayment(date, shardingCount, shardingItem);
    }

    @Override
    public List<RepaymentPlan> selectDueRepaymentList(String date) {
        return repaymentPlanMapper.selectDueRepaymentList(date);
    }

//    private List<RepaymentPlanDTO> convertEntityList2DtoList(List<RepaymentPlan> repaymentPlanList) {
//        if (repaymentPlanList == null) {
//            return null;
//        }
//
//        List<RepaymentPlanDTO> repaymentPlanDTOList = new ArrayList<>();
//        repaymentPlanList.forEach(repaymentPlan -> {
//            RepaymentPlanDTO repaymentPlanDTO = new RepaymentPlanDTO();
//            BeanUtils.copyProperties(repaymentPlan, repaymentPlanDTO);
//            repaymentPlanDTOList.add(repaymentPlanDTO);
//        });
//
//        return repaymentPlanDTOList;
//    }

    /**
     * 启动还款
     * 涉及两个数据库的插入操作
     * @param projectWithTendersDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public String startRepayment(ProjectWithTendersDTO projectWithTendersDTO) {
        //1、生成借款人还款计划
        //1.1、获取标的信息
        ProjectDTO projectDTO = projectWithTendersDTO.getProject();
        //1.2、获取投标信息
        List<TenderDTO> tenders = projectWithTendersDTO.getTenders();
        //1.3、计算还款的月数
        double ceil = Math.ceil(projectDTO.getPeriod() / 30.0);  //向上取整
        int month = (int) ceil;
        //1.4、还款方式，只针对等额本息
        String repaymentWay = projectDTO.getRepaymentWay();
        if (repaymentWay.equals(RepaymentWayCode.FIXED_REPAYMENT.getCode())) {
            //1.5、生成还款计划
            EqualInterestRepayment fixedRepayment = RepaymentUtil.fixedRepayment(projectDTO.getAmount(), projectDTO.getBorrowerAnnualRate(), month, projectDTO.getCommissionAnnualRate());
            //1.6、保存还款计划，涉及数据库 repayment_plan（借款人还款计划）
            List<RepaymentPlan> planList = saveRepaymentPlan(projectDTO, fixedRepayment);

            //2、生成投资人应收计划
            //2.1、根据投标信息生成应收明细
            tenders.forEach(tender -> {
                // 当前投标人的收款计划
                EqualInterestRepayment receipt = RepaymentUtil.fixedRepayment(tender.getAmount(), tender.getProjectAnnualRate(), month, projectWithTendersDTO.getCommissionInvestorAnnualRate());
                // 由于投标人的收款计划需要还款信息,所有遍历还款计划, 把还款期数与投资人应收期数对应上
                planList.forEach(plan -> {
                    //2.2、保存应收计划到数据库，涉及数据库 receivable_plan （投资人应收计划）
                    saveReceivablePlan(plan, tender, receipt);
                });
            });
        } else {
            return "-1";
        }

        return DepositoryReturnCode.RETURN_CODE_00000.getCode();
    }

    /**
     * 保存还款计划到数据库
     * 涉及数据库 repayment_plan（借款人还款计划）
     *
     * @param projectDTO 标的信息
     * @param fixedRepayment 还款计划
     * @return
     */
    private List<RepaymentPlan> saveRepaymentPlan(ProjectDTO projectDTO, EqualInterestRepayment fixedRepayment) {
        List<RepaymentPlan> repaymentPlanList = new ArrayList<>();
        // 获取每期利息
        final Map<Integer, BigDecimal> interestMap = fixedRepayment.getInterestMap();
        // 平台收取利息
        final Map<Integer, BigDecimal> commissionMap = fixedRepayment.getCommissionMap();

        // 获取每期本金
        fixedRepayment.getPrincipalMap().forEach((k, v) -> {
            // 还款计划封装数据
            final RepaymentPlan repaymentPlan = new RepaymentPlan();
            // 标的id
            repaymentPlan.setProjectId(projectDTO.getId());
            // 发标人用户标识
            repaymentPlan.setConsumerId(projectDTO.getConsumerId());
            // 发标人用户编码
            repaymentPlan.setUserNo(projectDTO.getUserNo());
            // 标的编码
            repaymentPlan.setProjectNo(projectDTO.getProjectNo());
            // 期数
            repaymentPlan.setNumberOfPeriods(k);
            // 当期还款利息
            repaymentPlan.setInterest(interestMap.get(k));
            // 还款本金
            repaymentPlan.setPrincipal(v);
            // 本息 = 本金 + 利息
            repaymentPlan.setAmount(repaymentPlan.getPrincipal().add(repaymentPlan.getInterest()));
            // 应还时间 = 当前时间 + 期数( 单位月 )
            repaymentPlan.setShouldRepaymentDate(DateUtil.localDateTimeAddMonth(DateUtil.now(), k));
            // 应还状态, 当前业务为待还
            repaymentPlan.setRepaymentStatus("0");
            // 计划创建时间
            repaymentPlan.setCreateDate(DateUtil.now());
            // 设置平台佣金( 借款人让利 ) 注意这个地方是 具体佣金
            repaymentPlan.setCommission(commissionMap.get(k));
            // 保存到数据库
            repaymentPlanMapper.insert(repaymentPlan);
            repaymentPlanList.add(repaymentPlan);
        });

        return repaymentPlanList;
    }

    /**
     * 保存应收明细到数据库
     * 涉及数据库 receivable_plan （投资人应收明细）
     *
     * @param repaymentPlan 还款计划
     * @param tender 投标信息
     * @param receipt 当前投标人的收款明细
     */
    private void saveReceivablePlan(RepaymentPlan repaymentPlan, TenderDTO tender, EqualInterestRepayment receipt) {
        // 应收本金
        final Map<Integer, BigDecimal> principalMap = receipt.getPrincipalMap();
        // 应收利息
        final Map<Integer, BigDecimal> interestMap = receipt.getInterestMap();
        // 平台收取利息
        final Map<Integer, BigDecimal> commissionMap = receipt.getCommissionMap();
        // 封装投资人应收明细
        ReceivablePlan receivablePlan = new ReceivablePlan();
        // 投标信息标识
        receivablePlan.setTenderId(tender.getId());
        // 设置期数
        receivablePlan.setNumberOfPeriods(repaymentPlan.getNumberOfPeriods());
        // 投标人用户标识
        receivablePlan.setConsumerId(tender.getConsumerId());
        // 投标人用户编码
        receivablePlan.setUserNo(tender.getUserNo());
        // 还款计划项标识
        receivablePlan.setRepaymentId(repaymentPlan.getId());
        // 应收利息
        receivablePlan.setInterest(interestMap.get(repaymentPlan.getNumberOfPeriods()));
        // 应收本金
        receivablePlan.setPrincipal(principalMap.get(repaymentPlan.getNumberOfPeriods()));
        // 应收本息 = 应收本金 + 应收利息
        receivablePlan.setAmount(receivablePlan.getInterest().add(receivablePlan.getPrincipal()));
        // 应收时间
        receivablePlan.setShouldReceivableDate(repaymentPlan.getShouldRepaymentDate());
        // 应收状态, 当前业务为未收
        receivablePlan.setReceivableStatus(0);
        // 创建时间
        receivablePlan.setCreateDate(DateUtil.now());
        // 设置投资人让利, 注意这个地方是具体: 佣金
        receivablePlan.setCommission(commissionMap.get(repaymentPlan.getNumberOfPeriods()));
        // 保存
        receivablePlanMapper.insert(receivablePlan);
    }
}
