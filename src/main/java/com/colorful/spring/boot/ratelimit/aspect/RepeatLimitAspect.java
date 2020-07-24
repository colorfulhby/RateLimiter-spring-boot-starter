package com.colorful.spring.boot.ratelimit.aspect;

import ch.qos.logback.core.CoreConstants;
import com.colorful.spring.boot.ratelimit.annotation.RepeatLimit;
import com.colorful.spring.boot.ratelimit.enums.LimitType;
import com.colorful.spring.boot.ratelimit.exception.RateLimitInvocationException;
import com.colorful.spring.boot.ratelimit.service.UserKeyService;
import com.colorful.spring.boot.ratelimit.util.RequestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author chenyj
 * 2020/6/18 - 16:26.
 **/
@Aspect
public class RepeatLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RepeatLimitAspect.class);
    private static final String REDIS_LIMIT_KEY_PREFIX = "limit:";
    private final RedisScript<Long> limitRedisScript;
    private RedisTemplate<String, String> redisTemplate;
    private UserKeyService userKeyService;

    public RepeatLimitAspect(RedisTemplate<String, String> redisTemplate, RedisScript<Long> limitRedisScript, UserKeyService userKeyService) {
        this.redisTemplate = redisTemplate;
        this.limitRedisScript = limitRedisScript;
        this.userKeyService = userKeyService;
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
        String limitKey = limitKey(method, repeatLimit.limitType());

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

        Long executeTimes = redisTemplate.execute(limitRedisScript, Collections.singletonList(key), now + "", ttl + "", expired + "", max + "");
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
     * @param limitType LimitType
     * @return String
     */
    private String limitKey(Method method, LimitType limitType) {

        StringBuilder sb = new StringBuilder(REDIS_LIMIT_KEY_PREFIX);

        switch (limitType) {
            case USER:
                sb.append(userKeyService.getUserKey()).append(CoreConstants.DOT);
                break;
            case ARGS:
                getRequestArgs(sb);
                break;
            case ARGS_AND_USER:
                sb.append(userKeyService.getUserKey()).append(CoreConstants.DOT);
                getRequestArgs(sb);
                break;
            case IP:
                sb.append(RequestUtils.getReqIp()).append(CoreConstants.DOT);
                break;
            case ALL:
                break;
            default:
                // default USER
                sb.append(userKeyService.getUserKey()).append(CoreConstants.DOT);
                break;
        }
        sb.append(method.getDeclaringClass().getName()).append(CoreConstants.DOT).append(method.getName());
        return sb.toString();
    }


    private static void getRequestArgs(StringBuilder sb){
        HttpServletRequest req = RequestUtils.getRequest();
        if(null != req){
            Map<String, String[]> parameterMap = req.getParameterMap();
            if(!CollectionUtils.isEmpty(parameterMap)){
                ArrayList<String> list = new ArrayList<>();
                for (Map.Entry<String, String[]> entry:parameterMap.entrySet()){
                    list.add(entry.getKey());
                    String[] values = entry.getValue();
                    if(!ArrayUtils.isEmpty(values)){
                        list.addAll(Arrays.asList(values));
                    }
                }
                sb.append(list.hashCode()).append(CoreConstants.DOT);
            }
        }
    }

}
