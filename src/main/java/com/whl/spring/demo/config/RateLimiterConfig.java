package com.whl.spring.demo.config;

import com.whl.spring.demo.limiter.redis.RedisRateLimiter;
import com.whl.spring.demo.limiter.redis.RedisRateLimiterManager;
import org.apache.commons.collections4.CollectionUtils;
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
        private final RedisRateLimiterManager rateLimiterManager;

        TimeBasedRateLimiterSlidingConfig(RedisRateLimiterManager rateLimiterManager) {
            this.rateLimiterManager = rateLimiterManager;
            this.rateLimiterManager.buildRateLimiter("20qps", 1000, 20);
            this.rateLimiterManager.buildRateLimiter("10000qp20s", 20000, 10000);
            this.rateLimiterManager.buildRateLimiter("50qps", 1000, 50);
            this.rateLimiterManager.buildRateLimiter("1000qpm", 60000, 1000);
            this.rateLimiterManager.init();
        }

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
