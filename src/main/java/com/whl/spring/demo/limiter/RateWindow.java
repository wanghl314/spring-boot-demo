package com.whl.spring.demo.limiter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RateWindow<T extends TimeBasedRateValue> {
    protected long time;

    protected T value;

    public long time() {
        return this.time;
    }

    public long get() {
        return this.value.get();
    }

    public void incr() {
        this.value.incr();
    }

    public void expire() {
        this.value.expire();
    }

    public void reset() {
        this.value.reset();
    }

}
