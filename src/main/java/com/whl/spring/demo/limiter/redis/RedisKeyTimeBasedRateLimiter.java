package com.whl.spring.demo.limiter.redis;

import com.whl.spring.demo.limiter.DefaultRateLimiter;
import com.whl.spring.demo.limiter.KeyBasedRateValue;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class RedisKeyTimeBasedRateLimiter extends DefaultRateLimiter {
    private final int intervalInMs;

    public RedisKeyTimeBasedRateLimiter(String name, int intervalInMs, long limit, KeyBasedRateValue value) {
        super(name, limit, value);
        Assert.isTrue(intervalInMs > 0, "intervalInMs should be positive");
        this.intervalInMs = intervalInMs;
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

    public String htmlStat(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"1\" style=\"border-collapse:collapse;\">");
        builder.append("<thead>");
        builder.append("<tr>");
        builder.append("<th colspan=\"2\" style=\"padding:5px;\">");
        builder.append(this.getName());
        builder.append("</th>");
        builder.append("</tr>");
        builder.append("</thead>");
        builder.append("<tbody>");
        builder.append("<tr>");
        builder.append("<td style=\"padding:5px;\">passedCount</td>");
        builder.append("<td style=\"padding:5px;\">");
        builder.append(this.passed(key));
        builder.append("</td>");
        builder.append("</tr>");
        builder.append("</tbody");
        return builder.toString();
    }

    private String index() {
        return String.valueOf(System.currentTimeMillis() / this.getIntervalInMs());
    }

}
