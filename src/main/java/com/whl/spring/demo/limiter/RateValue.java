package com.whl.spring.demo.limiter;

import java.time.Duration;

public interface RateValue {
    Duration DEFAULT_EXPIRE = Duration.ofMinutes(1);

    Duration getExpire();
}
