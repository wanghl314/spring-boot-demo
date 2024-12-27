package com.whl.spring.demo.controller;

import com.whl.spring.demo.limiter.RateWindow;
import com.whl.spring.demo.limiter.redis.RedisRateLimiter;
import com.whl.spring.demo.limiter.redis.RedisRateLimiterManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ratelimiter")
public class RateLimiterController {
    private static Logger logger = LoggerFactory.getLogger(RateLimiterController.class);

    private static Map<String, List<RedisRateLimiter>> limiters = new HashMap<>();
    
    @Autowired
    private RedisRateLimiterManager rateLimiterManager;

    @GetMapping("/init")
    public String init() {
        String category = "test";
        String globalKey = "global";
        String testKey = "/test";
        RedisRateLimiter qps = this.rateLimiterManager.addRateLimiter(category, "qps", 1000, 2);
        RedisRateLimiter qpm = this.rateLimiterManager.addRateLimiter(category, "qpm", 60000, 100);
        RedisRateLimiter custom1 = this.rateLimiterManager.addRateLimiter(category, 1000, 1);
        RedisRateLimiter custom2 = this.rateLimiterManager.addRateLimiter(category, 60000, 50);
        limiters.put(globalKey, Arrays.asList(qps, qpm));
        limiters.put(testKey, Arrays.asList(custom1, custom2));
        return "SUCCESS";
    }

    @GetMapping("/test")
    public String test(@RequestParam String path, @RequestParam String key) {
        long current = System.currentTimeMillis();

        if (MapUtils.isNotEmpty(limiters)) {
            boolean limited = false;
            String message = null;
            // 全局级别限流器校验
            List<RedisRateLimiter> globalLimiters = null;

            if (limiters.containsKey("global")) {
                globalLimiters = new ArrayList<>(limiters.get("global"));
            }

            if (CollectionUtils.isNotEmpty(globalLimiters)) {
                for (RedisRateLimiter limiter : globalLimiters) {
                    RateWindow<?> window = limiter.currentWindow(current);

                    if (limiter.isLimit()) {
                        limited = true;
                        message = "\n【全局限流控制】：" + limiter;
                        break;
                    }
                    window.incr();
                }
            }

            if (!limited) {
                List<RedisRateLimiter> matchingLimiters = null;

                if (limiters.containsKey(path)) {
                    matchingLimiters = new ArrayList<>(limiters.get(path));
                }

                if (CollectionUtils.isNotEmpty(matchingLimiters)) {
                    for (RedisRateLimiter limiter : matchingLimiters) {
                        String index = path + ":" + key;
                        RateWindow<?> window = limiter.currentWindow(current, index);

                        if (limiter.isLimit(index)) {
                            limited = true;
                            message = "\n【限流控制】：" + limiter + "\n【请求参数】：" + index;
                            break;
                        }
                        window.incr();
                    }
                }
            }

            if (limited) {
                logger.error(message);
                return "限流了";
            }
        }
        return "没限流";
    }

//    @GetMapping("/change")
//    public String change(@RequestParam String intervalInMs, @RequestParam String limit) {
//        int newIntervalInMs = Integer.parseInt(intervalInMs);
//        long newLimit = Long.parseLong(limit);
//        int oldIntervalInMs = this.apiLimiter.getIntervalInMs();
//        long oldLimit = this.agentLimiter.getLimit();
//
//        if (newIntervalInMs != oldIntervalInMs || newLimit != oldLimit) {
//            this.apiLimiter.change(newIntervalInMs, newLimit);
//        }
//        return "SUCCESS";
//    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        List<RedisRateLimiter> rateLimiters = this.rateLimiterManager.getAllRateLimiters();
        List<String> data = new ArrayList<>();

        if (rateLimiters != null) {
            rateLimiters.forEach(limiter -> data.add(limiter.toString()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("data", data);
        return result;
    }

    @GetMapping("/stat/{name}")
    public Map<String, Object> stat(@PathVariable String name, String key) {
        Map<String, Object> result = new HashMap<>();
        List<RedisRateLimiter> rateLimiters = this.rateLimiterManager.getAllRateLimiters();

        if (CollectionUtils.isNotEmpty(rateLimiters)) {
            for (RedisRateLimiter limiter : rateLimiters) {
                if (StringUtils.equals(limiter.getName(), name)) {
                    if (!limiter.isEnabled()) {
                        result.put("code", 3);
                        result.put("msg", "RateLimiter name=" + name + " is disabled");
                        return result;
                    }
                    String stat;

                    if (StringUtils.isNotBlank(key)) {
                        stat = limiter.htmlStat(key);
                    } else {
                        stat = limiter.htmlStat();
                    }
                    result.put("code", 0);
                    result.put("msg", "");
                    result.put("data", stat);
                    return result;
                }
            }
            result.put("code", 2);
            result.put("msg", "RateLimiter name=" + name + " not found");
        } else {
            result.put("code", 1);
            result.put("msg", "No RateLimiter found");
        }
        return result;
    }

}
