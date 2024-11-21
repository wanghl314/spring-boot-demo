package com.whl.spring.demo.limiter.v2.atomic;

import com.whl.spring.demo.limiter.v2.AbstractRateLimiter;
import com.whl.spring.demo.limiter.v2.RateWindow;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongRateLimiter extends AbstractRateLimiter {

    public AtomicLongRateLimiter(String name, int intervalInMs, long limit) {
        super(name, intervalInMs, limit);
    }

    public AtomicLongRateLimiter(String name, int intervalInMs, int windowLengthInMs, long limit) {
        super(name, intervalInMs, windowLengthInMs, limit);
    }

    @Override
    protected RateWindow<?> newWindow(long time) {
        return new RateWindow<>(time, new AtomicLongRateValue(new AtomicLong()));
    }

    @Override
    protected void deprecated(RateWindow<?> window) {
        if (window != null) {
            window.expire();
        }
    }

}
