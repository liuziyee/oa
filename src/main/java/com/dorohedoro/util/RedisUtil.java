package com.dorohedoro.util;

import com.dorohedoro.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate redisTemplate;
    private final AppProperties appProperties;
    
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) {
        set(key, value, appProperties.getJwt().getCacheExpire(), TimeUnit.DAYS);
    }

    public void set(String key, Object value, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }
    
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
