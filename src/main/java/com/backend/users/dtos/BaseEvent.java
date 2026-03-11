package com.backend.users.dtos;

import java.time.OffsetDateTime;

import com.backend.users.config.KafkaEventProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEvent {
  private String timestamp;
  private String environment;
  private UserPayloadDto payload;

  public BaseEvent(
      String environment, UserPayloadDto payload) {
    this.timestamp = String.valueOf(OffsetDateTime.now().toInstant().toEpochMilli());
    this.environment = environment;
    this.payload = payload;
  }
}
