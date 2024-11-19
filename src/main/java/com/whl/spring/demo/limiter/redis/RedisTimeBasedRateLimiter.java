package com.whl.spring.demo.limiter.redis;

import com.whl.spring.demo.limiter.AbstractTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.RateWindow;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RedisTimeBasedRateLimiter extends AbstractTimeBasedRateLimiter {
    private final RedisTemplate<String, List<Long>> redisTemplate;

    @SuppressWarnings("unchecked")
    public RedisTimeBasedRateLimiter(String name, int intervalInMs, long limit, RedisTemplate<?, ?> redisTemplate) {
        super(name, intervalInMs, limit);
        this.redisTemplate = (RedisTemplate<String, List<Long>>) redisTemplate;
    }

    @Override
    public void init() {
        List<Long> data = this.redisTemplate.opsForValue().get(this.name + ":persist");

        if (CollectionUtils.isNotEmpty(data) && data.size() == this.sampleCount) {
            for (int i = 0; i < this.sampleCount; i++) {
                Long time = data.get(i);

                if (time != null) {
                    RateWindow<RedisTimeBasedRateValue> window = new RateWindow<RedisTimeBasedRateValue>(time, new RedisTimeBasedRateValue(redisTemplate, this.name + ":" + time));
                    this.array.compareAndSet(i, null, window);
                }
            }
        }
    }

    @Override
    public void persist() {
        List<Long> data = new ArrayList<>();

        for (int i = 0; i < this.array.length(); i++) {
            RateWindow<?> window = this.array.get(i);

            if (window != null) {
                data.add(window.time());
            } else {
                data.add(null);
            }
        }
        this.redisTemplate.opsForValue().set(this.name + ":persist", data);
    }

    @Override
    protected RateWindow<RedisTimeBasedRateValue> newWindow(long time) {
        return new RateWindow<>(time, new RedisTimeBasedRateValue(this.redisTemplate, this.name + ":" + time));
    }

    @Override
    protected void deprecated(RateWindow<?> window) {
        if (window != null) {
            window.expire();
        }
    }

    @Override
    public String toString() {
        return name +
                "{" +
                "intervalInMs=" + intervalInMs +
                ", limit=" + limit +
                ", passed=" + this.passed() +
                '}';
    }

    public String htmlStat() {
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"1\" style=\"border-collapse:collapse;\">");
        builder.append("  <thead>");
        builder.append("    <tr>");
        builder.append("      <th colspan=\"2\" style=\"padding:5px;\">");
        builder.append(this.getName());
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("  </thead>");
        builder.append("  <tbody>");
        List<RateWindow<?>> statistics = this.statistics();
        long totalPassed = 0;

        for (RateWindow<?> window : statistics) {
            if (window != null) {
                long time = window.time();
                long passed = window.get();
                totalPassed += passed;
                builder.append("    <tr>");
                builder.append("      <td style=\"padding:5px;\">");

                if (time % 1000 == 0) {
                    builder.append(window.time() / 1000);
                } else {
                    builder.append(window.time());
                }
                builder.append("</td>");
                builder.append("      <td style=\"padding:5px;\">");
                builder.append(passed);
                builder.append("</td>");
                builder.append("    </tr>");
            }
        }
        builder.append("  </tbody>");
        builder.append("  <tfoot>");
        builder.append("    <tr>");
        builder.append("      <th style=\"padding:5px;\">totalPassed</th>");
        builder.append("      <th style=\"padding:5px;\">");
        builder.append(totalPassed);
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("  </tfoot>");
        builder.append("</table>");
        return builder.toString();
    }

}
