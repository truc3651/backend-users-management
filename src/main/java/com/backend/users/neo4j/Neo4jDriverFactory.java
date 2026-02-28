package com.backend.users.neo4j;

import static com.backend.users.neo4j.Neo4jDriverType.MIGRATION;
import static com.backend.users.neo4j.Neo4jDriverType.READER;
import static com.backend.users.neo4j.Neo4jDriverType.WRITER;
import static java.lang.String.format;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.springframework.stereotype.Component;

import com.backend.core.exceptions.ConfigurationException;
import com.backend.users.neo4j.settings.Neo4jConnectionSettings;
import com.backend.users.neo4j.settings.Neo4jConnectionSettingsProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Neo4jDriverFactory {
  private final Neo4jConnectionSettingsProvider neo4jConnectionSettingsProvider;

  public Driver getDriver(String driverType, Config commonConfig) {
    Neo4jConnectionSettings settings = neo4jConnectionSettingsProvider.provide();
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
      Neo4jConnectionSettings neo4jConnectionSettings, Config commonConfig, String driverType) {

    Neo4jPropertiesHolder.Neo4jPropertiesHolderBuilder builder =
        Neo4jPropertiesHolder.builder()
            .driverType(driverType)
            .config(commonConfig)
            .database(neo4jConnectionSettings.getDatabase());

    return switch (driverType) {
      case READER -> builder
          .host(neo4jConnectionSettings.getReaderHost())
          .port(neo4jConnectionSettings.getReaderPort())
          .username(neo4jConnectionSettings.getReaderUsername())
          .password(neo4jConnectionSettings.getReaderPassword())
          .build();
      case WRITER -> builder
          .host(neo4jConnectionSettings.getWriterHost())
          .port(neo4jConnectionSettings.getWriterPort())
          .username(neo4jConnectionSettings.getWriterUsername())
          .password(neo4jConnectionSettings.getWriterPassword())
          .build();
      case MIGRATION -> builder
          .host(neo4jConnectionSettings.getWriterHost())
          .port(neo4jConnectionSettings.getWriterPort())
          .username(neo4jConnectionSettings.getMigrationUsername())
          .password(neo4jConnectionSettings.getMigrationPassword())
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
