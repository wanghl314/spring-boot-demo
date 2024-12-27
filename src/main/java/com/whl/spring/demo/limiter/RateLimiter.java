package com.whl.spring.demo.limiter;

public interface RateLimiter {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    int getIntervalInMs();

    int getSampleCount();

    long getLimit();

    int getWindowLengthInMs();

    RateWindow<?> currentWindow(long currentTime);

    RateWindow<?> currentWindow(long currentTime, String key);

    boolean isLimit();

    boolean isLimit(String key);

    long passed();

    long passed(String key);
}
