package com.whl.spring.demo.config;

import com.whl.spring.demo.limiter.v1.KeyBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.TimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.redis.RedisKeyTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.redis.RedisKeyTimeBasedRateValue;
import com.whl.spring.demo.limiter.v1.redis.RedisTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v2.redis.RedisRateLimiter;
import org.apache.commons.collections4.CollectionUtils;
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
    public KeyBasedRateLimiter dingTalkAgentV1() {
        return new RedisKeyTimeBasedRateLimiter("DingTalk-Agent-V1", 1000, 20, new RedisKeyTimeBasedRateValue(this.redisTemplate));
    }

    @Bean
    public TimeBasedRateLimiter dingTalkApiV1() {
        RedisTimeBasedRateLimiter limiter = new RedisTimeBasedRateLimiter("DingTalk-Api-V1", 20000, 10000, this.redisTemplate);
        limiter.init();
        return limiter;
    }

    @Bean
    public RedisRateLimiter dingTalkAgentV2() {
        RedisRateLimiter limiter = new RedisRateLimiter("DingTalk-Agent-V2", 1000, 20, this.redisTemplate);
        limiter.init();
        return limiter;
    }

    @Bean
    public RedisRateLimiter dingTalkApiV2() {
        RedisRateLimiter limiter = new RedisRateLimiter("DingTalk-Api-V2", 20000, 10000, this.redisTemplate);
        limiter.init();
        return limiter;
    }

    @Configuration
    static class TimeBasedRateLimiterSlidingConfig {
        private final List<TimeBasedRateLimiter> timeLimiters;

        private final List<RedisRateLimiter> redisLimiters;

        TimeBasedRateLimiterSlidingConfig(ObjectProvider<List<TimeBasedRateLimiter>> provider,
                                          ObjectProvider<List<RedisRateLimiter>> redisProvider) {
            this.timeLimiters = provider.getIfAvailable();
            this.redisLimiters = redisProvider.getIfAvailable();
        }

        @Scheduled(cron = "0/1 * * * * ?")
        public void sliding() {
            if (CollectionUtils.isNotEmpty(this.timeLimiters)) {
                this.timeLimiters.forEach(limiter -> limiter.sliding(System.currentTimeMillis()));
            }
            if (CollectionUtils.isNotEmpty(this.redisLimiters)) {
                this.redisLimiters.forEach(limiter -> {
                    if (limiter.isEnabled()) {
                        limiter.sliding(System.currentTimeMillis());
                    }
                });
            }
        }
    }

}
