package com.whl.spring.demo.config;

import com.whl.spring.demo.limiter.v1.KeyBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.TimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.redis.RedisKeyTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v1.redis.RedisKeyTimeBasedRateValue;
import com.whl.spring.demo.limiter.v1.redis.RedisTimeBasedRateLimiter;
import com.whl.spring.demo.limiter.v2.AbstractRateLimiter;
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
        return new RedisTimeBasedRateLimiter("DingTalk-Api-V1", 20000, 10000, this.redisTemplate);
    }

    @Bean
    public RedisRateLimiter dingTalkAgentV2() {
        return new RedisRateLimiter("DingTalk-Agent-V2", 1000, 20, this.redisTemplate);
    }

    @Bean
    public RedisRateLimiter dingTalkApiV2() {
        return new RedisRateLimiter("DingTalk-Api-V2", 20000, 10000, this.redisTemplate);
    }

    @Bean
    public com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter atomicLongTestV1() {
        return new com.whl.spring.demo.limiter.v1.atomic.AtomicLongRateLimiter("AtomicLong-Test-V1", 10000, 200);
    }

    @Bean
    public com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter atomicLongTestV2() {
        return new com.whl.spring.demo.limiter.v2.atomic.AtomicLongRateLimiter("AtomicLong-Test-V2", 10000, 200);
    }

    @Configuration
    static class TimeBasedRateLimiterSlidingConfig {
        private final List<TimeBasedRateLimiter> v1TimeLimiters;

        private final List<AbstractRateLimiter> v2Limiters;

        TimeBasedRateLimiterSlidingConfig(ObjectProvider<List<TimeBasedRateLimiter>> v1Provider,
                                          ObjectProvider<List<AbstractRateLimiter>> v2Provider) {
            this.v1TimeLimiters = v1Provider.getIfAvailable();
            this.v2Limiters = v2Provider.getIfAvailable();

            if (CollectionUtils.isNotEmpty(this.v1TimeLimiters)) {
                this.v1TimeLimiters.forEach(TimeBasedRateLimiter::init);
            }
            if (CollectionUtils.isNotEmpty(this.v2Limiters)) {
                this.v2Limiters.forEach(AbstractRateLimiter::init);
            }
        }

        @Scheduled(cron = "0/1 * * * * ?")
        public void sliding() {
            if (CollectionUtils.isNotEmpty(this.v1TimeLimiters)) {
                this.v1TimeLimiters.forEach(limiter -> limiter.sliding(System.currentTimeMillis()));
            }
            if (CollectionUtils.isNotEmpty(this.v2Limiters)) {
                this.v2Limiters.forEach(limiter -> {
                    if (limiter.isInited() && limiter.isEnabled()) {
                        limiter.sliding(System.currentTimeMillis());
                    }
                });
            }
        }
    }

}
