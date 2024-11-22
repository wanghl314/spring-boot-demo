package com.whl.spring.demo.controller;

import com.whl.spring.demo.limiter.v1.KeyBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.RateWindow;
import com.whl.spring.demo.limiter.v1.TimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.redis.RedisKeyTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.redis.RedisTimeBasedRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rateLimiter/v1")
public class RateLimiterV1Controller {
    @Autowired
    private List<KeyBasedRateLimiter> keyLimiters;

    @Autowired
    private List<TimeBasedRateLimiter> timeLimiters;

    @GetMapping("")
    public String index(@RequestParam String key) {
        for (KeyBasedRateLimiter limiter : this.keyLimiters) {
            long passed = limiter.passed(key);

            if (passed >= limiter.getLimit()) {
                return "限流了: " + limiter;
            }
            limiter.incr(key);
        }

        for (TimeBasedRateLimiter limiter : this.timeLimiters) {
            RateWindow<?> window = limiter.currentWindow(System.currentTimeMillis());
            long passed = limiter.passed();

            if (passed >= limiter.getLimit()) {
                return "限流了: " + limiter;
            }
            window.incr();
        }
        return "没限流";
    }

    @GetMapping("/stat")
    public String stat(@RequestParam String key) {
        StringBuilder builder = new StringBuilder();

        for (KeyBasedRateLimiter limiter : this.keyLimiters) {
            builder.append(((RedisKeyTimeBasedRateLimiter) limiter).htmlStat(key));
        }

        for (TimeBasedRateLimiter limiter : this.timeLimiters) {
            if (limiter instanceof RedisTimeBasedRateLimiter limiter1) {
                builder.append(limiter1.htmlStat());
            }
        }
        return builder.toString();
    }

}
