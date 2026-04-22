package com.whl.spring.demo.config;

import com.whl.spring.demo.lc.LocalCache;
import com.whl.spring.demo.lc.LocalCacheMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class LocalCacheConfig {

    @Bean
    public Topic topic() {
        return new ChannelTopic("topic-localcache");
    }

    @Bean
    public LocalCache localCache() {
        return new LocalCache();
    }

    @Bean
    public MessageListener listener(RedisTemplate<?, ?> redisTemplate,
                                    LocalCache localCache) {
        return new LocalCacheMessageListener(redisTemplate, localCache);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory,
                                                   MessageListener localCacheMessageListener,
                                                   Topic testTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(localCacheMessageListener, testTopic);
        return container;
    }

}
