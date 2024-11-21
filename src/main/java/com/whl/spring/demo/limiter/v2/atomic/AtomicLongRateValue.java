package com.whl.spring.demo.limiter.v2.atomic;

import com.whl.spring.demo.limiter.v2.RateValue;
import lombok.AllArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
public class AtomicLongRateValue implements RateValue {
    private AtomicLong value;

    public long get() {
        return this.value.get();
    }

    public long incr() {
        return this.value.incrementAndGet();
    }

    public void reset() {
        this.value.set(0);
    }

    @Override
    public RateValue copy() {
        return new AtomicLongRateValue(new AtomicLong(this.value.get()));
    }

}
