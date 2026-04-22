package com.whl.spring.demo.controller;

import com.whl.spring.demo.lc.LocalCache;
import com.whl.spring.demo.lc.LocalCacheCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/localCache")
public class LocalCacheController {
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Topic topic;

    @Autowired
    private LocalCache localCache;

    @SuppressWarnings("unchecked")
    @Autowired
    public void setRedisTemplate(RedisTemplate<?, ?> redisTemplate) {
        this.redisTemplate = (RedisTemplate<String, Object>) redisTemplate;
    }

    @GetMapping("/get/{key}")
    public Object getCache(@PathVariable String key) {
        return this.localCache.get(key);
    }

    @PostMapping("/set/{key}")
    public String setCache(@PathVariable String key, @RequestBody Object value) {
        this.localCache.set(key, value);
        return "SUCCESS";
    }

    @DeleteMapping("/del/{key}")
    public String delCache(@PathVariable String key) {
        this.localCache.del(key);
        return "SUCCESS";
    }

    @GetMapping("/clear")
    public String clearCache() {
//        this.localCache.clear();
        this.redisTemplate.convertAndSend(topic.getTopic(), LocalCacheCommand.CLEAR);
        return "SUCCESS";
    }

}
