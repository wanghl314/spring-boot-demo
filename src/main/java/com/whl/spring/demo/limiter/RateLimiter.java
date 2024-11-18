package com.whl.spring.demo.limiter;

public interface RateLimiter {
    String getName();

    long getLimit();
}
