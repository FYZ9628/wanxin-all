package com.wanxin.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxin.account.entity.Account;
import com.wanxin.api.account.model.AccountDTO;
import com.wanxin.api.account.model.AccountLoginDTO;
import com.wanxin.api.account.model.AccountRegisterDTO;
import com.wanxin.common.domain.RestResponse;

/**
 * @Author Administrator
 * @Date 2022/6/4 12:33
 */
public interface AccountService extends IService<Account> {

    /**
     * 用户登录
     *
     * @param accountLoginDTO 登录信息
     * @return
     */
    AccountDTO login(AccountLoginDTO accountLoginDTO);

    /**
     * 保存账户
     *
     * @param accountRegisterDTO 注册用户实体类
     * @return
     */
    AccountDTO registry(AccountRegisterDTO accountRegisterDTO);

    /**
     * 手机号与验证码校验
     *
     * @param mobile 手机号
     * @param key    秘钥
     * @param code   验证码
     * @return
     */
    Integer checkMobile(String mobile, String key, String code);

    /**
     * 获取手机验证码
     *
     * @param mobile 手机号
     * @return
     */
    RestResponse getSMSCode(String mobile);
}
