package com.backend.users.neo4j;

import static com.backend.users.neo4j.Neo4jDriverType.READER;
import static com.backend.users.neo4j.Neo4jDriverType.WRITER;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass({Driver.class})
public class Neo4jDriverConfig {
  @Autowired protected Neo4jDriverFactory driverFactory;

  @Bean
  protected Config neo4jCommonConfig() {
    return Config.builder()
        .withMaxConnectionPoolSize(50)
        // If pool is exhausted, request waits up to 60 seconds
        .withConnectionAcquisitionTimeout(60, TimeUnit.SECONDS)
        // How long to wait when opening a NEW connection to Neo4j
        .withConnectionTimeout(30, TimeUnit.SECONDS)
        // Maximum lifetime of a connection before it's CLOSED and recreated
        .withMaxConnectionLifetime(1, TimeUnit.HOURS)
        // Timeout for checking if an IDLE connection is still alive before using it
        .withConnectionLivenessCheckTimeout(5, TimeUnit.SECONDS)
        .build();
  }

  @Bean(WRITER)
  protected Driver writerNeo4jDriver() {
    return driverFactory.getDriver(WRITER, neo4jCommonConfig());
  }

  @Bean(READER)
  protected Driver readerNeo4jDriver() {
    return driverFactory.getDriver(READER, neo4jCommonConfig());
  }

  @Bean
  @Primary
  protected Driver neo4jDriver() {
    final Map<String, Driver> driverMap = new HashMap<>();
    driverMap.put(WRITER, writerNeo4jDriver());
    driverMap.put(READER, readerNeo4jDriver());

    return new Neo4jReadWriteReplicaRoutingDriver(driverMap, writerNeo4jDriver());
  }
}
