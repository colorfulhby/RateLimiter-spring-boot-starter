package com.colorful.spring.boot.ratelimit.config;


import com.colorful.spring.boot.ratelimit.service.RedisService;
import com.colorful.spring.boot.ratelimit.service.impl.RedisServiceImpl;
import com.colorful.spring.boot.ratelimit.util.RedisContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

/**
 * @author hby
 * 2020/7/24 - 16:58.
 **/
public class RedisConfiguration {

    private RedisConnectionFactory redisConnectionFactory;

    public RedisConfiguration(ObjectProvider<RedisConnectionFactory> redisConnectionFactory, ObjectProvider<GenericJackson2JsonRedisSerializer> genericJackson2JsonRedisSerializer){
        this.redisConnectionFactory = redisConnectionFactory.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate redisTemplate() {

        RedisTemplate redis = new RedisTemplate();
        GenericToStringSerializer<String> keySerializer = new GenericToStringSerializer<String>(String.class);
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        redis.setKeySerializer(keySerializer);
        redis.setHashKeySerializer(keySerializer);
        redis.setValueSerializer(valueSerializer);
        redis.setHashValueSerializer(valueSerializer);
        redis.setConnectionFactory(redisConnectionFactory);
        return redis;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public GenericToStringSerializer<String> stringRedisSerializer(){
        return new GenericToStringSerializer<>(String.class);
    }

    @Bean
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RedisService redisServiceImpl(RedisTemplate redisTemplate){
        return new RedisServiceImpl(redisTemplate);
    }


    @Bean
    @SuppressWarnings("unchecked")
    public RedisContext redisContext(){
        return new RedisContext(redisTemplate());
    }
}
