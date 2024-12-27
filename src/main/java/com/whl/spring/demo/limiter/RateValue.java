package com.whl.spring.demo.limiter;

public interface RateValue {
    default String getKey() {
        return null;
    }

    default void setKey(String key) {
    }

    long get();

    long incr();

    void reset();

    RateValue copy();
}
