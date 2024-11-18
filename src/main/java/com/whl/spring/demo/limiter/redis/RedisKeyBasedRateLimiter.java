package com.whl.spring.demo.limiter.redis;

import com.whl.spring.demo.limiter.DefaultRateLimiter;
import com.whl.spring.demo.limiter.KeyBasedRateValue;

public class RedisKeyBasedRateLimiter extends DefaultRateLimiter {

    public RedisKeyBasedRateLimiter(String name, int intervalInMs, long limit, KeyBasedRateValue value) {
        super(name, intervalInMs, limit, value);
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

}
