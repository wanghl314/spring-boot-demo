package com.whl.spring.demo.config;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

@Configuration
public class RedisConfig {
    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public BeanPostProcessor redisTemplateBeanPostProcessor() {
        return new RedisTemplateBeanPostProcessor();
    }

    @Configuration
    static class RedisTestConfig {

        public RedisTestConfig(RedisTemplate<?, ?> redisTemplate) {
            redisTemplate.execute((RedisConnection connection) -> {
                String ping = connection.ping();
                logger.info(ping);
                String redisVersion = Objects.requireNonNull(connection.serverCommands().info()).getProperty("redis_version");
                logger.info(redisVersion);
                return null;
            });
        }

    }

    private static class RedisTemplateBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
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

    }

}
