package com.whl.spring.demo.config;

import com.whl.spring.demo.limiter.KeyBasedRateLimiter;
import com.whl.spring.demo.limiter.TimeBasedRateLimiter;
import com.whl.spring.demo.limiter.redis.RedisKeyBasedRateLimiter;
import com.whl.spring.demo.limiter.redis.RedisKeyBasedRateValue;
import com.whl.spring.demo.limiter.redis.RedisTimeBasedRateLimiter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
public class RateLimiterConfig {
    @Autowired
    private RedisTemplate<?, ?> redisTemplate;

    @Bean
    public KeyBasedRateLimiter dingTalkAgent() {
        return new RedisKeyBasedRateLimiter("DingTalk-Agent", 1000, 20, new RedisKeyBasedRateValue(this.redisTemplate));
    }

    @Bean
    public TimeBasedRateLimiter dingTalkApi() {
        RedisTimeBasedRateLimiter limiter = new RedisTimeBasedRateLimiter("DingTalk-Api", 20000, 10000, this.redisTemplate);
        limiter.init();
        return limiter;
    }

    @Configuration
    static class TimeBasedRateLimiterSlidingConfig {
        private final List<TimeBasedRateLimiter> timeLimiters;

        TimeBasedRateLimiterSlidingConfig(ObjectProvider<List<TimeBasedRateLimiter>> provider) {
            this.timeLimiters = provider.getIfAvailable();
        }

        @Scheduled(cron = "0/1 * * * * ?")
        public void sliding() {
            for (TimeBasedRateLimiter limiter : this.timeLimiters) {
                limiter.sliding(System.currentTimeMillis());
            }
        }

    }

}
