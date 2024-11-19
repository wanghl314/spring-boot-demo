package com.whl.spring.demo.limiter;

import java.util.List;

public interface TimeBasedRateLimiter extends RateLimiter {
    int getIntervalInMs();

    boolean limit();

    long passed();

    RateWindow<?> currentWindow(long currentTime);

    void sliding(long currentTime);

    List<RateWindow<?>> statistics();

    void init();

    void persist();
}
