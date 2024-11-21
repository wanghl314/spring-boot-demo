package com.whl.spring.demo.limiter.v2.redis;

import com.whl.spring.demo.limiter.v2.RateValue;
import com.whl.spring.demo.limiter.v2.TimeBoundRateValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class RedisRateValue implements TimeBoundRateValue {
    private final RedisTemplate<String, Number> redisTemplate;

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private Duration expire;

    public RedisRateValue(RedisTemplate<?, ?> redisTemplate) {
        this(redisTemplate, null, null);
    }

    public RedisRateValue(RedisTemplate<?, ?> redisTemplate, String key) {
        this(redisTemplate, key, null);
    }

    @SuppressWarnings("unchecked")
    public RedisRateValue(RedisTemplate<?, ?> redisTemplate, String key, Duration expire) {
        this.redisTemplate = (RedisTemplate<String, Number>) redisTemplate;
        this.key = key;
        this.expire = expire;
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
    public long incr() {
        Number value = this.redisTemplate.opsForValue().increment(this.key);

        if (value != null && value.longValue() == 1L) {
            this.expire();
        }
        if (value != null) {
            return value.longValue();
        }
        return 0L;
    }

    @Override
    public void expire() {
        this.redisTemplate.expire(this.key, (this.expire != null && this.expire != Duration.ZERO) ? this.expire : DEFAULT_EXPIRE);
    }

    @Override
    public void reset() {
        this.redisTemplate.opsForValue().set(this.key, 0);
        this.expire();
    }

    @Override
    public RateValue copy() {
        return new RedisRateValue(this.redisTemplate, this.key, this.expire);
    }

}
