package com.whl.spring.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public BeanPostProcessor init() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof RedisTemplate<?, ?> redisTemplate &&
                        !(bean instanceof StringRedisTemplate)) {
                    try {
                        RedisSerializer<String> keySerializer = new StringRedisSerializer();
                        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();
                        redisTemplate.setKeySerializer(keySerializer);
                        redisTemplate.setHashKeySerializer(keySerializer);
                        redisTemplate.setValueSerializer(valueSerializer);
                        redisTemplate.setHashValueSerializer(valueSerializer);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                return bean;
            }
        };
    }

}
