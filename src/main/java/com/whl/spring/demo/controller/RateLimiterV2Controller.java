package com.whl.spring.demo.controller;

import com.whl.spring.demo.limiter.v2.RateWindow;
import com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter;
import com.whl.spring.demo.limiter.v2.redis.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rateLimiter/v2")
public class RateLimiterV2Controller {

    @Autowired
    @Qualifier("dingTalkAgentV2")
    private RedisRateLimiter agentLimiter;

    @Autowired
    @Qualifier("dingTalkApiV2")
    private RedisRateLimiter apiLimiter;

    @Autowired
    @Qualifier("atomicLongTestV2")
    private AtomicLongRateLimiter atomicLongLimiter;

    @GetMapping("")
    public String index(@RequestParam String key) {
        long currentTime = System.currentTimeMillis();
        RateWindow<?> window = this.agentLimiter.currentWindow(currentTime, key);

        if (this.agentLimiter.isLimit(key)) {
            return "限流了: " + this.agentLimiter;
        }
        window.incr();
        window = this.apiLimiter.currentWindow(currentTime);

        if (this.apiLimiter.isLimit()) {
            return "限流了: " + this.apiLimiter;
        }
        window.incr();
        return "没限流";
    }

    @GetMapping("/change")
    public String change(@RequestParam String intervalInMs, @RequestParam String limit) {
        int newIntervalInMs = Integer.parseInt(intervalInMs);
        long newLimit = Long.parseLong(limit);
        int oldIntervalInMs = this.apiLimiter.getIntervalInMs();
        long oldLimit = this.agentLimiter.getLimit();

        if (newIntervalInMs != oldIntervalInMs || newLimit != oldLimit) {
            this.apiLimiter.change(newIntervalInMs, newLimit);
        }
        return "SUCCESS";
    }

    @GetMapping("/stat")
    public String stat(@RequestParam String key) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.agentLimiter.htmlStat(key));
        builder.append("<br/>");
        builder.append(this.apiLimiter.htmlStat());
        builder.append("<br/>");
        builder.append(this.atomicLongLimiter.htmlStat());
        builder.append("<br/>");
        return builder.toString();
    }

}
