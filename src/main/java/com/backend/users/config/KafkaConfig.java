package com.backend.users.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.users.dtos.BaseEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
  private final KafkaProperties kafkaProperties;

  @Bean
  public KafkaSender<String, BaseEvent> kafkaSender() {
    SenderOptions<String, BaseEvent> senderOptions =
        SenderOptions.<String, BaseEvent>create(kafkaProperties.buildProducerProperties())
            .maxInFlight(1024);
    return KafkaSender.create(senderOptions);
  }
}
