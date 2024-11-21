package com.whl.spring.demo.limiter.v1.atomic;

import com.whl.spring.demo.limiter.v1.AbstractTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.RateWindow;

public class AtomicLongRateLimiter extends AbstractTimeBasedRateLimiter {

    public AtomicLongRateLimiter(String name, int intervalInMs, long limit) {
        super(name, intervalInMs, limit);
    }

    @Override
    protected RateWindow<?> newWindow(long time) {
        return null;
    }

    @Override
    protected void deprecated(RateWindow<?> window) {

    }

}
