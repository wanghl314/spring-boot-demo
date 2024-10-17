package com.whl.spring.demo.config;

import lombok.Getter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class StorageConfig {
    public static final String STORAGE_LOCK = "storage-lock";

    private static final int DEFAULT_AMOUNT = 100;

    private static Logger logger = LoggerFactory.getLogger(StorageConfig.class);

    @Getter
    private int storage = DEFAULT_AMOUNT;

    @Autowired
    private RedissonClient redissonClient;

    public int reduceStorage() {
        return this.reduceStorage(1);
    }

    public int reduceStorage(int count) {
        if (this.storage <= 0) {
            return 0;
        }
        int realCount = Math.min(this.storage, count);
        this.storage -= count;

        if (this.storage < 0) {
            this.storage = 0;
        }
        logger.info("消耗了 {} 个库存，还剩 {} 个库存。", realCount, this.storage);
        return realCount;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshRoutes() {
        RLock lock = this.redissonClient.getLock(STORAGE_LOCK);

        try {
            lock.lock();
            final int COUNT = DEFAULT_AMOUNT - Math.max(this.storage, 0);

            if (COUNT > 0) {
                this.storage = DEFAULT_AMOUNT;
                logger.info("补充 {} 个库存，总共 {} 个库存。", COUNT, this.storage);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

}
