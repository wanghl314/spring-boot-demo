package com.whl.spring.demo.config;

import com.whl.spring.demo.limiter.redis.RedisRateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.type.TypeFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RedisPolymorphicTypeValidatorTests {

    private final GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
            .enableDefaultTyping(RedisConfig.redisPolymorphicTypeValidator())
            .build();

    @Test
    void allowsAppAndJdkTypes() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "demo");
        map.put("count", 1);

        assertEquals(map, roundTrip(map));

        RedisRateLimiter.Persist persist = new RedisRateLimiter.Persist(1L, 1000, 10L, List.of(1L, 2L));
        RedisRateLimiter.Persist restored = (RedisRateLimiter.Persist) roundTrip(persist);
        assertEquals(persist.getExpire(), restored.getExpire());
        assertEquals(persist.getLimit(), restored.getLimit());
        assertEquals(persist.getTimes(), restored.getTimes());
    }

    @Test
    void rejectsUnsafeTypesOutsideAllowlist() {
        PolymorphicTypeValidator validator = RedisConfig.redisPolymorphicTypeValidator();
        JavaType objectType = TypeFactory.createDefaultInstance().constructType(Object.class);

        assertSame(
                PolymorphicTypeValidator.Validity.ALLOWED,
                validator.validateSubClassName(null, objectType, "com.whl.spring.demo.lc.LocalCacheCommand"));
        assertSame(
                PolymorphicTypeValidator.Validity.ALLOWED,
                validator.validateSubClassName(null, objectType, "java.util.HashMap"));

        assertNotSame(
                PolymorphicTypeValidator.Validity.ALLOWED,
                validator.validateSubClassName(null, objectType, "com.alibaba.druid.pool.DruidDataSource"));
        assertNotSame(
                PolymorphicTypeValidator.Validity.ALLOWED,
                validator.validateSubClassName(null, objectType, "java.io.File"));
    }

    @Test
    void deserializeRejectsNonAllowlistedClassName() {
        assertThrows(SerializationException.class,
                () -> serializer.deserialize("{\"@class\":\"com.alibaba.druid.pool.DruidDataSource\"}".getBytes()));
        assertThrows(SerializationException.class,
                () -> serializer.deserialize("[\"java.io.File\",\"C:/windows/win.ini\"]".getBytes()));
    }

    private Object roundTrip(Object value) {
        return serializer.deserialize(serializer.serialize(value));
    }

}
