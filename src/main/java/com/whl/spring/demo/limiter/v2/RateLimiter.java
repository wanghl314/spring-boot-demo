package com.whl.spring.demo.limiter.v2;

public interface RateLimiter {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    int getIntervalInMs();

    int getSampleCount();

    long getLimit();

    int getWindowLengthInMs();

    boolean isLimit();

    boolean isLimit(String key);

    long passed();

    long passed(String key);
}
