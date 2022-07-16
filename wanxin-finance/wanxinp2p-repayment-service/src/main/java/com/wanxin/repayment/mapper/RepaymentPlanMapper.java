package com.wanxin.repayment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanxin.repayment.entity.RepaymentPlan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 借款人还款计划 Mapper 接口
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Repository
public interface RepaymentPlanMapper extends BaseMapper<RepaymentPlan> {
    /**
     * 根据日期查询所有到期的还款计划
     *
     * @return
     */
    @Select("SELECT * FROM repayment_plan WHERE DATE_FORMAT(SHOULD_REPAYMENT_DATE, '%Y-%m-%d') = #{date} AND REPAYMENT_STATUS = '0'")
    List<RepaymentPlan> selectDueRepaymentList(@Param("date") String date);

    /**
     * 根据日期查询所有到期的还款计划
     * 并且根据期数分片
     *
     * @return
     */
    @Select("SELECT * FROM repayment_plan WHERE DATE_FORMAT(SHOULD_REPAYMENT_DATE, '%Y-%m-%d') = #{date} AND REPAYMENT_STATUS = '0' AND MOD(NUMBER_OF_PERIODS, #{shardingCount}) = #{shardingItem}")
    List<RepaymentPlan> selectDueRepayment(@Param("date") String date, @Param("shardingCount") int shardingCount, @Param("shardingItem") int shardingItem);
}
