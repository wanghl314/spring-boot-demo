package com.whl.spring.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class AmqpConfig {
    public static final String TEST_QUEUE = "test-queue";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter("java.lang", "java.util", "com.whl.spring.demo");
    }

    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, true);
    }

}
