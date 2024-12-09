package com.example.kafka.multi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiKafkaApplication {

  public static void main(String[] args) {
    SpringApplication.run(MultiKafkaApplication.class, "--spring.profiles.active=multi");
  }

}
