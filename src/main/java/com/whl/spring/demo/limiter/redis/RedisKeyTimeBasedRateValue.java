package com.whl.spring.demo.limiter.redis;

import com.whl.spring.demo.limiter.KeyBasedRateValue;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class RedisKeyTimeBasedRateValue implements KeyBasedRateValue {
    private final RedisTemplate<String, Number> redisTemplate;

    private Duration expire;

    @SuppressWarnings("unchecked")
    public RedisKeyTimeBasedRateValue(RedisTemplate<?, ?> redisTemplate) {
        this.redisTemplate = (RedisTemplate<String, Number>) redisTemplate;
    }

    @SuppressWarnings("unchecked")
    public RedisKeyTimeBasedRateValue(RedisTemplate<?, ?> redisTemplate, Duration expire) {
        this.redisTemplate = (RedisTemplate<String, Number>) redisTemplate;
        this.expire = expire;
    }

    @Override
    public Duration getExpire() {
        return this.expire;
    }

    @Override
    public long get(String key) {
        Number number = this.redisTemplate.opsForValue().get(key);

        if (number != null) {
            return number.longValue();
        }
        return 0L;
    }

    @Override
    public void incr(String key) {
        this.redisTemplate.opsForValue().increment(key);
        this.redisTemplate.expire(key, (this.expire != null && this.expire != Duration.ZERO) ? this.expire : DEFAULT_EXPIRE);
    }

    @Override
    public void reset(String key) {
        this.redisTemplate.opsForValue().set(key, 0);
    }

}
