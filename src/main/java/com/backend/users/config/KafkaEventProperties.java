package com.backend.users.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-properties")
public class KafkaEventProperties {
  private String followTopicName;
  private String blockTopicName;
  private String deadLetterTopicName;

  public enum EventName {
    FOLLOW,
    UN_FOLLOW,
    BLOCK,
    UN_BLOCK
  }
}
