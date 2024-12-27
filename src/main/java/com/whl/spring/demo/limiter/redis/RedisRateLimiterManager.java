package com.whl.spring.demo.limiter.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedisRateLimiterManager {
    private static final Map<String, List<RedisRateLimiter>> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    private static final List<RedisRateLimiter> RATE_LIMITER_LIST = new CopyOnWriteArrayList<>();

    private static final String RATE_LIMITER_PREFIX = "ratelimiter:";

    private final RedisTemplate<?, ?> redisTemplate;

    public RedisRateLimiterManager(RedisTemplate<?, ?> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<RedisRateLimiter> getRateLimiters(String category) {
        return RATE_LIMITER_MAP.get(category);
    }

    public List<RedisRateLimiter> getAllRateLimiters() {
        return new ArrayList<>(RATE_LIMITER_LIST);
    }

    public RedisRateLimiter addRateLimiter(String category, int intervalInMs, long limit) {
        return this.addRateLimiter(category, null, intervalInMs, limit);
    }

    public RedisRateLimiter addRateLimiter(String category, String name, int intervalInMs, long limit) {
        List<RedisRateLimiter> rateLimiters = RATE_LIMITER_MAP.computeIfAbsent(category, k -> new ArrayList<>());
        RedisRateLimiter current = null;

        if (!rateLimiters.isEmpty()) {
            current = rateLimiters.stream()
                    .filter(limiter -> limiter.getIntervalInMs() == intervalInMs && limiter.getLimit() == limit)
                    .findFirst()
                    .orElse(null);
        }

        if (current == null) {
            current = RATE_LIMITER_LIST.stream()
                    .filter(limiter -> limiter.getIntervalInMs() == intervalInMs && limiter.getLimit() == limit)
                    .findFirst()
                    .orElse(null);

            if (current == null) {
                current = new RedisRateLimiter(RATE_LIMITER_PREFIX + StringUtils.defaultIfBlank(name, UUID.randomUUID().toString().replace("-", "")),
                        intervalInMs, limit, this.redisTemplate);
                RATE_LIMITER_LIST.add(current);
                current.init();
            }
            rateLimiters.add(current);
        }
        return current;
    }

}
