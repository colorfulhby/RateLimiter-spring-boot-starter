package com.colorful.spring.boot.ratelimit.aspect;

import ch.qos.logback.core.CoreConstants;
import com.colorful.spring.boot.ratelimit.annotation.RepeatLimit;
import com.colorful.spring.boot.ratelimit.enums.LimitType;
import com.colorful.spring.boot.ratelimit.exception.RateLimitInvocationException;
import com.colorful.spring.boot.ratelimit.service.LimitKeyService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author hby
 * 2020/7/25 - 16:26.
 **/
@Aspect
public class RepeatLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RepeatLimitAspect.class);
    private static final String REDIS_LIMIT_KEY_PREFIX = "limit:";
    private final RedisScript<Long> limitRedisScript;
    private RedisTemplate<String, String> redisTemplate;
    private LimitKeyService limitKeyService;

    public RepeatLimitAspect(RedisTemplate<String, String> redisTemplate, RedisScript<Long> limitRedisScript, LimitKeyService limitKeyService) {
        this.redisTemplate = redisTemplate;
        this.limitRedisScript = limitRedisScript;
        this.limitKeyService = limitKeyService;
    }

    /**
     * 包含限流注解的controller
     */
    @Pointcut("@annotation(com.colorful.spring.boot.ratelimit.annotation.RepeatLimit)")
    public void repeatLimitPointcut() {
    }

    @Before("repeatLimitPointcut()")
    public void pointcut(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RepeatLimit repeatLimit = method.getAnnotation(RepeatLimit.class);
        long max = repeatLimit.max();
        long timeout = repeatLimit.timeout();
        TimeUnit timeUnit = repeatLimit.timeUnit();

        String limitKey = limitKey(method, repeatLimit.limitType(),joinPoint.getArgs().hashCode());

        boolean limited = shouldLimited(limitKey, max, timeout, timeUnit);
        if (limited) {
            throw new RateLimitInvocationException();
        }
    }

    private boolean shouldLimited(String key, long max, long timeout, TimeUnit timeUnit) {
        // 统一使用单位毫秒
        long ttl = timeUnit.toMillis(timeout);
        // 当前时间毫秒数
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;

        Long executeTimes = redisTemplate.execute(limitRedisScript,
                Collections.singletonList(key), now + "", ttl + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                logger.debug("request is limited, limit_key:{}, timeout_ttl:{}, req_max:{}", key, ttl, max);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取限流key => limit:ip.clazzName.methodName || limit:userName.clazzName.methodName
     *
     * @param method    Method
     * @param limitTypes LimitTypes
     * @param argsHashCode argsHashCode
     * @return String
     */
    private String limitKey(Method method, LimitType[] limitTypes,int argsHashCode) {

        StringBuilder limitKey = new StringBuilder(REDIS_LIMIT_KEY_PREFIX);
        for (LimitType limitType : limitTypes) {
            switch (limitType) {
                case ARGS:
                    limitKey.append(argsHashCode);
                    break;
                case IP:
                    limitKey.append(limitKeyService.getIpKey());
                    break;
                case USER:
                    limitKey.append(limitKeyService.getUserKey());
                    break;
                case METHOD:
                    limitKey.append(method.getDeclaringClass().getName())
                            .append(CoreConstants.DOT)
                            .append(method.getName());
                    break;
                default:
                    // default METHOD
                    break;
            }
            limitKey.append(CoreConstants.DOT);
        }
        return limitKey.toString();
    }


}
