package com.whl.spring.demo.limiter;

public interface KeyBasedRateValue extends RateValue {
    long get(String key);

    void incr(String key);

    void reset(String key);
}
