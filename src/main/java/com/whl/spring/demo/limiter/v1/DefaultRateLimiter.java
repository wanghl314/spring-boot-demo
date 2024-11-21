package com.whl.spring.demo.limiter.v1;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

public class DefaultRateLimiter implements KeyBasedRateLimiter {
    @Getter
    protected String name;

    @Getter
    protected long limit;

    @Setter
    protected KeyBasedRateValue value;

    public DefaultRateLimiter(String name, long limit, KeyBasedRateValue value) {
        Assert.isTrue(limit > 0, "limit should be positive");
        Assert.notNull(value, "value should be not null");
        this.name = name;
        this.limit = limit;
        this.value = value;
    }

    @Override
    public boolean limit(String key) {
        return this.passed(key) >= this.limit;
    }

    @Override
    public long passed(String key) {
        return this.value.get(this.name + ":" + key);
    }

    @Override
    public void incr(String key) {
        this.value.incr(this.name + ":" + key);
    }

    @Override
    public void reset(String key) {
        this.value.reset(this.name + ":" + key);
    }

    @Override
    public String toString() {
        return "DefaultRateLimiter{" +
                "name='" + name + '\'' +
                ", limit=" + limit +
                '}';
    }

}
