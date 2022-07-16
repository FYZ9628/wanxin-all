package com.wanxin.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanxin.api.account.model.LoginUser;
import com.wanxin.api.consumer.model.BalanceDetailsDTO;
import com.wanxin.api.consumer.model.ConsumerDTO;
import com.wanxin.api.dipository.model.LoanDetailRequest;
import com.wanxin.api.dipository.model.LoanRequest;
import com.wanxin.api.dipository.model.UserAutoPreTransactionRequest;
import com.wanxin.api.transaction.model.ModifyProjectStatusDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.api.transaction.model.ProjectInvestDTO;
import com.wanxin.api.transaction.model.ProjectQueryDTO;
import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.api.transaction.model.TenderDTO;
import com.wanxin.api.transaction.model.TenderOverviewDTO;
import com.wanxin.common.domain.BusinessException;
import com.wanxin.common.domain.CodePrefixCode;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.DepositoryReturnCode;
import com.wanxin.common.domain.PageVO;
import com.wanxin.common.domain.PreprocessBusinessTypeCode;
import com.wanxin.common.domain.ProjectCode;
import com.wanxin.common.domain.RepaymentWayCode;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.common.domain.StatusCode;
import com.wanxin.common.util.CodeNoUtil;
import com.wanxin.common.util.CommonUtil;
import com.wanxin.transaction.agent.ConsumerApiAgent;
import com.wanxin.transaction.agent.ContentSearchApiAgent;
import com.wanxin.transaction.agent.DepositoryAgentApiAgent;
import com.wanxin.transaction.common.constant.TradingCode;
import com.wanxin.transaction.common.constant.TransactionErrorCode;
import com.wanxin.transaction.common.utils.IncomeCalcUtil;
import com.wanxin.transaction.common.utils.SecurityUtil;
import com.wanxin.transaction.entity.Project;
import com.wanxin.transaction.entity.Tender;
import com.wanxin.transaction.mapper.ProjectMapper;
import com.wanxin.transaction.mapper.TenderMapper;
import com.wanxin.transaction.message.TransactionProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Author Administrator
 * @Date 2022/6/21 23:28
 */
@Slf4j
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ConsumerApiAgent consumerApiAgent;
    @Autowired
    private ConfigService configService;
    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;
    @Autowired
    private ContentSearchApiAgent contentSearchApiAgent;
    @Autowired
    private TenderMapper tenderMapper;
    @Autowired
    private TransactionProducer transactionProducer;
    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 审核标的满标放款
     *
     * @param id 标的id
     * @param approveStatus 审核结果：通过/拒绝
     * @param commission 佣金（手续费）
     * @return String
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loansApprovalStatus(Long id, String approveStatus, String commission) {
        // 第一阶段: 1、生成放款明细
        //标的信息
        Project project = getById(id);
        //投标信息
        QueryWrapper<Tender> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Tender::getProjectId, id);
        List<Tender> tenderList = tenderMapper.selectList(queryWrapper);
        // 生成还款明细
        LoanRequest loanRequest = generateLoanRequest(project, tenderList, commission);

        // 第二阶段: 2、放款
        RestResponse<String> restResponse = depositoryAgentApiAgent.confirmLoan(loanRequest);
        if (DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(restResponse.getResult())) {
            // 响应成功, 更新投标信息: 已放款
            updateTenderStatusAlreadyLoan(tenderList);
            // 第三阶段: 3、修改标的业务状态
            // 调用存管代理服务，修改状态为还款中
            // 构造请求参数
            ModifyProjectStatusDTO modifyProjectStatusDTO = new ModifyProjectStatusDTO();
            // 业务实体id
            modifyProjectStatusDTO.setId(project.getId());
            // 业务状态
            modifyProjectStatusDTO.setProjectStatus(ProjectCode.REPAYING.getCode());
            // 请求流水号
            modifyProjectStatusDTO.setRequestNo(loanRequest.getRequestNo());
            modifyProjectStatusDTO.setProjectNo(project.getProjectNo());

            // 执行请求，向存管代理服务发起请求
            RestResponse<String> modifyProjectStatus = depositoryAgentApiAgent.modifyProjectStatus(modifyProjectStatusDTO);
            if (DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(modifyProjectStatus.getResult())) {
                //没引入RocketMQ之前写的，引入RocketMQ后就放到方法 updateProjectStatusAndStartRepayment 中了
//                // 如果处理成功, 就修改标的状态为还款中
//                project.setProjectStatus(ProjectCode.REPAYING.getCode());
////                projectMapper.updateById(project);
//                updateById(project);

                //第四阶段: 4、启动还款
                // 封装调用还款服务请求对象的数据
                ProjectWithTendersDTO projectWithTendersDTO = new ProjectWithTendersDTO();
                // 封装标的信息
                projectWithTendersDTO.setProject(convertProjectEntityToDTO(project));
                // 封装投标信息
                projectWithTendersDTO.setTenders(convertTenderEntityListToDTOList(tenderList));
                // 封装投资人让利
                projectWithTendersDTO.setCommissionInvestorAnnualRate(configService.getCommissionInvestorAnnualRate());
                // 封装借款人让利
                projectWithTendersDTO.setCommissionBorrowerAnnualRate(configService.getCommissionBorrowerAnnualRate());

                //还款服务需要计算然后生成还款计划，这个过程可能比较耗时
                //涉及到分布式事务，通过RocketMQ实现
                transactionProducer.updateProjectStatusAndStartRepayment(project, projectWithTendersDTO);

                // 调用还款服务, 启动还款(生成还款计划, 应收明细)
                return "审核成功";
            } else {
                log.warn("审核满标放款失败-标的ID为: {}, 存管代理服务返回的状态为-{}", project.getId(), restResponse.getResult());
                throw new BusinessException(TransactionErrorCode.E_150113);
            }
        } else {
            log.warn("审核满标放款失败-标的ID为: {}, 存管代理服务返回的状态为-{}", project.getId(), restResponse.getResult());
            throw new BusinessException(TransactionErrorCode.E_150113);
        }
    }

    /**
     * 根据标的及投标信息生成放款明细
     *
     * @param project
     * @param tenderList
     * @param commission
     * @return
     */
    public LoanRequest generateLoanRequest(Project project, List<Tender> tenderList, String commission) {
        LoanRequest loanRequest = new LoanRequest();
        // 设置标的id
        loanRequest.setId(project.getId());
        // 设置平台佣金
        if (StringUtils.isNotBlank(commission)) {
            loanRequest.setCommission(new BigDecimal(commission));
        }
        // 设置标的编码
        loanRequest.setProjectNo(project.getProjectNo());
        // 设置请求流水号(标的不没有需要生成新的)
        loanRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        // 处理放款明细
        List<LoanDetailRequest> details = new ArrayList<>();
        tenderList.forEach(tender -> {
            LoanDetailRequest loanDetailRequest = new LoanDetailRequest();
            // 设置放款金额
            loanDetailRequest.setAmount(tender.getAmount());
            // 设置预处理业务流水号
            loanDetailRequest.setPreRequestNo(tender.getRequestNo());
            details.add(loanDetailRequest);
        });
        // 设置放款明细
        loanRequest.setDetails(details);

        // 返回封装好的数据
        return loanRequest;
    }

    /**
     * 更新投标信息的状态为: 已放款
     *
     * @param tenderList
     */
    private void updateTenderStatusAlreadyLoan(List<Tender> tenderList) {
        tenderList.forEach(tender -> {
            // 设置状态为已放款
            tender.setTenderStatus(TradingCode.LOAN.getCode());
            // 更新数据库
            tenderMapper.updateById(tender);
        });
    }

    private List<TenderDTO> convertTenderEntityListToDTOList(List<Tender> records) {
        if (records == null) {
            return null;
        }

        List<TenderDTO> dtoList = new ArrayList<>();
        records.forEach(tender -> {
            TenderDTO tenderDTO = new TenderDTO();
            BeanUtils.copyProperties(tender, tenderDTO);
            dtoList.add(tenderDTO);
        });

        return dtoList;
    }

    @Override
    public Integer queryQualifications() {
        // 判断是否绑定银行卡
        LoginUser user = SecurityUtil.getUser();
        ConsumerDTO result = consumerApiAgent.getCurrConsumer().getResult();
        if (!result.getIsBindCard().equals(1)) {
            return 0;
        }

        // 判断是否以发标
        // 查看数据库中有没有状态为 0 的，有则返回 0，没有则返回 1
        Integer count = projectMapper.selectCount(new LambdaQueryWrapper<Project>().eq(Project::getConsumerId, result.getId()).eq(Project::getStatus, 0));
        if (!count.equals(0)) {
            return 0;
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Boolean updateProjectStatusAndStartRepayment(Project project) {
//        project.setProjectStatus(ProjectCode.REPAYING.getCode());
//        return projectMapper.updateById(project) == 1;

        // 如果处理成功, 就修改标的状态为还款中
        project.setProjectStatus(ProjectCode.REPAYING.getCode());
//                projectMapper.updateById(project);
        project.setModifyDate(new Date());
        Boolean updateBool = updateById(project);

        return updateBool;
    }

    @Override
    public TenderDTO createTender(ProjectInvestDTO projectInvestDTO) {
        //1、前置条件判断
        //1.1、判断投标金额是否大于最小投标金额
        BigDecimal amount = new BigDecimal(projectInvestDTO.getAmount());
        //获得最小投标金额
        BigDecimal miniInvestmentAmount = configService.getMiniInvestmentAmount();
        if (amount.compareTo(miniInvestmentAmount) < 0) {
            throw new BusinessException(TransactionErrorCode.E_150109);
        }

        //1.2、判断用户账户余额是否足够
        //通过手机号查询用户信息
//        LoginUser user = SecurityUtil.getUser();
        RestResponse<ConsumerDTO> restResponse = consumerApiAgent.getCurrConsumer();
        //通过用户编号查询账户余额
        RestResponse<BalanceDetailsDTO> balance = consumerApiAgent.getBalance(restResponse.getResult().getUserNo());
        BigDecimal myBalance = balance.getResult().getBalance();
        if (myBalance.compareTo(amount) < 0) {
            throw new BusinessException(TransactionErrorCode.E_150112);
        }

        //1.3、判断标的是否瞒标，标的状态为FULL就表示瞒标
//        Project project = projectMapper.selectById(projectInvestDTO.getId());
        Project project = getById(projectInvestDTO.getId());
        if ("FULLY".equalsIgnoreCase(project.getProjectStatus())) {
            throw new BusinessException(TransactionErrorCode.E_150114);
        }

        //1.4、判断投标金额是否超过剩余未投金额
        BigDecimal remainingAmount = getProjectRemainingAmount(project);
        if (amount.compareTo(remainingAmount) < 1) {
            //1.5、判断此次投标后的剩余未投金额是否满足最小投标金额
            // 例如:借款人需要借1万 现在已经投标了8千 还剩2千 本次投标1950元
            // 公式:此次投标后的剩余未投金额 = 目前剩余未投金额 - 本次投标金额
            BigDecimal subtract = remainingAmount.subtract(amount);
            int result = subtract.compareTo(configService.getMiniInvestmentAmount());
            if (result < 0) {
                if (subtract.compareTo(new BigDecimal("0.0")) != 0) {
                    throw new BusinessException(TransactionErrorCode.E_150111);
                }
            }

            //2、保存投标信息并发送给存管代理服务
            // 封装投标信息
            Tender tender = new Tender();
            // 投资人投标金额( 投标冻结金额 )
            tender.setAmount(amount);
            // 投标人用户标识
            tender.setConsumerId(restResponse.getResult().getId());
            tender.setConsumerUsername(restResponse.getResult().getUsername());
            // 投标人用户编码
            tender.setUserNo(restResponse.getResult().getUserNo());
            // 标的标识
            tender.setProjectId(projectInvestDTO.getId());
            // 标的编码
            tender.setProjectNo(project.getProjectNo());
            // 投标状态
            tender.setTenderStatus(TradingCode.FROZEN.getCode());
            // 创建时间
            tender.setCreateDate(new Date());
            // 请求流水号
            tender.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
            // 可用状态
            tender.setStatus(0);
            tender.setProjectName(project.getName());
            // 标的期限(单位:天)
            tender.setProjectPeriod(project.getPeriod());
            // 年化利率(投资人视图)
            tender.setProjectAnnualRate(project.getAnnualRate());
            // 保存到数据库
            tenderMapper.insert(tender);

            // 发送数据给存管代理服务
            // 构造请求数据
            UserAutoPreTransactionRequest userAutoPreTransactionRequest = new UserAutoPreTransactionRequest();
            // 冻结金额
            userAutoPreTransactionRequest.setAmount(amount);
            // 预处理业务类型
            userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.TENDER.getCode());
            // 标的号
            userAutoPreTransactionRequest.setProjectNo(project.getProjectNo());
            // 请求流水号
            userAutoPreTransactionRequest.setRequestNo(tender.getRequestNo());
            // 投资人用户编码
            userAutoPreTransactionRequest.setUserNo(restResponse.getResult().getUserNo());
            // 设置关联业务实体标识
            userAutoPreTransactionRequest.setId(tender.getId());
            // 远程调用存管代理服务
            RestResponse<String> response = depositoryAgentApiAgent.userAutoPreTransaction(userAutoPreTransactionRequest);

            //3、根据结果更新投标状态
            //3.1、判断结果
            if (response != null && DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(response.getResult())) {
                //3.2、修改状态为: 已发布
                tender.setStatus(1);
                tenderMapper.updateById(tender);
                //3.3、投标成功后判断标的是否已投满, 如果满标, 更新标的状态
                BigDecimal remainAmount = getProjectRemainingAmount(project);
                if (remainAmount.compareTo(new BigDecimal(0)) == 0) {
                    project.setProjectStatus(ProjectCode.FULLY.getCode());
//                    projectMapper.updateById(project);
                    project.setModifyDate(new Date());
                    updateById(project);
                }

                //3.4、转换为dto对象并封装数据
                TenderDTO tenderDTO = convertTenderEntityToDTO(tender);
                // 封装标的信息
                project.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getDesc());
                tenderDTO.setProject(convertProjectEntityToDTO(project));
                // 封装预期收益
                // 根据标的期限计算还款月数
                Double ceil = Math.ceil(project.getPeriod() / 30.0);
                Integer month = ceil.intValue();
                // 计算预期收益
                tenderDTO.setExpectedIncome(IncomeCalcUtil.getIncomeTotalInterest(new BigDecimal(projectInvestDTO.getAmount()), configService.getAnnualRate(), month));
                return tenderDTO;
            } else {
                log.warn("投标失败-标的ID: {}-存管代理服务返回的状态为-{}", projectInvestDTO.getId(), restResponse.getResult());
                throw new BusinessException(TransactionErrorCode.E_150113);
            }

        } else {
            throw new BusinessException(TransactionErrorCode.E_150110);
        }
    }

    @Override
    public List<TenderOverviewDTO> queryTendersByProjectId(Long id) {
        List<Tender> tenderList = tenderMapper.selectList(Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, id));
        List<TenderOverviewDTO> tenderOverviewDTOList = new ArrayList<>();
        tenderList.forEach(tender -> {
            TenderOverviewDTO tenderOverviewDTO = new TenderOverviewDTO();
            BeanUtils.copyProperties(tender, tenderOverviewDTO);
            tenderOverviewDTO.setConsumerUsername(CommonUtil.hiddenMobile(tender.getConsumerUsername()));
            tenderOverviewDTOList.add(tenderOverviewDTO);
        });


        return tenderOverviewDTOList;
    }

    @Override
    public List<ProjectDTO> queryProjectsIds(String ids) {
        //1、查询标的信息
        //构件查询对象
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        //获取id列表集合
        List<Long> list = new ArrayList<>();
        Arrays.asList(ids.split(",")).forEach(str->{
            list.add(Long.parseLong(str));
        });
        queryWrapper.lambda().in(Project::getId, list);
        //查询
        List<Project> projects = list(queryWrapper);

        //2、转换为DTO对象
//        for (Project project : projects) {
//            ProjectDTO projectDTO = convertProjectEntityToDTO(project);
//        }
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        projects.forEach(project -> {
            ProjectDTO projectDTO = convertProjectEntityToDTO(project);
            //3、获取剩余额度
            projectDTO.setRemainingAmount(getProjectRemainingAmount(project));
            //4、查询出借人数
            int count = tenderMapper.selectCount(Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, project.getId()));
            projectDTO.setTenderCount(count);
            projectDTOList.add(projectDTO);
        });

        return projectDTOList;
    }

    /**
     * 获取标的剩余可投额度 = 标的额度 - 已投标金额
     *
     * @param project
     * @return
     */
    private BigDecimal getProjectRemainingAmount(Project project) {
        //根据标的id在投标表查询已投金额
        List<BigDecimal> decimalList = tenderMapper.selectAmountInvestedByProjectId(project.getId());
        //求和结果集
        BigDecimal amountInvested = new BigDecimal("0.0");
        for (BigDecimal d : decimalList) {
            amountInvested = amountInvested.add(d);
        }
        //得到剩余额度
        return project.getAmount().subtract(amountInvested);
    }

    @Override
    public PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order, Integer pageNo, Integer pageSize, String sortBy) {
        RestResponse<PageVO<ProjectDTO>> esResponse = contentSearchApiAgent.queryProjectIndex(projectQueryDTO, pageNo, pageSize, sortBy, order);

        if (!esResponse.isSuccessful()) {
            throw new BusinessException(CommonErrorCode.UNKOWN);
        }

        return esResponse.getResult();
    }

    /**
     * 管理员审核标的信息
     *
     * @param id            标的id
     * @param approveStatus 状态
     * @return String
     */
    @Override
    public String projectsApprovalStatus(Long id, String approveStatus) {
        //1、根据id查询标的信息并转换为DTO对象
        Project project = getById(id);
        ProjectDTO projectDTO = convertProjectEntityToDTO(project);

        //2、生成流水号
        // 生成流水号(不存在才生成，实现 DepositoryRecordServiceImpl.handleIdempotent() 方法中的幂等性)
        if (StringUtils.isBlank(project.getRequestNo())) {
            projectDTO.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
            update(Wrappers.<Project>lambdaUpdate()
                    .set(Project::getRequestNo, projectDTO.getRequestNo())
                    .set(Project::getModifyDate, new Date())
                    .eq(Project::getId, id)
            );
        }

        //3、通过feign远程访问存管代理服务，把标的信息传输过去
        RestResponse<String> restResponse =depositoryAgentApiAgent .createProject(projectDTO);
        if (DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(restResponse.getResult())) {
            //4、根据结果修改状态
            int status = Integer.parseInt(approveStatus);
            String projectStatus = "";
            if (status == 2) {
//                projectStatus = ProjectCode.FULLY.getCode();
                projectStatus = ProjectCode.COLLECTING.getCode();
            }

            if (status == 4) {
                projectStatus = ProjectCode.MISCARRY.getCode();
            }
            if (StringUtils.isBlank(projectStatus)) {
                projectStatus = "COLLECTING";
            }

            update(Wrappers.<Project>lambdaUpdate()
                    .set(Project::getStatus, 1)
                    .set(Project::getProjectStatus, projectStatus)
                    .set(Project::getRequestNo, projectDTO.getRequestNo())
                    .set(Project::getModifyDate, new Date())
                    .eq(Project::getId, id)
            );
            return "success";
        }

        //5、如果失败就抛异常
        throw new BusinessException(TransactionErrorCode.E_150113);
    }

    /**
     * 根据分页条件检索标的信息
     *
     * @param projectQueryDTO 查询请求体
     * @param pageNo          页码
     * @param pageSize        数据条数
     * @return
     */
    @Override
    public PageVO<ProjectDTO> queryProjectsByQueryDTO(ProjectQueryDTO projectQueryDTO, Integer pageNo, Integer pageSize) {

        //带条件
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if (projectQueryDTO != null) {
            // 标的类型
            if (StringUtils.isNotBlank(projectQueryDTO.getType())) {
                queryWrapper.lambda().eq(Project::getType, projectQueryDTO.getType());
            }

            // 起止年化利率(投资人) -- 区间
            if (projectQueryDTO.getStartAnnualRate() != null) {
                queryWrapper.lambda().ge(Project::getAnnualRate, projectQueryDTO.getStartAnnualRate());
            }
            if (projectQueryDTO.getEndAnnualRate() != null) {
                queryWrapper.lambda().le(Project::getAnnualRate, projectQueryDTO.getEndAnnualRate());
            }

            // 借款期限 -- 区间
            if (projectQueryDTO.getStartPeriod() != null) {
                queryWrapper.lambda().ge(Project::getPeriod, projectQueryDTO.getStartPeriod());
            }
            if (projectQueryDTO.getEndPeriod() != null) {
                queryWrapper.lambda().le(Project::getPeriod, projectQueryDTO.getEndPeriod());
            }

            // 标的状态
            if (StringUtils.isNotBlank(projectQueryDTO.getProjectStatus())) {
                queryWrapper.lambda().eq(Project::getProjectStatus, projectQueryDTO.getProjectStatus());
            }

            // 标的可用状态
            if (projectQueryDTO.getStatus() != null) {
                queryWrapper.lambda().eq(Project::getStatus, projectQueryDTO.getStatus());
            }
        }

        //分页
        //构造分页对象
        Page<Project> page = new Page<>(pageNo, pageSize);

        //排序
        //默认的排序，根据创建时间降序排序（最近创建的排在最上面）
        queryWrapper.lambda().orderByDesc(Project::getCreateDate);

        //执行查询
        IPage<Project> iPage = page(page, queryWrapper);
//        IPage<Project> iPage = projectMapper.selectPage(page, queryWrapper);

        //封装结果
        List<ProjectDTO> projectDTOList = convertProjectEntityListToDTOList(iPage.getRecords());
        return new PageVO<>(projectDTOList, iPage.getTotal(), pageNo, pageSize);
    }

    private List<ProjectDTO> convertProjectEntityListToDTOList(List<Project> projectList) {
        if (projectList == null) {
            return null;
        }
        List<ProjectDTO> dtoList = new ArrayList<>();
        projectList.forEach(project -> {
            ProjectDTO projectDTO = new ProjectDTO();
            BeanUtils.copyProperties(project, projectDTO);
            dtoList.add(projectDTO);
        });

        return dtoList;
    }

    /**
     * 借款人发标(创建标的)
     *
     * @param projectDTO 标的信息
     * @return
     */
    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        RestResponse<ConsumerDTO> consumer = consumerApiAgent.getCurrConsumer();

        // 设置用户编码
        projectDTO.setUserNo(consumer.getResult().getUserNo());
        // 设置用户id
        projectDTO.setConsumerId(consumer.getResult().getId());
        // 生成标的编码
        projectDTO.setProjectNo(CodeNoUtil.getNo(CodePrefixCode.CODE_PROJECT_PREFIX));
        // 标的状态修改
        projectDTO.setProjectStatus(ProjectCode.COLLECTING.getCode());
        // 标的可用状态修改, 未同步
        projectDTO.setStatus(StatusCode.STATUS_OUT.getCode());
        // 设置标的创建时间
        projectDTO.setCreateDate(new Date());
        // 设置还款方式
        projectDTO.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getCode());
        // 设置标的类型
        projectDTO.setType("NEW");

        Project project = convertProjectDTOToEntity(projectDTO);
        project.setBorrowerAnnualRate(configService.getBorrowerAnnualRate());
        project.setAnnualRate(configService.getAnnualRate());
        // 年化利率(平台佣金, 利差)
        project.setCommissionAnnualRate(configService.getCommissionAnnualRate());
        // 债权转让
        project.setIsAssignment(0);
        // 判断男女
        String sex = Integer.parseInt(consumer.getResult().getIdNumber().substring(16, 17)) % 2 == 0 ? "女士" : "先生";
        // 构造借款次数查询条件
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<Project>().eq(Project::getConsumerId, consumer.getResult().getId());
//        // 设置标的名字, 姓名 + 性别 + 第N次借款
//        project.setName(consumer.getResult().getFullname() + sex + "第" + (projectMapper.selectCount(eq) + 1) + "次借款");
//        projectMapper.insert(project);

        //  用 ServiceImpl 中的方法
        // 设置标的名字, 姓名 + 性别 + 第N次借款
        project.setName(consumer.getResult().getFullname() + sex + "第" + (count(queryWrapper) + 1) + "次借款");
        save(project);

        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        return projectDTO;
    }

    private Project convertProjectDTOToEntity(ProjectDTO projectDTO) {
        if (projectDTO == null) {
            return null;
        }
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);
        return project;
    }

    private ProjectDTO convertProjectEntityToDTO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }

    private TenderDTO convertTenderEntityToDTO(Tender tender) {
        if (tender == null) {
            return null;
        }

        TenderDTO tenderDTO = new TenderDTO();
        BeanUtils.copyProperties(tender, tenderDTO);
        return tenderDTO;
    }
}
