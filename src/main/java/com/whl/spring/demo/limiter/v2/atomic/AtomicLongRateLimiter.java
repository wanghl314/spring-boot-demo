package com.whl.spring.demo.limiter.v2.atomic;

import com.whl.spring.demo.limiter.v2.AbstractRateLimiter;
import com.whl.spring.demo.limiter.v2.RateWindow;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongRateLimiter extends AbstractRateLimiter {

    public AtomicLongRateLimiter(String name, int intervalInMs, long limit) {
        super(name, intervalInMs, limit);
    }

    public AtomicLongRateLimiter(String name, int intervalInMs, int windowLengthInMs, long limit) {
        super(name, intervalInMs, windowLengthInMs, limit);
    }

    @Override
    protected RateWindow<?> newWindow(long time) {
        return new RateWindow<>(time, new AtomicLongRateValue(new AtomicLong()));
    }

    @Override
    protected void deprecated(RateWindow<?> window) {
        if (window != null) {
            window.expire();
        }
    }

    public String htmlStat() {
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
        List<RateWindow<?>> statistics = this.statistics();
        long totalPassed = 0;

        for (RateWindow<?> window : statistics) {
            if (window != null) {
                long passed = window.get();
                totalPassed += passed;
                builder.append("    <tr>");
                builder.append("      <td style=\"padding:5px;\">");
                builder.append(window.getTime());
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

}
