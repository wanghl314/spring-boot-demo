package com.whl.spring.demo.limiter.v1;

public interface KeyBasedRateLimiter extends RateLimiter {
    boolean limit(String key);

    long passed(String key);

    void incr(String key);

    void reset(String key);
}
