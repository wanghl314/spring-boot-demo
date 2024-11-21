package com.whl.spring.demo.limiter.v1;

import java.time.Duration;

public interface RateValue {
    Duration DEFAULT_EXPIRE = Duration.ofMinutes(5);

    Duration getExpire();
}
