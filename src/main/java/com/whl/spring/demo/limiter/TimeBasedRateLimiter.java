package com.whl.spring.demo.limiter;

import java.util.List;

public interface TimeBasedRateLimiter extends RateLimiter {
    boolean limit();

    long passed();

    RateWindow<?> currentWindow(long currentTime);

    void sliding(long currentTime);

    List<RateWindow<?>> statistics();

    default void init() {
    }

    default void persist() {
    }
}
