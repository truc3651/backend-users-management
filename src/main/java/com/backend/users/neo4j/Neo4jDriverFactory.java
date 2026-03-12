package com.backend.users.neo4j;

import static com.backend.users.neo4j.Neo4jDriverType.READER;
import static com.backend.users.neo4j.Neo4jDriverType.WRITER;
import static java.lang.String.format;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.backend.core.exceptions.ConfigurationException;
import com.backend.users.neo4j.settings.Neo4jConnectionSettings;
import com.backend.users.neo4j.settings.Neo4jConnectionSettingsProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Neo4jDriverFactory {
  private final Neo4jConnectionSettingsProvider connectionProvider;

  public Neo4jDriverFactory(
      @Qualifier("delegatingNeo4jConnectionSettingsProvider")
          Neo4jConnectionSettingsProvider connectionProvider) {
    this.connectionProvider = connectionProvider;
  }

  public Driver getDriver(String driverType, Config commonConfig) {
    Neo4jConnectionSettings settings = connectionProvider.provide();
    Neo4jPropertiesHolder driverPropertiesHolder =
        initializePropertiesHolder(settings, commonConfig, driverType);

    if (!isValidDriverPropertiesHolder(driverPropertiesHolder)) {
      throw new ConfigurationException(
          format("Failed to initialize required Neo4j Driver [%s]", driverType));
    }

    log.info("Neo4j Driver [{}] URI: {}", driverType, driverPropertiesHolder.getUri());
    log.info("Neo4j Driver [{}] Username: {}", driverType, driverPropertiesHolder.getUsername());
    log.info("Neo4j Driver [{}] Database: {}", driverType, driverPropertiesHolder.getDatabase());

    return driverPropertiesHolder.toDriver();
  }

  private Neo4jPropertiesHolder initializePropertiesHolder(
      Neo4jConnectionSettings settings, Config commonConfig, String driverType) {

    Neo4jPropertiesHolder.Neo4jPropertiesHolderBuilder builder =
        Neo4jPropertiesHolder.builder()
            .driverType(driverType)
            .config(commonConfig)
            .database(settings.getDatabase());

    return switch (driverType) {
      case READER -> builder
          .host(settings.getReaderHost())
          .port(settings.getReaderPort())
          .username(settings.getReaderUsername())
          .password(settings.getReaderPassword())
          .build();
      case WRITER -> builder
          .host(settings.getWriterHost())
          .port(settings.getWriterPort())
          .username(settings.getWriterUsername())
          .password(settings.getWriterPassword())
          .build();
      default -> throw new ConfigurationException(
          format("Unsupported Neo4j Driver type supplied [%s]", driverType));
    };
  }

  private boolean isValidDriverPropertiesHolder(Neo4jPropertiesHolder driverPropertiesHolder) {
    String driverType = driverPropertiesHolder.getDriverType();

    if (StringUtils.isBlank(driverPropertiesHolder.getHost())) {
      log.error("Neo4j Driver[{}] connection[host] is not defined", driverType);
      return false;
    }
    if (StringUtils.isBlank(driverPropertiesHolder.getPort())) {
      log.error("Neo4j Driver[{}] connection[port] is not defined", driverType);
      return false;
    }
    if (StringUtils.isBlank(driverPropertiesHolder.getUsername())) {
      log.error("Neo4j Driver[{}] connection[user] is not defined", driverType);
      return false;
    }
    if (StringUtils.isBlank(driverPropertiesHolder.getPassword())) {
      log.error("Neo4j Driver[{}] connection[password] is not defined", driverType);
      return false;
    }
    if (StringUtils.isBlank(driverPropertiesHolder.getDatabase())) {
      log.error("Neo4j Driver[{}] connection[database] is not defined", driverType);
      return false;
    }
    return true;
  }
}
