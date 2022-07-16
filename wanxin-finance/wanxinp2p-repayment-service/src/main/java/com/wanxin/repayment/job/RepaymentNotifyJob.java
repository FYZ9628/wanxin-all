package com.wanxin.repayment.job;

import com.wanxin.repayment.service.RepaymentService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Administrator
 * @Date 2022/7/5 1:21
 */
@Component
public class RepaymentNotifyJob {
    private Logger logger = LoggerFactory.getLogger(RepaymentNotifyJob.class);

    @Autowired
    private RepaymentService repaymentService;

    @XxlJob(value = "RepaymentNotifyJob", init = "init", destroy = "destroy")
    public ReturnT<String> execute() {
        logger.info("还款短信提醒 execute 任务触发成功：" + LocalDateTime.now());

        //调用业务层执行还款短信提醒、提前一天提醒还款
        repaymentService.sendRepaymentNotify(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));

        return ReturnT.SUCCESS;
    }

    private void init() {
        logger.info("init 方法调用成功");
    }

    private void destroy() {
        logger.info("destroy 方法调用成功");
    }
}
