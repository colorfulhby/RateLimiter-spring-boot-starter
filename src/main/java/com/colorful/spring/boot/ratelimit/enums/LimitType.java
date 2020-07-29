package com.colorful.spring.boot.ratelimit.enums;

/**
 * @author hby
 * 2020/7/24 - 17:03.
 **/
public enum LimitType{


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

}
