package com.example.kafka.multi.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaSecondConsumerServiceImpl implements KafkaConsumerService {

    @KafkaListener(topics = { "${kafka.consumer.consumer2.topic}" },
            groupId = "${kafka.consumer.consumer2.group-id}",
            containerFactory = "consumer2ContainerFactory")
    public void receive(@Payload String message) {
        log.info("message received in consumer2: {}", message);
    }
}