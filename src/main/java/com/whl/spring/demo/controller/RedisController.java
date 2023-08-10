package com.whl.spring.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisTemplate redisTemplate;

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

}
