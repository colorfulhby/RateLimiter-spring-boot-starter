package com.colorful.spring.boot.ratelimit.annotation;


import com.colorful.spring.boot.ratelimit.enums.LimitType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author hby
 * 2020/7/24 - 16:19.
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatLimit {

    /**
     * max timeout时间内最大请求数
     */
    long max() default 10;

    /**
     * 超时时长，默认5秒
     */
    long timeout() default 5;

    /**
     * 超时时间单位，默认 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    LimitType limitType() default LimitType.USER;

//    ReturnCodeType returnCodeType() default ReturnCodeType.REQUEST_LIMIT;
}
