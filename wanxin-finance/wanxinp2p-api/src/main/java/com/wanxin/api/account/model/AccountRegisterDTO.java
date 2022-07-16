package com.wanxin.api.account.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Administrator
 * @Date 2022/6/6 19:26
 */
@Data
@ApiModel(value = "AccountRegisterDTO", description = "账户注册信息")
public class AccountRegisterDTO {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;
}
