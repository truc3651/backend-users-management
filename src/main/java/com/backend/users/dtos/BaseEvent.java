package com.backend.users.dtos;

import java.time.OffsetDateTime;

import com.backend.users.config.KafkaEventProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEvent {
  private String timestamp = String.valueOf(OffsetDateTime.now().toInstant().toEpochMilli());
  private String environment;
  private KafkaEventProperties.EventName eventName;
  private UserEventDto payload;

  public BaseEvent(
      String environment, KafkaEventProperties.EventName eventName, UserEventDto payload) {
    this.environment = environment;
    this.eventName = eventName;
    this.payload = payload;
  }
}
