package com.colorful.spring.boot.ratelimit.service.impl;

import com.colorful.spring.boot.ratelimit.service.RedisService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hby
 * 2020/7/24 - 16:26.
 **/
public class RedisServiceImpl<T> implements RedisService<T> {

    private RedisTemplate<String, T> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, T> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, T value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public T get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public List<T> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.expire(key, time, timeUnit);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public T hGet(String key, String hashKey) {
        HashOperations<String, String, T> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, hashKey);
    }

    @Override
    public Boolean hSet(String key, String hashKey, T value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time, timeUnit);
    }

    @Override
    public void hSet(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> map, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time, timeUnit);
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    @Override
    public Set<T> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    @SafeVarargs
    public final Long sAdd(String key, T... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @SafeVarargs
    @Override
    public final Long sAddWithExpire(String key, long time, TimeUnit timeUnit, T... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time, timeUnit);
        return count;
    }

    @Override
    public Boolean sIsMember(String key, T value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<T> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public T lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lPush(String key, T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long lPushWithExpire(String key, T value, long time, TimeUnit timeUnit) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time, timeUnit);
        return index;
    }

    @SafeVarargs
    @Override
    public final Long lPushAll(String key, T... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @SafeVarargs
    @Override
    public final Long lPushAllWithExpire(String key, Long time, TimeUnit timeUnit, T... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time, timeUnit);
        return count;
    }

    @Override
    public Long lRemove(String key, long count, T value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> keyScan(String prefix, int scanSize) throws IOException {
        ArrayList<String> keys = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match(prefix).count(scanSize).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        try(Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(new RedisCallback() {
            @Override
            public Object doInRedis(@NonNull RedisConnection redisConnection) throws DataAccessException {
                return new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize);
            }
        })){
            if(null != cursor){
                while (cursor.hasNext()){
                    keys.add(cursor.next());
                }
            }
        }
        return keys;
    }


//    public static void main(String[] args) {
//        RedisServiceImpl<String> longRedisService = new RedisServiceImpl<>(new RedisTemplate<>());
//        longRedisService.lPushAllWithExpire("1", 1L, TimeUnit.SECONDS, "1");
//    }
}
