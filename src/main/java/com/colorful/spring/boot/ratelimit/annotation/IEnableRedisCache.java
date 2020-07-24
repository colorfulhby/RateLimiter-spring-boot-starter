package com.colorful.spring.boot.ratelimit.annotation;

import com.colorful.spring.boot.ratelimit.config.CacheConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hby
 * 2020/7/25 - 17:37.
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAutoConfiguration
@EnableRedis
@Import({CacheConfiguration.class})
@interface IEnableRedisCache {
}
