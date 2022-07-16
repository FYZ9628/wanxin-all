package com.wanxin.repayment.service;

import com.wanxin.api.repayment.model.RepaymentRequest;
import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.repayment.entity.RepaymentDetail;
import com.wanxin.repayment.entity.RepaymentPlan;

import java.util.List;

/**
 * @Author Administrator
 * @Date 2022/6/28 12:52
 */
public interface RepaymentService {

    /**
     * 查询还款人相关信息，并调用发送短信接口进行还款提醒
     *
     * @param date
     */
    void sendRepaymentNotify(String date);

    /**
     * 执行还款
     *
     * @param date 日期 格式：yyyy-MM-dd
     * @param shardingCount 分片数
     * @param shardingItem 服务器数量
     * @return
     */
    void executeRepayment(String date, int shardingCount, int shardingItem);
//    void executeRepayment(String date);

    /**
     * 远程调用确认还款接口
     *
     * @param repaymentPlan
     * @param repaymentRequest
     */
    void invokeConfirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest);

    /**
     * 确认还款处理
     *
     * @param repaymentPlan
     * @param repaymentRequest
     * @return
     */
    Boolean confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest);

    /**
     * 还款预处理-冻结借款人应还金额
     *
     * @param repaymentPlan 还款计划
     * @param preRequestNo 流水号
     * @return
     */
    Boolean preRepayment(RepaymentPlan repaymentPlan, String preRequestNo);

    /**
     * 根据还款计划生成还款明细并保存
     *
     * @param repaymentPlan
     * @return
     */
    RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan);

    /**
     * 根据日期查询所有到期的还款计划
     * 并且根据期数分片
     *
     * @param date 日期 格式：yyyy-MM-dd
     * @param shardingCount 分片数
     * @param shardingItem 服务器数量
     * @return
     */
    List<RepaymentPlan> selectDueRepayment(String date, int shardingCount, int shardingItem);

    /**
     * 根据日期查询所有到期的还款计划
     *
     * @param date 日期 格式：yyyy-MM-dd
     * @return
     */
    List<RepaymentPlan> selectDueRepaymentList(String date);

    /**
     * 启动还款
     *
     * @param projectWithTendersDTO
     * @return
     */
    String startRepayment(ProjectWithTendersDTO projectWithTendersDTO);
}
