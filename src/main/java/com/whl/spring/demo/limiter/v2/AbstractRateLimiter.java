package com.whl.spring.demo.limiter.v2;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public abstract class AbstractRateLimiter implements RateLimiter {
    protected static final int DEFAULT_WINDOW_LENGTH_IN_MS = 1000;

    private static final int MINIMUM_WINDOW_LENGTH_IN_MS = 500;

    private final ReentrantLock changeLock = new ReentrantLock();

    protected String name;

    @Setter
    protected boolean enabled = true;

    protected volatile boolean inited = false;

    protected int intervalInMs;

    protected int sampleCount;

    protected long limit;

    protected int windowLengthInMs;

    protected AtomicReferenceArray<RateWindow<?>> array;

    public AbstractRateLimiter(String name, int intervalInMs, long limit) {
        this(name, intervalInMs, DEFAULT_WINDOW_LENGTH_IN_MS, limit);
    }

    public AbstractRateLimiter(String name, int intervalInMs, int windowLengthInMs, long limit) {
        Assert.isTrue(intervalInMs > 0, "intervalInMs should be positive");
        Assert.isTrue(windowLengthInMs > 0, "windowLengthInMs should be positive");
        Assert.isTrue(limit > 0, "limit should be positive");
        Assert.isTrue(intervalInMs >= windowLengthInMs, "intervalInMs should be no less then windowLengthInMs");
        Assert.isTrue(windowLengthInMs >= MINIMUM_WINDOW_LENGTH_IN_MS, "the minimum value of windowLengthInMs is " + MINIMUM_WINDOW_LENGTH_IN_MS);
        Assert.isTrue(intervalInMs % windowLengthInMs == 0, "intervalInMs must be divided by windowLengthInMs");
        this.name = name;
        this.intervalInMs = intervalInMs;
        this.windowLengthInMs = windowLengthInMs;
        this.limit = limit;
        this.sampleCount = this.intervalInMs / this.windowLengthInMs;

        if (this.sampleCount > 1) {
            this.array = new AtomicReferenceArray<>(this.sampleCount);
        }
    }

    public void change(int intervalInMs, long limit) {
        this.change(intervalInMs, this.windowLengthInMs, limit);
    }

    public void change(int intervalInMs, int windowLengthInMs, long limit) {
        Assert.isTrue(intervalInMs > 0, "intervalInMs should be positive");
        Assert.isTrue(windowLengthInMs >= MINIMUM_WINDOW_LENGTH_IN_MS, "the minimum value of windowLengthInMs is " + MINIMUM_WINDOW_LENGTH_IN_MS);
        Assert.isTrue(intervalInMs >= windowLengthInMs, "intervalInMs should be no less then windowLengthInMs");
        Assert.isTrue(intervalInMs % windowLengthInMs == 0, "intervalInMs must be divided by windowLengthInMs");
        Assert.isTrue(limit > 0, "limit should be positive");

        if (this.changeLock.tryLock()) {
            try {
                this.intervalInMs = intervalInMs;
                this.windowLengthInMs = windowLengthInMs;
                this.limit = limit;
                this.sampleCount = this.intervalInMs / this.windowLengthInMs;

                if (this.sampleCount == 1) {
                    this.array = null;
                } else {
                    if (this.array == null) {
                        this.array = new AtomicReferenceArray<>(this.sampleCount);
                    } else {
                        AtomicReferenceArray<RateWindow<?>> temp = new AtomicReferenceArray<>(this.sampleCount);

                        for (int i = 0; i < this.array.length(); i++) {
                            RateWindow<?> window = this.array.get(i);

                            if (window != null) {
                                int newIdx = this.calculateTimeIdx(window.getTime());
                                long newTime = this.calculateTime(window.getTime());
                                RateWindow<?> exists = temp.get(newIdx);

                                if (exists == null || exists.getTime() < newTime) {
                                    temp.compareAndSet(newIdx, exists, this.newWindow(newTime));
                                }
                            }
                        }
                        this.array = temp;
                    }
                    this.sliding(System.currentTimeMillis());
                }
            } finally {
                this.changeLock.unlock();
            }
        }
    }

    public boolean isLimit() {
        return this.isLimit(null);
    }

    public boolean isLimit(String key) {
        return this.passed(key) >= this.limit;
    }

    public long passed() {
        return this.passed(null);
    }

    public long passed(String key) {
        long passed = 0;

        if (this.array == null) {
            RateWindow<?> window = this.currentWindow(System.currentTimeMillis(), key);
            passed += window.get();
        } else {
            for (int i = 0; i < this.array.length(); i++) {
                RateWindow<?> window = this.array.get(i);

                if (window != null) {
                    RateWindow<?> copy = window.copy(this.key(key));
                    passed += copy.get();
                }
            }
        }
        return passed;
    }

    public RateWindow<?> currentWindow(long currentTime) {
        return this.currentWindow(currentTime, null);
    }

    public RateWindow<?> currentWindow(long currentTime, String key) {
        long time = this.calculateTime(currentTime);
        RateWindow<?> window;

        if (this.array == null) {
            window = this.newWindow(time);
        } else {
            int idx = this.calculateTimeIdx(currentTime);
            window = this.array.get(idx);

            if (window == null) {
                window = this.newWindow(time);
                this.array.compareAndSet(idx, null, window);
            }
        }
        return window.copy(this.key(key));
    }

    public void sliding(long currentTime) {
        if (this.array != null) {
            if (this.changeLock.tryLock()) {
                try {
                    int idx = this.calculateTimeIdx(currentTime);
                    long time = this.calculateTime(currentTime);
                    RateWindow<?> old = this.array.get(idx);

                    if (old == null || old.getTime() != time) {
                        RateWindow<?> window = this.newWindow(time);
                        this.array.compareAndSet(idx, old, window);

                        if (old != null) {
                            this.deprecated(old);
                        }
                    }
                    this.persist();
                } finally {
                    this.changeLock.unlock();
                }
            }
        }
    }

    public List<RateWindow<?>> statistics() {
        return this.statistics(null);
    }

    public List<RateWindow<?>> statistics(String key) {
        List<RateWindow<?>> windows = new ArrayList<>();

        if (this.array == null) {
            RateWindow<?> window = this.currentWindow(System.currentTimeMillis(), key);
            windows.add(window);
        } else {
            for (int i = 0; i < this.array.length(); i++) {
                RateWindow<?> window = this.array.get(i);

                if (window != null) {
                    windows.add(window.copy(this.key(key)));
                }
            }
        }
        return windows;
    }

    public void init() {
        this.inited = true;
    }

    public void persist() {
    }

    public String toString() {
        return "AbstractRateLimiter{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", intervalInMs=" + intervalInMs +
                ", sampleCount=" + sampleCount +
                ", limit=" + limit +
                ", windowLengthInMs=" + windowLengthInMs +
                '}';
    }

    protected abstract RateWindow<?> newWindow(long time);

    protected abstract void deprecated(RateWindow<?> window);

    private int calculateTimeIdx(long currentTime) {
        if (this.sampleCount == 1) {
            return 0;
        }
        long timeId = currentTime / this.windowLengthInMs;
        return (int) (timeId % this.sampleCount);
    }

    private long calculateTime(long currentTime) {
        return currentTime - currentTime % this.windowLengthInMs;
    }

    private String key(String key) {
        if (StringUtils.isBlank(key)) {
            return this.name;
        }
        return this.name + ":" + key;
    }

}
