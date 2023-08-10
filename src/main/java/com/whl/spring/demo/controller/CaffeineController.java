package com.whl.spring.demo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.benmanes.caffeine.cache.Cache;
import com.whl.spring.demo.config.CaffeineConfig;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/caffeine")
public class CaffeineController {
    private static Logger logger = LoggerFactory.getLogger(CaffeineController.class);

    @Autowired
    private CacheManager cacheManager;

    @Resource(name = CaffeineConfig.MANUAL_CACHE_NAME)
    private Cache<String, Object> cache;

    @Cacheable(cacheNames = "cache1", key = "#key")
    @GetMapping("/getByKey/{key}")
    public Object getByKey(@PathVariable String key) throws Exception {
        logger.info("hit controller, query cache");
        return "test-cache";
    }

    @CachePut(cacheNames = "cache1", key = "#key")
    @PostMapping("/set/{key}")
    public Object set(@PathVariable String key, @RequestBody Object data) throws Exception {
        logger.info("hit controller, set cache");
        return data;
    }

    @CacheEvict(cacheNames = "cache1", key = "#key")
    @GetMapping("/deleteByKey/{key}")
    public int deleteByKey(@PathVariable String key) throws Exception {
        logger.info("hit controller, remove cache");
        return 1;
    }

    @GetMapping("/manual/getByKey/{key}")
    public Object manualGetByKey(@PathVariable String key) throws Exception {
        Object result = this.cache.getIfPresent(key);

        if (result == null) {
            logger.info("hit controller, query manual cache");
            result = "test-cache";
            this.cache.put(key, result);
        }
        return result;
    }

    @PostMapping("/manual/set/{key}")
    public Object manualSet(@PathVariable String key, @RequestBody Object data) throws Exception {
        logger.info("hit controller, set manual cache");
        this.cache.put(key, data);
        return data;
    }

    @GetMapping("/manual/deleteByKey/{key}")
    public int manualDeleteByKey(@PathVariable String key) throws Exception {
        logger.info("hit controller, remove manual cache");
        this.cache.invalidate(key);
        return 1;
    }

}
