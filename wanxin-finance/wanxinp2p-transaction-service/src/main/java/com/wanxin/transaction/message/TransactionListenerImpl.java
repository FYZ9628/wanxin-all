package com.wanxin.transaction.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanxin.common.domain.ProjectCode;
import com.wanxin.transaction.entity.Project;
import com.wanxin.transaction.mapper.ProjectMapper;
import com.wanxin.transaction.service.ProjectService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 开始还款-消息生产者本地事务监听器
 *
 * @Author Administrator
 * @Date 2022/6/29 10:07
 */
@Component
@RocketMQTransactionListener(txProducerGroup = "PID_START_REPAYMENT")
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 执行本地事务
     *
     * @param message
     * @param o
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        //1、解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) message.getPayload()));
        Project project = JSONObject.parseObject(jsonObject.getString("project"), Project.class);

        //2、执行本地事务
        Boolean result = projectService.updateProjectStatusAndStartRepayment(project);

        //3、返回执行结果
        if (result) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 执行事务回查
     *
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        System.out.println("事务回查");

        //1、解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) message.getPayload()));
        Project project = JSONObject.parseObject(jsonObject.getString("project"), Project.class);

        //2、查询标的状态
        Project pro = projectMapper.selectById(project.getId());

        //3、返回结果
        if (pro.getProjectStatus().equals(ProjectCode.REPAYING.getCode())) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
