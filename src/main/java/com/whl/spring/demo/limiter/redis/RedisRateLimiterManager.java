package com.whl.spring.demo.limiter.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedisRateLimiterManager {
    private static final Map<String, List<RedisRateLimiter>> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    private static final List<RedisRateLimiter> RATE_LIMITER_LIST = new CopyOnWriteArrayList<>();

    private static final String RATE_LIMITER_PREFIX = "ratelimiter:";

    private static boolean inited = false;

    private final RedisTemplate<?, ?> redisTemplate;

    public RedisRateLimiterManager(RedisTemplate<?, ?> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 构建内置限流器
     * @param name
     * @param intervalInMs
     * @param limit
     * @return
     */
    public RedisRateLimiter buildRateLimiter(String name, int intervalInMs, long limit) {
        RedisRateLimiter exists = RATE_LIMITER_LIST.stream()
                .filter(limiter -> StringUtils.equalsIgnoreCase(limiter.getName(), RATE_LIMITER_PREFIX + name))
                .findFirst()
                .orElse(null);

        if (exists != null) {
            throw new IllegalArgumentException(name + " already exists");
        }
        RedisRateLimiter limiter = new RedisRateLimiter(RATE_LIMITER_PREFIX + name, intervalInMs, limit, redisTemplate);
        RATE_LIMITER_LIST.add(limiter);
        return limiter;
    }

    /**
     * 初始化内置限流器
     */
    public void init() {
        if (!inited) {
            inited = true;
            RATE_LIMITER_LIST.forEach(RedisRateLimiter::init);
        }
    }

    public List<RedisRateLimiter> getAllRateLimiters() {
        return new ArrayList<>(RATE_LIMITER_LIST);
    }

    /**
     * 添加自定义限流器
     * @param category
     * @param intervalInMs
     * @param limit
     * @return
     */
    public RedisRateLimiter addRateLimiter(String category, int intervalInMs, long limit) {
        return this.addRateLimiter(category, null, intervalInMs, limit);
    }

    /**
     * 添加自定义限流器
     * @param category
     * @param name
     * @param intervalInMs
     * @param limit
     * @return
     */
    public synchronized RedisRateLimiter addRateLimiter(String category, String name, int intervalInMs, long limit) {
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
                if (StringUtils.isBlank(name)) {
                    name = category + "_" + intervalInMs + "_" + limit;
                }
                current = this.buildRateLimiter(name, intervalInMs, limit);
                current.init();
            }
            rateLimiters.add(current);
        }
        return current;
    }

}
