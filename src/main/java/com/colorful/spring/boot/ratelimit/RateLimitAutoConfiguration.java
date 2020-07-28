package com.colorful.spring.boot.ratelimit;

/**
 * @ClassName:RateLimitAutoConfiguration
 * @Description:
 * @author:hongby
 * @date:2020/7/24 15:51
 */

import com.colorful.spring.boot.ratelimit.config.CacheConfiguration;
import com.colorful.spring.boot.ratelimit.config.RateLimitConfig;
import com.colorful.spring.boot.ratelimit.config.RedisConfiguration;
import com.colorful.spring.boot.ratelimit.config.RepeatLimitConfiguration;
import com.colorful.spring.boot.ratelimit.enums.Constant;
import com.colorful.spring.boot.ratelimit.handler.DefaultLimitKeyHandler;
import com.colorful.spring.boot.ratelimit.handler.LimitKeyHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author hby
 * 2020/7/25 - 10:11
 *  自动装配类
 **/
@Configuration
@ConditionalOnProperty(prefix = Constant.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RateLimitConfig.class)
@Import({RedisConfiguration.class, CacheConfiguration.class, RepeatLimitConfiguration.class})
public class RateLimitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = LimitKeyHandler.class)
    public LimitKeyHandler stringRandomGenerator() {
        return new DefaultLimitKeyHandler();
    }

}
