# 腾讯云短信服务(tencent-sms)

[![GitHub license](https://img.shields.io/github/license/mikuhuyo/tencent-sms)](https://github.com/mikuhuyo/tencent-sms/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/mikuhuyo/tencent-sms)](https://github.com/mikuhuyo/tencent-sms/issues)
[![GitHub stars](https://img.shields.io/github/stars/mikuhuyo/tencent-sms)](https://github.com/mikuhuyo/tencent-sms/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/mikuhuyo/tencent-sms)](https://github.com/mikuhuyo/tencent-sms/network)
![Java version](https://img.shields.io/badge/Jdk-11-yellow)
![SpringBoot version](https://img.shields.io/badge/SpringBoot-2.1.13-brightgreen)

## 前言

这是我自己的一个学习项目.

我裂开了 :)

所需技术: 

1)SpringBoot

2)Swagger2

3)Redis

## 启动方式

### 环境配置-容器创建

```shell script
# 创建redis容器
docker pull redis:4

docker run --name redis -p 6379:6379  \
-d redis:4 \
--requirepass "yueliminvc@outlook.com" \
--appendonly yes
```

### 环境配置-启动服务

修改`application.yml`文件中, 关于`Redis`以及腾讯云的相关配置(如果没有开通腾讯云就不同修改).

```yaml
# 腾讯云秘钥(默认没有开启秘钥)
sms:
  qcloud:
    appId: 12345678
    appKey: 'yueliminvc888888ddddd'
    templateId: '123456'
    sign: 'fake'
```

启动`TencentSmsApplication`.

## 使用手册

接口地址: http://127.0.0.1:56085/tencent/swagger-ui.html

## 特别鸣谢

### 关注者

[![Stargazers repo roster for @mikuhuyo/tencent-sms](https://reporoster.com/stars/mikuhuyo/tencent-sms)](https://github.com/mikuhuyo/tencent-sms/stargazers)

### 收藏者

[![Forkers repo roster for @mikuhuyo/tencent-sms](https://reporoster.com/forks/mikuhuyo/tencent-sms)](https://github.com/mikuhuyo/tencent-sms/network/members)

## 整理不易-请这个b喝杯水?

![Alipay](./doc/images/alipays.png)

---

![WeChatPay](./doc/images/wechats.png)
