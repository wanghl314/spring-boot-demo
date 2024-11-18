package com.whl.spring.demo.limiter.redis;

import com.whl.spring.demo.limiter.TimeBasedRateValue;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class RedisTimeBasedRateValue implements TimeBasedRateValue {
    private final RedisTemplate<String, Number> redisTemplate;

    private final String key;

    private Duration expire;

    @SuppressWarnings("unchecked")
    public RedisTimeBasedRateValue(RedisTemplate<?, ?> redisTemplate, String key) {
        this.redisTemplate = (RedisTemplate<String, Number>) redisTemplate;
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    public RedisTimeBasedRateValue(RedisTemplate<?, ?> redisTemplate, String key, Duration expire) {
        this.redisTemplate = (RedisTemplate<String, Number>) redisTemplate;
        this.key = key;
        this.expire = expire;
    }

    @Override
    public Duration getExpire() {
        return this.expire;
    }

    @Override
    public long get() {
        Number number = this.redisTemplate.opsForValue().get(this.key);

        if (number != null) {
            return number.longValue();
        }
        return 0L;
    }

    @Override
    public void incr() {
        this.redisTemplate.opsForValue().increment(this.key);
        this.redisTemplate.expire(this.key, (this.expire != null && this.expire != Duration.ZERO) ? this.expire : DEFAULT_EXPIRE);
    }

    @Override
    public void reset() {
        this.redisTemplate.opsForValue().set(key, 0);
    }

    @Override
    public String toString() {
        return "RedisTimeBasedRateValue{" +
                "key='" + key + '\'' +
                ", value='" + this.get() + '\'' +
                '}';
    }
}
