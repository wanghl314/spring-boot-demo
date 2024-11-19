package com.whl.spring.demo.limiter;

public interface TimeBasedRateValue extends RateValue {
    long get();

    void incr();

    void expire();

    void reset();
}
