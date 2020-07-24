package com.colorful.spring.boot.ratelimit.enums;

/**
 * @author hby
 * 2020/7/24 - 17:03.
 **/
public enum LimitType {

    /**
     * 根据IP限流
     */
    IP,
    /**
     * 根据用户限流
     */
    USER,
    /**
     * 根据 请求参数限流，注意post的请求体不作为参数限流指标，仅取url上的参数
     */
    ARGS,
    /**
     * 根据用户-请求参数限流，注意post的请求体不作为参数限流指标，仅取url上的参数
     */
    ARGS_AND_USER,
    /**
     * 针对接口访问限流，不区分IP或用户
     */
    ALL
}
