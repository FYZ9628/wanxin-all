package com.wanxin.api.account.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Administrator
 * @Date 2022/6/6 19:24
 */
@Data
@ApiModel(value = "AccountDTO", description = "账户信息")
public class AccountDTO {

    @ApiModelProperty("标识")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("账号状态")
    private Integer status;

    @ApiModelProperty(" 域(c：c端用户；b：b端用户)")
    private String domain;
}
