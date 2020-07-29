# RateLimiter-spring-boot-starter
基于redis的限流插件

# 快速开始

> spring boot项目接入


1.添加组件依赖，目前还没上传到公共仓库，需要自己下源码build
```
<dependency>
    <groupId>com.colorfulhby</groupId>
    <artifactId>ratelimiter-spring-boot-starter</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>

```

2.application.yml配置redis相关依赖
```
  redis:
    host: 127.0.0.1
    port: 6379
    password: xxx
    timeout: 10s
    lettuce:
      pool:
        min-idle: 0
        max-idle: 8
        max-active: 8
        max-wait: -1ms
```


3.在需要加分布式锁的方法上，添加注解@RepeatLimit



@RepeatLimit 支持多种（方法，IP，用户，参数）限流方式,可以组合配置使用



```
/**
     * default 针对指定接口方法访问限流
     */
    METHOD,
    /**
     * 根据IP and 指定接口方法 限流
     */
    IP,
    /**
     * 根据用户 and 指定接口方法 限流
     */
    USER,
    /**
     * 根据 请求参数  and 指定接口方法  限流  
     */
    ARGS
```
其中 *USER*   限流方式需要继承LimitKeyHandler重写getUserKey()实现自己的用户唯一key获取方法
