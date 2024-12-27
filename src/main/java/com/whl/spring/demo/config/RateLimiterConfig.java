package com.whl.spring.demo.config;

import com.whl.spring.demo.limiter.redis.RedisRateLimiter;
import com.whl.spring.demo.limiter.redis.RedisRateLimiterManager;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RedisRateLimiterManager rateLimiterManager(RedisTemplate<?, ?> redisTemplate) {
        return new RedisRateLimiterManager(redisTemplate);
    }

    @Configuration
    static class TimeBasedRateLimiterSlidingConfig {
        @Autowired
        private RedisRateLimiterManager rateLimiterManager;

        @Scheduled(cron = "0/1 * * * * ?")
        public void sliding() {
            long timeMillis = System.currentTimeMillis();
            List<RedisRateLimiter> rateLimiters = this.rateLimiterManager.getAllRateLimiters();

            if (CollectionUtils.isNotEmpty(rateLimiters)) {
                rateLimiters.forEach(limiter -> {
                    if (limiter.isInited() && limiter.isEnabled()) {
                        limiter.sliding(timeMillis);
                    }
                });
            }
        }
    }

}
