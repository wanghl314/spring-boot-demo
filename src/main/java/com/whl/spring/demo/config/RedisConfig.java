package com.whl.spring.demo.config;

import io.lettuce.core.SslVerifyMode;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.lang.reflect.Field;
import java.util.Objects;

@Configuration
public class RedisConfig {
    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis.ssl", value = "enabled", havingValue = "true")
    public LettuceClientConfigurationBuilderCustomizer customizer(@Value("${spring.data.redis.ssl.verify-mode:FULL}") String verifyMode) {
        if (logger.isInfoEnabled()) {
            logger.info("Config SslVerifyMode: {}", verifyMode);
        }
        return builder -> {
            try {
                Field field = LettuceClientConfiguration.LettuceClientConfigurationBuilder.class.getDeclaredField("verifyMode");
                field.setAccessible(true);
                field.set(builder, SslVerifyMode.valueOf(verifyMode));
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        };
    }

    @Bean
    public static BeanPostProcessor redisTemplateBeanPostProcessor() {
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
                    StringRedisSerializer keySerializer = new StringRedisSerializer();
                    GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder()
                            .enableDefaultTyping(
                                    BasicPolymorphicTypeValidator.builder()
                                            .allowIfBaseType(Object.class)
                                            .allowIfSubType((ctx, clazz) -> true)
                                            .build())
                            .build();
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
