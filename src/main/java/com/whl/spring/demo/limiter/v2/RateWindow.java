package com.whl.spring.demo.limiter.v2;

import lombok.Getter;

@Getter
public class RateWindow<T extends RateValue> {
    protected long time;

    protected T value;

    public RateWindow(long time, T value) {
        this.time = time;
        this.value = value;
    }

    public RateWindow(long time, T value, String key) {
        this.time = time;
        this.value = value;

        if (value != null) {
            this.value.setKey(key + ":" + time);
        }
    }

    public long get() {
        return this.value.get();
    }

    public long incr() {
        return this.value.incr();
    }

    public void expire() {
        if (this.value instanceof TimeBoundRateValue timeBoundRateValue) {
            timeBoundRateValue.expire();
        }
    }

    public void reset() {
        this.value.reset();
    }

    public RateWindow<?> copy() {
        return this.copy(null);
    }

    public RateWindow<?> copy(String key) {
        return new RateWindow<>(this.time, this.value.copy(), key);
    }

}
