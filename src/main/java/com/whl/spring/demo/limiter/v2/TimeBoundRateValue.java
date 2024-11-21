package com.whl.spring.demo.limiter.v2;

import java.time.Duration;

public interface TimeBoundRateValue extends RateValue {
    Duration DEFAULT_EXPIRE = Duration.ofMinutes(5);

    Duration getExpire();

    void setExpire(Duration expire);

    void expire();
}
