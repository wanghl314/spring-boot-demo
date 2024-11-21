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

    @Bean
    public com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter atomicLongTestV1() {
        com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter limiter = new com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter("AtomicLong-Test-V1", 10000, 200);
        limiter.init();
        return limiter;
    }

    @Bean
    public com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter atomicLongTestV2() {
        com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter limiter = new com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter("AtomicLong-Test-V2", 10000, 200);
        limiter.init();
        return limiter;
    }

    @Configuration
    static class TimeBasedRateLimiterSlidingConfig {
        private final List<TimeBasedRateLimiter> timeLimiters;

        private final List<RedisRateLimiter> redisLimiters;

        private final List<com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter> atomicLongLimitersV1;

        private final List<com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter> atomicLongLimitersV2;

        TimeBasedRateLimiterSlidingConfig(ObjectProvider<List<TimeBasedRateLimiter>> timeProvider,
                                          ObjectProvider<List<RedisRateLimiter>> redisProvider,
                                          ObjectProvider<List<com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter>> atomicV1Provider,
                                          ObjectProvider<List<com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter>> atomicV2Provider) {
            this.timeLimiters = timeProvider.getIfAvailable();
            this.redisLimiters = redisProvider.getIfAvailable();
            this.atomicLongLimitersV1 = atomicV1Provider.getIfAvailable();
            this.atomicLongLimitersV2 = atomicV2Provider.getIfAvailable();
        }

        @Scheduled(cron = "0/1 * * * * ?")
        public void sliding() {
            if (CollectionUtils.isNotEmpty(this.timeLimiters)) {
                this.timeLimiters.forEach(limiter -> limiter.sliding(System.currentTimeMillis()));
            }
            if (CollectionUtils.isNotEmpty(this.atomicLongLimitersV1)) {
                this.atomicLongLimitersV1.forEach(limiter -> limiter.sliding(System.currentTimeMillis()));
            }
            if (CollectionUtils.isNotEmpty(this.redisLimiters)) {
                this.redisLimiters.forEach(limiter -> {
                    if (limiter.isEnabled()) {
                        limiter.sliding(System.currentTimeMillis());
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(this.atomicLongLimitersV2)) {
                this.atomicLongLimitersV2.forEach(limiter -> {
                    if (limiter.isEnabled()) {
                        limiter.sliding(System.currentTimeMillis());
                    }
                });
            }
        }
    }

}
