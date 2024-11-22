package com.whl.spring.demo.limiter.v2.redis;

import com.whl.spring.demo.limiter.v2.AbstractRateLimiter;
import com.whl.spring.demo.limiter.v2.RateWindow;
import com.whl.spring.demo.limiter.v2.TimeBoundRateValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RedisRateLimiter extends AbstractRateLimiter {
    private static final String KEY_SUFFIX = ":persist";

    private final RedisTemplate<String, Persist> redisTemplate;

    @Getter
    @Setter
    private Duration expire;

    public RedisRateLimiter(String name, int intervalInMs, long limit, RedisTemplate<?, ?> redisTemplate) {
        this(name, intervalInMs, DEFAULT_WINDOW_LENGTH_IN_MS, limit, redisTemplate);
    }

    @SuppressWarnings("unchecked")
    public RedisRateLimiter(String name, int intervalInMs, int windowLengthInMs, long limit, RedisTemplate<?, ?> redisTemplate) {
        super(name, intervalInMs, windowLengthInMs, limit);
        this.redisTemplate = (RedisTemplate<String, Persist>) redisTemplate;
    }

    @Override
    public void init() {
        if (!this.inited) {
            Persist persist = this.redisTemplate.opsForValue().get(this.name + KEY_SUFFIX);

            if (persist != null) {
                Long expire = persist.getExpire();
                Integer intervalInMs = persist.getIntervalInMs();
                Long limit = persist.getLimit();
                List<Long> times = persist.getTimes();

                if (expire != null && expire > 0L) {
                    this.expire = Duration.ofSeconds(expire);
                }
                if (intervalInMs != null && intervalInMs > 0 && limit != null && limit > 0L &&
                        (intervalInMs != this.intervalInMs || limit != this.limit)) {
                    this.change(intervalInMs, limit);
                }
                if (CollectionUtils.isNotEmpty(times)) {
                    times.forEach(time -> {
                        if (time != null) {
                            this.sliding(time);
                        }
                    });
                }
            }
            this.inited = true;
        }
    }

    @Override
    public void persist() {
        String key = this.name + ":persist";
        List<Long> times = new ArrayList<>();

        for (int i = 0; i < this.array.length(); i++) {
            RateWindow<?> window = this.array.get(i);

            if (window != null) {
                times.add(window.getTime());
            } else {
                times.add(null);
            }
        }
        Persist persist = new Persist(this.expire != null ? this.expire.getSeconds() : null, this.intervalInMs, this.limit, times);
        this.redisTemplate.opsForValue().set(key, persist);
        this.redisTemplate.expire(key, this.getExpireTime());
    }

    @Override
    protected RateWindow<?> newWindow(long time) {
        RedisRateValue value = new RedisRateValue(this.redisTemplate);
        value.setExpire(this.expire);
        return new RateWindow<>(time, value, this.name);
    }

    @Override
    protected void deprecated(RateWindow<?> window) {
        if (window != null) {
            window.expire();
        }
    }

    public String htmlStat() {
        return this.htmlStat(null);
    }

    public String htmlStat(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"1\" style=\"border-collapse:collapse;text-align:center;\">");
        builder.append("  <thead>");
        builder.append("    <tr>");
        builder.append("      <th colspan=\"2\" style=\"padding:5px;\">");
        builder.append(this.getName());
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("  </thead>");
        builder.append("  <tbody>");
        List<RateWindow<?>> statistics = this.statistics(key);
        long totalPassed = 0;

        for (RateWindow<?> window : statistics) {
            if (window != null) {
                long passed = window.get();
                totalPassed += passed;
                builder.append("    <tr>");
                builder.append("      <td style=\"padding:5px;\">");
                builder.append(window.getValue().getKey());
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
        builder.append("    <tr>");
        builder.append("      <th style=\"padding:5px;\">limit</th>");
        builder.append("      <th style=\"padding:5px;\">");
        builder.append(this.limit);
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("  </tfoot>");
        builder.append("</table>");
        return builder.toString();
    }

    private Duration getExpireTime() {
        if (this.expire != null && this.expire != Duration.ZERO) {
            return this.expire;
        }
        return Duration.ofSeconds(TimeBoundRateValue.DEFAULT_EXPIRE.getSeconds() * 2);
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Persist implements Serializable {
        @Serial
        private static final long serialVersionUID = 4033100182607933453L;

        private Long expire;

        private Integer intervalInMs;

        private Long limit;

        private List<Long> times;
    }

}
