package com.whl.spring.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import jakarta.annotation.PostConstruct;

@Configuration
public class RedisConfig {
    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private RedisTemplate<?, ?> redisTemplate;

    @PostConstruct
    public void init() {
        try {
            RedisSerializer<String> keySerializer = new StringRedisSerializer();
            RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();
            this.redisTemplate.setKeySerializer(keySerializer);
            this.redisTemplate.setHashKeySerializer(keySerializer);
            this.redisTemplate.setValueSerializer(valueSerializer);
            this.redisTemplate.setHashValueSerializer(valueSerializer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
