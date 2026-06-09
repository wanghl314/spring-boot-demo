package com.whl.spring.demo.controller;

import com.whl.spring.demo.config.StorageConfig;
import org.redisson.api.RLock;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisController {
    private static Logger logger = LoggerFactory.getLogger(RedisController.class);

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StorageConfig storage;

    @SuppressWarnings("unchecked")
    @Autowired
    public void setRedisTemplate(RedisTemplate<?, ?> redisTemplate) {
        this.redisTemplate = (RedisTemplate<String, Object>) redisTemplate;
    }

    @GetMapping("/getByKey/{key}")
    public Object getByKey(@PathVariable String key) throws Exception {
        return this.redisTemplate.opsForValue().get(key);
    }

    @PostMapping("/set/{key}")
    public int set(@PathVariable String key, @RequestBody Object data) throws Exception {
        this.redisTemplate.opsForValue().set(key, data);
        return 1;
    }

    @GetMapping("/deleteByKey/{key}")
    public int deleteByKey(@PathVariable String key) throws Exception {
        this.redisTemplate.opsForValue().getAndDelete(key);
        return 1;
    }

    @GetMapping("/reduceStorage")
    public String reduceStorage(@RequestParam(defaultValue = "1") int count) {
        count = Math.max(count, 1);

        if (this.storage.getStorage() <= 0) {
            return "没库存了！";
        }
        RLock lock = this.redissonClient.getLock(StorageConfig.STORAGE_LOCK);

        try {
            if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                return "人太多了，请稍后重试！";
            }
            int realCount = this.storage.reduceStorage(count);
            return "获取到" + realCount + "个库存。";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "系统繁忙，请稍后重试！";
        } finally {
            lock.unlock();
        }
    }

    @GetMapping("/rateLimiter")
    public String test(@RequestParam String key) {
        RRateLimiter limiter = this.redissonClient.getRateLimiter("qps2" + key);
        // 用 PER_CLIENT 当全局限流：它是单机独立计数，集群下会失效。
        // 重复 setRate 不生效：trySetRate 是幂等的，已设置过就不会改。
        limiter.trySetRate(RateType.OVERALL, 2, Duration.ofSeconds(1));
        // 不设过期时间：Redis key 会永久存在，建议加 expire。
        limiter.expire(Duration.ofMinutes(30));

        if (limiter.tryAcquire(1)) {
            logger.info("[限流通过] key={}, 剩余令牌={}", key, limiter.availablePermits());
            return "正在执行业务逻辑。。。";
        } else {
            logger.warn("[限流拒绝] key={}, 剩余令牌={}", key, limiter.availablePermits());
            return "请求过于频繁，请稍后重试！";
        }
    }

}
