package com.whl.spring.demo.limiter.v2;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Getter
public abstract class AbstractRateLimiter implements RateLimiter {
    protected static final int DEFAULT_WINDOW_LENGTH_IN_MS = 1000;

    private static final int MINIMUM_WINDOW_LENGTH_IN_MS = 500;

    protected String name;

    @Setter
    protected boolean enabled = true;

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

    public synchronized void change(int intervalInMs, int windowLengthInMs, long limit) {
        Assert.isTrue(intervalInMs > 0, "intervalInMs should be positive");
        Assert.isTrue(windowLengthInMs >= MINIMUM_WINDOW_LENGTH_IN_MS, "the minimum value of windowLengthInMs is " + MINIMUM_WINDOW_LENGTH_IN_MS);
        Assert.isTrue(intervalInMs >= windowLengthInMs, "intervalInMs should be no less then windowLengthInMs");
        Assert.isTrue(intervalInMs % windowLengthInMs == 0, "intervalInMs must be divided by windowLengthInMs");
        Assert.isTrue(limit > 0, "limit should be positive");
        this.intervalInMs = intervalInMs;
        this.windowLengthInMs = windowLengthInMs;
        this.limit = limit;
        this.sampleCount = this.intervalInMs / this.windowLengthInMs;

        if (this.sampleCount > 1) {
            if (this.array == null) {
                this.array = new AtomicReferenceArray<>(this.sampleCount);
            } else {
                long current = System.currentTimeMillis();
                AtomicReferenceArray<RateWindow<?>> temp = new AtomicReferenceArray<>(this.sampleCount);

                for (int i = 0; i < this.array.length(); i++) {
                    RateWindow<?> window = this.array.get(i);
                    int newIdx = this.calculateTimeIdx(window.getTime());
                    long newTime = this.calculateTime(window.getTime());
                    RateWindow<?> exists = temp.get(newIdx);

                    if (exists == null || exists.getTime() < newTime) {
                        temp.compareAndSet(newIdx, exists, this.newWindow(newTime));
                    }
                }
                this.array = temp;
                this.sliding(current);
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

    public synchronized void sliding(long currentTime) {
        if (this.array != null) {
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

//    public static void main(String[] args) throws InterruptedException {
//        Thread[] threads = new Thread[20];
//
//        for (int i = 0; i < threads.length; i++) {
//            if (i % 2 == 0) {
//                threads[i] = new Thread(() -> {
//                    AbstractRateLimiter limiter = new AbstractRateLimiter("", 10000, 1000);
//                    for (int j = 0; j < limiter.array.length(); j++) {
//                        long start = System.currentTimeMillis();
//                        int idx = limiter.calculateTimeIdx(start);
//                        long time = limiter.calculateTime(start);
//                        Long old = limiter.array.get(idx);
//
//                        if (old == null) {
//                            limiter.array.compareAndSet(idx, null, time);
//                        } else if (old != time) {
//                            limiter.array.compareAndSet(idx, old, time);
//                        }
//
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                    System.out.println("扩容" + Thread.currentThread().threadId() + ":" + limiter.array);
//                    limiter.change(15000, 500, 1000);
//                    System.out.println("扩容" + Thread.currentThread().threadId() + ":" + limiter.array);
//                });
//            } else {
//                threads[i] = new Thread(() -> {
//                    AbstractRateLimiter limiter = new AbstractRateLimiter("", 10000, 1000);
//                    for (int j = 0; j < limiter.array.length(); j++) {
//                        long start = System.currentTimeMillis();
//                        int idx = limiter.calculateTimeIdx(start);
//                        long time = limiter.calculateTime(start);
//                        Long old = limiter.array.get(idx);
//
//                        if (old == null) {
//                            limiter.array.compareAndSet(idx, null, time);
//                        } else if (old != time) {
//                            limiter.array.compareAndSet(idx, old, time);
//                        }
//
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                    System.out.println("缩容" + Thread.currentThread().threadId() + ":" + limiter.array);
//                    limiter.change(1500, 500, 1000);
//                    System.out.println("缩容" + Thread.currentThread().threadId() + ":" + limiter.array);
//                });
//            }
//        }
//        Random random = new Random();
//
//        for (Thread thread : threads) {
//            thread.start();
//            Thread.sleep((random.nextLong(10000) + 1));
//        }
//    }

}
