package com.backend.users.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {
  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
        .withDatabaseName("test")
        .withUsername("admin")
        .withPassword("veryStrongPassword");
  }

  @Bean
  @ServiceConnection
  public Neo4jContainer<?> neo4jContainer() {
    return new Neo4jContainer<>("neo4j:5").withAdminPassword("veryStrongPassword");
  }

  @Bean
  @ServiceConnection
  public RedisContainer redisContainer() {
    return new RedisContainer(DockerImageName.parse("redis:7-alpine"));
  }

  @Bean
  @ServiceConnection
  public KafkaContainer kafkaContainer() {
    return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));
  }
}
