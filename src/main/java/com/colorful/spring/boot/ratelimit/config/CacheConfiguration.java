package com.colorful.spring.boot.ratelimit.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author hby
 * 2020/07/25 - 17:28.
 **/
public class CacheConfiguration {

    /**
     * 缓存有效期
     */
    private final Duration redisTime2Live = Duration.ofDays(1);

    private GenericToStringSerializer<String> genericToStringSerializer;
    private GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;

    public CacheConfiguration(ObjectProvider<GenericToStringSerializer<String>> genericToStringSerializer, ObjectProvider<GenericJackson2JsonRedisSerializer> genericJackson2JsonRedisSerializer){
        this.genericToStringSerializer = genericToStringSerializer.getIfAvailable();
        this.genericJackson2JsonRedisSerializer = genericJackson2JsonRedisSerializer.getIfAvailable();
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(redisTime2Live)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(genericToStringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer))
                .disableCachingNullValues();
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .transactionAware()
                .build();
    }

    @Bean(name = "i_keyGenerator")
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> method.getName() + "[" + Arrays.asList(params).toString() + "]";
    }
}
