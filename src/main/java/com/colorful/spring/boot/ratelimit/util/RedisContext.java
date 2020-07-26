package com.colorful.spring.boot.ratelimit.util;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hby
 * 2020/7/24  - 17:11.
 **/
public class RedisContext{

    private static final Logger logger = LoggerFactory.getLogger(RedisContext.class);

    private RedisTemplate<String, String> redisTemplate;

    public RedisContext(RedisTemplate<String, String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    public List<String> find(String patternKey) throws IOException {

        Assert.notNull(patternKey, "patternKey cannot be null");

        long start = System.currentTimeMillis();
        //需要匹配的key
        ScanOptions options = ScanOptions.scanOptions()
                //这里指定每次扫描key的数量(不要指定Integer.MAX_VALUE，这样的话跟 keys有什么区别？)
                .count(10000)
                .match(patternKey).build();
        @SuppressWarnings("unchecked")
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        List<String> result = new ArrayList<>();

        try(Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(
                        redisConnection.scan(options), redisSerializer::deserialize))){
            if(null != cursor){
                while(cursor.hasNext()){
                    result.add(cursor.next());
                }
            }
        //cursor.close(); //try with resource
        logger.info("scan patternKey:{}, use time:{}", patternKey, System.currentTimeMillis()-start,result.size());
        }
        return result;
    }

    /**
     * 加锁
     * @param key 锁key
     * @param value 锁value
     * @param timeout 锁持续时间
     * @param timeUnit 时间单位
     * @return 加锁成功返回true
     */
    public boolean lock(String key, String  value, long timeout, TimeUnit timeUnit) {
        // 可以设置返回true
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
        boolean isLocked = BooleanUtils.isTrue(isLock);
        if(isLocked){
            logger.info("set lock to redis, key:{}, value:{}, timeout:{}", key, value, timeout);
        }
        return isLocked;
    }

    /**
     * 解锁
     * 解锁时，key 和 value 值必须与加锁时候的值一致，否则解锁会失败
     * @param key 锁key
     * @param value 锁value
     * @return boolean true则说明锁存在并且解锁成功(锁不存在也返回true)， false说明操作锁异常
     */
    public boolean unlock(String key, String value) {

        try {
            Assert.notNull(key, "lock key can not be null");
            if(!BooleanUtils.isTrue(redisTemplate.hasKey(key))){
                //不存在这个键 即锁不存在 返回true
                return true;
            }
            String currentValue = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue)
                    && currentValue.equals(value)) {
                //只有锁存在并且 key、value 值一致，才能解锁
                redisTemplate.opsForValue().getOperations().delete(key);
                logger.info("remove lock from redis, key:{}", key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 加锁，业务处理完后立即释放锁
     * @param runnable 业务处理方法
     * @param lockKey 锁标识
     * @param time 加锁时长
     * @param timeUnit 单位
     */
    public void doInLock(Runnable runnable, String lockKey, long time, TimeUnit timeUnit) {
        if(lock(lockKey, lockKey, time, timeUnit)){
            try{
                runnable.run();
            }finally {
                unlock(lockKey, lockKey);
            }
        }
    }

    /**
     *  加锁并处理业务，业务处理完后不释放锁让其超时释放
     * @param runnable 业务处理方法
     * @param lockKey 锁标识
     * @param time 加锁时长
     * @param timeUnit 单位
     */
    public void doAndLock(Runnable runnable, String lockKey, long time, TimeUnit timeUnit) {
        if(lock(lockKey, lockKey, time, timeUnit)){
            runnable.run();
        }
    }

    /**
     *  加锁并处理业务，业务处理完后不释放锁让其超时释放
     * @param runnable 业务处理方法
     * @param lockKey 锁标识
     * @param time 加锁时长
     * @param timeUnit 单位
     */
    public void doAndLock(Runnable runnable, String lockKey, long time, TimeUnit timeUnit, String cacheKey) {
        if(lock(lockKey, lockKey, time, timeUnit)){
            runnable.run();
        }
    }

    public boolean hasKey(String cacheKey){
        return BooleanUtils.isTrue(redisTemplate.hasKey(cacheKey));
    }
}
