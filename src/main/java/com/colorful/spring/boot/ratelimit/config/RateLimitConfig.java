package com.colorful.spring.boot.ratelimit.config;

import com.colorful.spring.boot.ratelimit.enums.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName:RateLimitConfig
 * @Description:
 * @author:hongby
 * @date:2020/7/24 15:36
 *
 *
 */
@ConfigurationProperties(prefix = Constant.PREFIX)
public class RateLimitConfig {

}
