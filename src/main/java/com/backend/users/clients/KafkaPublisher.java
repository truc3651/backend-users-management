package com.backend.users.clients;

import java.time.Duration;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.users.config.KafkaEventProperties;
import com.backend.users.dtos.BaseEvent;
import com.backend.users.dtos.BlockEventDto;
import com.backend.users.dtos.FollowEventDto;
import com.backend.users.dtos.UnblockEventDto;
import com.backend.users.dtos.UnfollowEventDto;
import com.fasterxml.jackson.core.JsonParseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisher {
  private static final int MAX_RETRY_ATTEMPTS = 3;
  private static final Duration RETRY_BACKOFF = Duration.ofSeconds(1);

  @Value("${ENVIRONMENT}")
  private String environment;

  private final KafkaEventProperties eventProperties;
  private final KafkaSender<String, BaseEvent> kafkaSender;

  public Mono<Void> sendFollowEvent(FollowEventDto payload) {
    BaseEvent event = new BaseEvent(environment, KafkaEventProperties.EventName.FOLLOW, payload);
    return sendEvent(event, eventProperties.getFollowTopicName());
  }

  public Mono<Void> sendUnfollowEvent(UnfollowEventDto payload) {
    BaseEvent event = new BaseEvent(environment, KafkaEventProperties.EventName.UN_FOLLOW, payload);
    return sendEvent(event, eventProperties.getFollowTopicName());
  }

  public Mono<Void> sendBlockEvent(BlockEventDto payload) {
    BaseEvent event = new BaseEvent(environment, KafkaEventProperties.EventName.BLOCK, payload);
    return sendEvent(event, eventProperties.getBlockTopicName());
  }

  public Mono<Void> sendUnblockEvent(UnblockEventDto payload) {
    BaseEvent event = new BaseEvent(environment, KafkaEventProperties.EventName.UN_BLOCK, payload);
    return sendEvent(event, eventProperties.getBlockTopicName());
  }

  private Mono<Void> sendEvent(BaseEvent event, String topic) {
    String key = String.valueOf(event.getPayload().getUserId());
    ProducerRecord<String, BaseEvent> producerRecord = new ProducerRecord<>(topic, key, event);
    SenderRecord<String, BaseEvent, String> senderRecord = SenderRecord.create(producerRecord, key);

    return kafkaSender
        .send(Mono.just(senderRecord))
        .next()
        .flatMap(
            result -> {
              if (result.exception() != null) {
                return Mono.error(result.exception());
              }
              log.info(
                  "Successfully sent event to topic '{}' with key '{}' at offset {}",
                  topic,
                  key,
                  result.recordMetadata().offset());
              return Mono.empty();
            })
        .retryWhen(
            Retry.fixedDelay(MAX_RETRY_ATTEMPTS, RETRY_BACKOFF)
                .filter(this::isRetryableException)
                .doBeforeRetry(
                    retrySignal ->
                        log.warn(
                            "Retrying to send event to topic '{}' with key '{}', attempt {}",
                            topic,
                            key,
                            retrySignal.totalRetries() + 1)))
        .onErrorResume(
            e -> {
              log.error(
                  "Failed to send event to topic '{}' with key '{}' after {} retries: {}",
                  topic,
                  key,
                  MAX_RETRY_ATTEMPTS,
                  e.getMessage());
              return sendToDeadLetterTopic(event, key, e);
            })
        .then();
  }

  private boolean isRetryableException(Throwable e) {
    return !(e instanceof IllegalArgumentException) && !(e instanceof JsonParseException);
  }

  private Mono<Void> sendToDeadLetterTopic(BaseEvent event, String key, Throwable originalError) {
    String deadLetterTopic = eventProperties.getDeadLetterTopicName();
    if (deadLetterTopic == null || deadLetterTopic.isBlank()) {
      log.warn("Dead letter topic not configured, discarding failed event with key '{}'", key);
      return Mono.empty();
    }

    ProducerRecord<String, BaseEvent> dlqRecord = new ProducerRecord<>(deadLetterTopic, key, event);
    SenderRecord<String, BaseEvent, String> senderRecord = SenderRecord.create(dlqRecord, key);

    return kafkaSender
        .send(Mono.just(senderRecord))
        .next()
        .doOnNext(
            result ->
                log.info(
                    "Sent failed event to dead letter topic '{}' with key '{}', original error: {}",
                    deadLetterTopic,
                    key,
                    originalError.getMessage()))
        .doOnError(
            e ->
                log.error(
                    "Failed to send event to dead letter topic '{}' with key '{}': {}",
                    deadLetterTopic,
                    key,
                    e.getMessage()))
        .then();
  }
}
