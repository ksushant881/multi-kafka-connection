package com.example.kafka.multi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka/test")
@Slf4j
@RequiredArgsConstructor
public class KafkaDiagnosticController {

    @Qualifier("producer1")
    private final KafkaTemplate<String, Object> producer1KafkaTemplate;

    @Qualifier("producer2")
    private final KafkaTemplate<String, Object> producer2KafkaTemplate;

    @GetMapping("/send1/{message}")
    public String testCluster1(@PathVariable String message) {
        log.info("Testing send to topic1: {}", message);
        producer1KafkaTemplate.send("topic1", message)
                              .addCallback(
                                      result -> log.info("Message sent to topic1 successfully"),
                                      ex -> log.error("Failed to send to topic1", ex)
                                          );
        return "Test message sent to topic1";
    }

    @GetMapping("/send2/{message}")
    public String testCluster2(@PathVariable String message) {
        log.info("Testing send to topic2: {}", message);
        producer2KafkaTemplate.send("topic2", message)
                              .addCallback(
                                      result -> log.info("Message sent to topic2 successfully"),
                                      ex -> log.error("Failed to send to topic2", ex)
                                          );
        return "Test message sent to topic2";
    }
}