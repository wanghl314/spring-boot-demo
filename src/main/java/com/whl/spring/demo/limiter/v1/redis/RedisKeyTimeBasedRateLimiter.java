package com.whl.spring.demo.limiter.v1.redis;

import com.whl.spring.demo.limiter.v1.DefaultRateLimiter;
import com.whl.spring.demo.limiter.v1.KeyBasedRateValue;
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
        long current = System.currentTimeMillis();
        String displayKey = this.name + ":" + key + ":" + (current - current % this.intervalInMs);
        long passed = this.passed(key);
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"5\" style=\"background-color:#000;text-align:center;\">");
        builder.append("  <thead>");
        builder.append("    <tr>");
        builder.append("      <th colspan=\"2\" style=\"background-color:#fff;\">");
        builder.append(this.getName());
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("  </thead>");
        builder.append("  <tbody>");
        builder.append("    <tr>");
        builder.append("      <td style=\"background-color:#fff;\">");
        builder.append(displayKey);
        builder.append("</td>");
        builder.append("      <td style=\"background-color:#fff;\">");
        builder.append(passed);
        builder.append("</td>");
        builder.append("    </tr>");
        builder.append("  </tbody>");
        builder.append("  <tfoot>");
        builder.append("    <tr>");
        builder.append("      <th style=\"background-color:#fff;\">totalPassed</th>");
        builder.append("      <th style=\"background-color:#fff;\">");
        builder.append(passed);
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("    <tr>");
        builder.append("      <th style=\"background-color:#fff;\">limit</th>");
        builder.append("      <th style=\"background-color:#fff;\">");
        builder.append(this.limit);
        builder.append("</th>");
        builder.append("    </tr>");
        builder.append("  </tfoot>");
        builder.append("</table>");
        return builder.toString();
    }

    private String index() {
        return String.valueOf(System.currentTimeMillis() / this.getIntervalInMs());
    }

}
