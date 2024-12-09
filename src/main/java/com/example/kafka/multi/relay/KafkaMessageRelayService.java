package com.example.kafka.multi.relay;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageRelayService {

    @Qualifier("producer1")
    private final KafkaTemplate<String, Object> producer1KafkaTemplate;

    @Qualifier("producer2")
    private final KafkaTemplate<String, Object> producer2KafkaTemplate;

    private static final String RELAY_PREFIX = "[RELAYED]";

    @KafkaListener(topics = "topic1", groupId = "relay-group1",
            containerFactory = "consumer1ContainerFactory")
    public void handleCluster1Messages(String message) {
        log.info("Relay Service: Cluster 1 received message: {}", message);

        if (!message.startsWith(RELAY_PREFIX)) {
            String relayedMessage = RELAY_PREFIX + " " + message;
            log.info("Relay Service: Forwarding from Cluster 1 to topic2: {}", relayedMessage);

            ListenableFuture<SendResult<String, Object>> future = producer2KafkaTemplate.send("topic2", relayedMessage);

            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    log.info("Relay Service: Successfully forwarded from topic1 to topic2");
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Relay Service: Failed to forward from topic1 to topic2", ex);
                }
            });
        } else {
            log.info("Relay Service: Skipping already relayed message in topic1: {}", message);
        }
    }

    @KafkaListener(topics = "topic2", groupId = "relay-group2",
            containerFactory = "consumer2ContainerFactory")
    public void handleCluster2Messages(String message) {
        log.info("Relay Service: Cluster 2 received message: {}", message);

        if (!message.startsWith(RELAY_PREFIX)) {
            String relayedMessage = RELAY_PREFIX + " " + message;
            log.info("Relay Service: Forwarding from Cluster 2 to topic1: {}", relayedMessage);

            ListenableFuture<SendResult<String, Object>> future = producer1KafkaTemplate.send("topic1", relayedMessage);

            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    log.info("Relay Service: Successfully forwarded from topic2 to topic1");
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Relay Service: Failed to forward from topic2 to topic1", ex);
                }
            });
        } else {
            log.info("Relay Service: Skipping already relayed message in topic2: {}", message);
        }
    }
}