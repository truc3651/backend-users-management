package com.backend.users.neo4j.settings;

import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("delegatingNeo4jConnectionSettingsProvider")
public class Neo4jConnectionSettingsProviderImplDelegating
    implements Neo4jConnectionSettingsProvider {
  private final Neo4jConnectionSettingsProviderImplLocaldev localdevProvider;
  private final Neo4jConnectionSettingsProviderImplAWSSecret awsSecretProvider;

  @Autowired
  public Neo4jConnectionSettingsProviderImplDelegating(
      @Qualifier("localdevNeo4jConnectionSettingsProvider")
          Neo4jConnectionSettingsProviderImplLocaldev localdevProvider,
      @Qualifier("awsSecretsNeo4jConnectionSettingsProvider")
          Neo4jConnectionSettingsProviderImplAWSSecret awsSecretProvider) {
    this.localdevProvider = localdevProvider;
    this.awsSecretProvider = awsSecretProvider;
  }

  @Override
  public Neo4jConnectionSettings provide() {
    Neo4jConnectionSettings localdevSettings = localdevProvider.provide();
    if (isAnyNull(localdevSettings)) {
      Neo4jConnectionSettings awsSecretSettings = awsSecretProvider.provide();
      return merge(awsSecretSettings, awsSecretSettings);
    }

    return localdevSettings;
  }

  private boolean isAnyNull(Neo4jConnectionSettings localdevSettings) {
    return isNull(localdevSettings.getWriterHost())
        || isNull(localdevSettings.getWriterPort())
        || isNull(localdevSettings.getWriterUsername())
        || isNull(localdevSettings.getWriterPassword())
        || isNull(localdevSettings.getDatabase());
  }

  private Neo4jConnectionSettings merge(
      Neo4jConnectionSettings source, Neo4jConnectionSettings target) {
    if (isNull(source.getDatabase())) {
      source.setDatabase(target.getDatabase());
    }

    // Writer
    if (isNull(source.getWriterHost())) {
      source.setWriterHost(target.getWriterHost());
    }
    if (isNull(source.getWriterPort())) {
      source.setWriterPort(target.getWriterPort());
    }
    if (isNull(source.getWriterUsername())) {
      source.setWriterUsername(target.getWriterUsername());
    }
    if (isNull(source.getWriterPassword())) {
      source.setWriterPassword(target.getWriterPassword());
    }

    // Reader
    if (isNull(source.getReaderHost())) {
      source.setReaderHost(target.getReaderHost());
    }
    if (isNull(source.getReaderPort())) {
      source.setReaderPort(target.getReaderPort());
    }
    if (isNull(source.getReaderUsername())) {
      source.setReaderUsername(target.getReaderUsername());
    }
    if (isNull(source.getReaderPassword())) {
      source.setReaderPassword(target.getReaderPassword());
    }

    return source;
  }
}
