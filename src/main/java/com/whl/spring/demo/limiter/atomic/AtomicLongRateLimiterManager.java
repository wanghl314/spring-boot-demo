package com.whl.spring.demo.limiter.atomic;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AtomicLongRateLimiterManager {
    private static final Map<String, List<AtomicLongRateLimiter>> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    private static final List<AtomicLongRateLimiter> RATE_LIMITER_LIST = new CopyOnWriteArrayList<>();

    public List<AtomicLongRateLimiter> getRateLimiters(String category) {
        return RATE_LIMITER_MAP.get(category);
    }

    public List<AtomicLongRateLimiter> getAllRateLimiters() {
        return new ArrayList<>(RATE_LIMITER_LIST);
    }

    public AtomicLongRateLimiter addRateLimiter(String category, int intervalInMs, long limit) {
        return this.addRateLimiter(category, null, intervalInMs, limit);
    }

    public synchronized AtomicLongRateLimiter addRateLimiter(String category, String name, int intervalInMs, long limit) {
        List<AtomicLongRateLimiter> rateLimiters = RATE_LIMITER_MAP.computeIfAbsent(category, k -> new ArrayList<>());
        AtomicLongRateLimiter current = null;

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
                current = new AtomicLongRateLimiter(StringUtils.defaultIfBlank(name, UUID.randomUUID().toString().replace("-", "")),
                        intervalInMs, limit);
                RATE_LIMITER_LIST.add(current);
                current.init();
            }
            rateLimiters.add(current);
        }
        return current;
    }

}
