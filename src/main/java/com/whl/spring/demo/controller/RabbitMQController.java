package com.whl.spring.demo.controller;

import com.rabbitmq.client.Channel;
import com.whl.spring.demo.bean.Message;
import com.whl.spring.demo.config.AmqpConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitMQController {
    private static Logger logger = LoggerFactory.getLogger(RabbitMQController.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping("/send")
    public String send(@RequestBody Message message) {
        if (StringUtils.isBlank(message.getUuid())) {
            message.setUuid(UUID.randomUUID().toString());
        }
        this.amqpTemplate.convertAndSend(AmqpConfig.TEST_QUEUE, message);
        return "SUCCESS";
    }

    @RabbitListener(queues = AmqpConfig.TEST_QUEUE)
    public void receive(Message msg, org.springframework.amqp.core.Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            logger.info("{}", msg);
            Random random = new Random();

            if (random.nextBoolean()) {
                throw new RuntimeException("this is a mock Exception");
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            logger.info("catch exception: {}, requeue", e.getClass());
            channel.basicNack(deliveryTag, false, true);
        }
    }

}
