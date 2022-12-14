package com.dorohedoro.util;

import com.dorohedoro.config.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate redisTemplate;
    private final Properties properties;
    
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) {
        set(key, value, properties.getJwt().getCacheExpire(), TimeUnit.DAYS);
    }
    
    public void set(String key, Object value, Date expire) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expireAt(key, expire);
    }
    

    public void set(String key, Object value, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }
    
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
