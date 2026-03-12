package com.backend.users.neo4j;

import static com.backend.users.utils.Constants.DATABASE;
import static com.backend.users.utils.Constants.NEO4J;

import org.neo4j.driver.Driver;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Neo4jHealthIndicator implements ReactiveHealthIndicator {
  private final Driver neo4jDriver;

  @Override
  public Mono<Health> health() {
    return Mono.fromCallable(
            () -> {
              neo4jDriver.verifyConnectivity();
              return true;
            })
        .map(connected -> Health.up().withDetail(DATABASE, NEO4J).build())
        .onErrorResume(
            ex -> Mono.just(Health.down().withDetail(DATABASE, NEO4J).withException(ex).build()));
  }
}
