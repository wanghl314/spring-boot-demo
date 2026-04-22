package com.whl.spring.demo.lc;

import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class LocalCacheMessageListener implements MessageListener {
    private RedisSerializer<?> serializer;

    private LocalCache localCache;

    public LocalCacheMessageListener(RedisTemplate<?, ?> redisTemplate,
                                     LocalCache localCache) {
        this.serializer = redisTemplate.getValueSerializer();
        this.localCache = localCache;
    }

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        Object msg = this.serializer.deserialize(message.getBody());

        if (msg instanceof String) {
            LocalCacheCommand command = LocalCacheCommand.valueOf((String) msg);

            if (command == LocalCacheCommand.CLEAR) {
                this.localCache.clear();
            }
        }
    }

}
