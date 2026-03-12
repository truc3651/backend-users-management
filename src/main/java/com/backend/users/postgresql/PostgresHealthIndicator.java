package com.backend.users.postgresql;

import static com.backend.users.utils.Constants.DATABASE;
import static com.backend.users.utils.Constants.POSTGRESQL;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PostgresHealthIndicator implements ReactiveHealthIndicator {
  private final DatabaseClient databaseClient;

  @Override
  public Mono<Health> health() {
    return databaseClient
        .sql("SELECT 1")
        .fetch()
        .first()
        .map(result -> Health.up().withDetail(DATABASE, POSTGRESQL).build())
        .onErrorResume(
            ex ->
                Mono.just(
                    Health.down().withDetail(DATABASE, POSTGRESQL).withException(ex).build()));
  }
}
