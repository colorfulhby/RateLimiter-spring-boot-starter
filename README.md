# RateLimiter-spring-boot-starter
基于redis的限流插件，分布式锁


@RepeatLimit 支持多种限流方式,可以组合配置使用

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
USER 限流方式需要继承LimitKeyHandler重写getUserKey()实现自己的用户唯一key获取方法
