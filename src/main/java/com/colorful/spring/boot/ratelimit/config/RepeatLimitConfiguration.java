package com.colorful.spring.boot.ratelimit.config;

import com.colorful.spring.boot.ratelimit.aspect.RepeatLimitAspect;
import com.colorful.spring.boot.ratelimit.handler.LimitKeyHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author hby
 * 2020/7/24 - 16:58.
 **/
public class RepeatLimitConfiguration {

    private RedisTemplate<String, String> redisTemplate;
    private LimitKeyHandler limitKeyHandler;

    public RepeatLimitConfiguration(ObjectProvider<RedisTemplate<String, String>> redisTemplate, ObjectProvider<LimitKeyHandler> limitKeyHandlers){
        this.redisTemplate = redisTemplate.getIfAvailable();
        this.limitKeyHandler = limitKeyHandlers.getIfAvailable();
    }

    @Bean
    public RepeatLimitAspect repeatLimitAspect(){
        return new RepeatLimitAspect(redisTemplate, limitRedisScript(), limitKeyHandler);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript<Long> limitRedisScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/limit.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

//    @Bean
//    public LimitExceptionAdvice limitExceptionAdvice(){
//        return new LimitExceptionAdvice();
//    }
}
