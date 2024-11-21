package com.whl.spring.demo.limiter.v2;

public interface RateValue {
    void setKey(String key);

    long get();

    long incr();

    void reset();

    RateValue copy();
}
