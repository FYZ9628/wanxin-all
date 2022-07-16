package com.wanxin.repayment.controller;

import com.wanxin.api.repayment.RepaymentAPI;
import com.wanxin.api.transaction.model.ProjectWithTendersDTO;
import com.wanxin.common.domain.RestResponse;
import com.wanxin.repayment.service.RepaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 还款微服务
 *
 * @Author Administrator
 * @Date 2022/6/28 0:22
 */
@RestController
@Api(value = "还款服务", tags = "repayment")
public class RepaymentController implements RepaymentAPI {

    @Autowired
    private RepaymentService repaymentService;

    /**
     * 没有给其他服务调用，startRepayment() 这个方法已经通过 rocketMQ 调用了
     * 这个接口就用不到了
     *
     * @param projectWithTendersDTO
     * @return
     */
    @Override
    @PostMapping("/l/start-repayment")
    @ApiOperation("启动还款")
    @ApiImplicitParam(name = "projectWithTendersDTO", value = "通过id获取标的信息", required = true, dataType = "ProjectWithTendersDTO", paramType = "body")
    public RestResponse<String> startRepayment(@RequestBody ProjectWithTendersDTO projectWithTendersDTO) {
        String result = repaymentService.startRepayment(projectWithTendersDTO);
        return RestResponse.success(result);
    }

//    @ApiOperation("测试用户还款")
//    @GetMapping("/execute-repayment/{date}")
//    public void testExecuteRepayment(@PathVariable String date) {
//        repaymentService.executeRepayment(date);
//    }

//    @ApiOperation("测试还款短信提醒")
//    @GetMapping("/repayment-notify/{date}")
//    public void testRepaymentNotify(@PathVariable String date) {
//        repaymentService.sendRepaymentNotify(date);
//    }
}
