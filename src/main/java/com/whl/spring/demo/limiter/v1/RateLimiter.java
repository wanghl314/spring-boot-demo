package com.whl.spring.demo.limiter.v1;

public interface RateLimiter {
    String getName();

    long getLimit();
}
