package com.whl.spring.demo.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * 定义事件处理器，即消费者
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Event: " + event);
    }

}
