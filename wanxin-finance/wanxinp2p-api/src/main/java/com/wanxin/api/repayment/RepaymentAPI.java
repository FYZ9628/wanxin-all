package com.wanxin.api.repayment;

import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.common.domain.RestResponse;

/**
 * @Author Administrator
 * @Date 2022/6/28 0:21
 */
public interface RepaymentAPI {
    /**
     * 启动还款
     *
     * @param projectWithTendersDTO
     * @return
     */
    RestResponse<String> startRepayment(ProjectWithTendersDTO projectWithTendersDTO);
}
