package com.whl.spring.demo.limiter.v1;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class AbstractTimeBasedRateLimiter implements TimeBasedRateLimiter {
    private static final int WINDOW_LENGTH_IN_MS = 1000;

    protected AtomicReferenceArray<RateWindow<?>> array;

    @Getter
    protected String name;

    @Getter
    protected int intervalInMs;

    @Getter
    protected long limit;

    protected int sampleCount;

    public AbstractTimeBasedRateLimiter(String name, int intervalInMs, long limit) {
        Assert.isTrue(intervalInMs >= WINDOW_LENGTH_IN_MS, "intervalInMs should be no less then " + WINDOW_LENGTH_IN_MS);
        Assert.isTrue(intervalInMs % WINDOW_LENGTH_IN_MS == 0, "intervalInMs must be divided by " + WINDOW_LENGTH_IN_MS);
        Assert.isTrue(limit > 0, "limit should be positive");
        this.name = name;
        this.intervalInMs = intervalInMs;
        this.limit = limit;
        this.sampleCount = intervalInMs / WINDOW_LENGTH_IN_MS;
        this.array = new AtomicReferenceArray<>(this.sampleCount);
    }

    @Override
    public boolean limit() {
        return this.passed() >= this.limit;
    }

    @Override
    public long passed() {
        long passed = 0;

        for (int i = 0; i < this.array.length(); i++) {
            RateWindow<?> window = this.array.get(i);

            if (window != null) {
                passed += window.get();
            }
        }
        return passed;
    }

    @Override
    public RateWindow<?> currentWindow(long currentTime) {
        int idx = this.calculateTimeIdx(currentTime);
        long time = this.calculateTime(currentTime);
        RateWindow<?> window = this.array.get(idx);

        if (window == null) {
            window = this.newWindow(time);
            this.array.compareAndSet(idx, null, window);
        }
        return window;
    }

    @Override
    public void sliding(long currentTime) {
        int idx = this.calculateTimeIdx(currentTime);
        long time = this.calculateTime(currentTime);
        RateWindow<?> old = this.array.get(idx);

        if (old == null) {
            RateWindow<?> window = this.newWindow(time);
            this.array.compareAndSet(idx, null, window);
        } else if (old.time() != time) {
            RateWindow<?> window = this.newWindow(time);
            this.array.compareAndSet(idx, old, window);
            this.deprecated(old);
        }
        this.persist();
    }

    @Override
    public List<RateWindow<?>> statistics() {
        List<RateWindow<?>> windows = new ArrayList<>(this.sampleCount);

        for (int i = 0; i < this.array.length(); i++) {
            windows.add(this.array.get(i));
        }
        return windows;
    }

    @Override
    public void init() {
    }

    @Override
    public void persist() {
    }

    @Override
    public String toString() {
        return "AbstractTimeBasedRateLimiter{" +
                "name='" + name + '\'' +
                ", intervalInMs=" + intervalInMs +
                ", limit=" + limit +
                ", sampleCount=" + sampleCount +
                '}';
    }

    protected abstract RateWindow<?> newWindow(long time);

    protected abstract void deprecated(RateWindow<?> window);

    private int calculateTimeIdx(long currentTime) {
        if (this.sampleCount == 1) {
            return 0;
        }
        long timeId = currentTime / WINDOW_LENGTH_IN_MS;
        return (int) timeId % this.sampleCount;
    }

    private long calculateTime(long currentTime) {
        return currentTime - currentTime % WINDOW_LENGTH_IN_MS;
    }

}
