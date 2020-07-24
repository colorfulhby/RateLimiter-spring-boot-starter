package com.colorful.spring.boot.ratelimit.annotation;

import com.colorful.spring.boot.ratelimit.config.RedisConfiguration;
import com.colorful.spring.boot.ratelimit.config.RepeatLimitConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hby
 * 2020/7/24 - 16:19.
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAutoConfiguration
@EnableAsync
@Import({RedisConfiguration.class, RepeatLimitConfiguration.class})
public @interface EnableRedis {
}
