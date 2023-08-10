package com.whl.spring.demo.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@EnableCaching
@Configuration
public class CaffeineConfig {
    public static final String MANUAL_CACHE_NAME = "manualCaffeineCache";

    @Bean(MANUAL_CACHE_NAME)
    public Cache<String, Object> manualCaffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(20)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .build();
    }

}
