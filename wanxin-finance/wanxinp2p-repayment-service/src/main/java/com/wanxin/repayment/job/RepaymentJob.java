package com.wanxin.repayment.job;

import com.wanxin.repayment.service.RepaymentService;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 定时还款任务
 *
 * @Author Administrator
 * @Date 2022/6/30 9:44
 */
@Component
//@ElasticJobScheduler(cron = "0/5 * * * * ?", shardingTotalCount = 2, name = "RepaymentJob")
public class RepaymentJob implements SimpleJob {

    @Autowired
    private RepaymentService repaymentService;

    @Override
    public void execute(ShardingContext shardingContext) {
        int shardingCount = shardingContext.getShardingTotalCount();
        int shardingItem = shardingContext.getShardingItem();
//        System.out.println("定时作业分片shardingItem：" + shardingItem);
        //调用业务层执行还款任务
        repaymentService.executeRepayment(LocalDate.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE), shardingCount, shardingItem);

        //调用业务层执行还款短信提醒、提前一天提醒还款
//        repaymentService.sendRepaymentNotify(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
