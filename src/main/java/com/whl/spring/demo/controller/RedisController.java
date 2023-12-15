package com.whl.spring.demo.controller;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whl.spring.demo.config.StorageConfig;

@RestController
@RequestMapping("/redis")
public class RedisController {
    private static Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StorageConfig storage;

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

}
