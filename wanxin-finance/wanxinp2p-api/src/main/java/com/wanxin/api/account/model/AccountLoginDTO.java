package com.wanxin.api.account.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Administrator
 * @Date 2022/6/6 19:28
 */
@Data
@ApiModel(value = "AccountLoginDTO", description = "账户登录信息")
public class AccountLoginDTO {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("域(c：c端用户；b：b端用户)")
    private String domain;
}
