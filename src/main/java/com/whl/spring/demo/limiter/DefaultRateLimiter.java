package com.whl.spring.demo.limiter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

public class DefaultRateLimiter implements KeyBasedRateLimiter {
    @Getter
    protected String name;

    @Getter
    protected int intervalInMs;

    @Getter
    protected long limit;

    @Setter
    protected KeyBasedRateValue value;

    public DefaultRateLimiter(String name, int intervalInMs, long limit, KeyBasedRateValue value) {
        Assert.isTrue(intervalInMs > 0, "total time interval of the sliding window should be positive");
        Assert.isTrue(limit > 0, "limit should be positive");
        this.name = name;
        this.intervalInMs = intervalInMs;
        this.limit = limit;
        this.value = value;
    }

    @Override
    public boolean limit(String key) {
        return this.passed(key) >= this.limit;
    }

    @Override
    public long passed(String key) {
        return this.value.get(this.name + ":" + key + ":" + this.index());
    }

    @Override
    public void incr(String key) {
        this.value.incr(this.name + ":" + key + ":" + this.index());
    }

    @Override
    public void reset(String key) {
        this.value.reset(this.name + ":" + key + ":" + this.index());
    }

    @Override
    public String toString() {
        return "DefaultRateLimiter{" +
                "name='" + name + '\'' +
                ", intervalInMs=" + intervalInMs +
                ", limit=" + limit +
                ", value=" + value +
                '}';
    }

    private String index() {
        return String.valueOf(System.currentTimeMillis() / this.getIntervalInMs());
    }

}
