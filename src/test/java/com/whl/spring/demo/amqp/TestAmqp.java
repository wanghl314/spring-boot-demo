package com.whl.spring.demo.amqp;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;

import com.whl.spring.demo.BootstrapTests;

public class TestAmqp extends BootstrapTests {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String QUEUE_NAME = "test-amqp";

    @Test
    public void producer() {
        this.amqpAdmin.declareQueue(new Queue(QUEUE_NAME));
        this.amqpTemplate.convertAndSend(QUEUE_NAME, "Hello Amqp!");
    }

    @Test
    public void consumer() {
        this.amqpAdmin.declareQueue(new Queue(QUEUE_NAME));
        Object message = this.amqpTemplate.receiveAndConvert(QUEUE_NAME);
        System.out.println("接收到的消息为: " + message);
    }

}
