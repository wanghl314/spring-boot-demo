package com.whl.spring.demo.controller;

import com.whl.spring.demo.config.StorageConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisControllerLockTests {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private StorageConfig storage;

    @Mock
    private RLock lock;

    @InjectMocks
    private RedisController redisController;

    @Test
    void reduceStorage_doesNotUnlockWhenTryLockFails() throws Exception {
        when(storage.getStorage()).thenReturn(10);
        when(redissonClient.getLock(StorageConfig.STORAGE_LOCK)).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(false);

        String result = redisController.reduceStorage(1);

        assertEquals("人太多了，请稍后重试！", result);
        verify(lock, never()).unlock();
        verify(storage, never()).reduceStorage(anyInt());
    }

    @Test
    void reduceStorage_unlocksWhenLockAcquired() throws Exception {
        when(storage.getStorage()).thenReturn(10);
        when(redissonClient.getLock(StorageConfig.STORAGE_LOCK)).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        when(storage.reduceStorage(eq(1))).thenReturn(1);

        String result = redisController.reduceStorage(1);

        assertEquals("获取到1个库存。", result);
        verify(lock).unlock();
    }

}
