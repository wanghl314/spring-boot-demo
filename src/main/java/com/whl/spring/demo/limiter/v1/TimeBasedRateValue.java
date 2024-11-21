package com.whl.spring.demo.limiter.v1;

public interface TimeBasedRateValue extends RateValue {
    long get();

    void incr();

    void expire();

    void reset();
}
