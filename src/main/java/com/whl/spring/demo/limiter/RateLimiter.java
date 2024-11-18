package com.whl.spring.demo.limiter;

public interface RateLimiter {
    String getName();

    int getIntervalInMs();

    long getLimit();
}
