package com.backend.users.postgresql.settings;

import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("delegatingPostgresqlConnectionSettingsProvider")
public class PostgresqlConnectionSettingsProviderImplDelegating
    implements PostgresqlConnectionSettingsProvider {
  private final PostgresqlConnectionSettingsProviderImplLocaldev localdevProvider;
  private final PostgresqlConnectionSettingsProviderImplAWSSecret awsSecretProvider;

  @Autowired
  public PostgresqlConnectionSettingsProviderImplDelegating(
      @Qualifier("localdevPostgresqlConnectionSettingsProvider")
          PostgresqlConnectionSettingsProviderImplLocaldev localdevProvider,
      @Qualifier("awsSecretsPostgresqlConnectionSettingsProvider")
          PostgresqlConnectionSettingsProviderImplAWSSecret awsSecretProvider) {
    this.localdevProvider = localdevProvider;
    this.awsSecretProvider = awsSecretProvider;
  }

  @Override
  public PostgresqlConnectionSettings provide() {
    PostgresqlConnectionSettings localdevSettings = localdevProvider.provide();
    if (isAnyNull(localdevSettings)) {
      PostgresqlConnectionSettings awsSecretSettings = awsSecretProvider.provide();
      return merge(awsSecretSettings, awsSecretSettings);
    }

    return localdevSettings;
  }

  private boolean isAnyNull(PostgresqlConnectionSettings localdevSettings) {
    return isNull(localdevSettings.getWriterHost())
        || isNull(localdevSettings.getWriterPort())
        || isNull(localdevSettings.getWriterUsername())
        || isNull(localdevSettings.getWriterPassword())
        || isNull(localdevSettings.getDatabase())
        || isNull(localdevSettings.getSchema());
  }

  private PostgresqlConnectionSettings merge(
      PostgresqlConnectionSettings source, PostgresqlConnectionSettings target) {
    if (isNull(source.getDatabase())) {
      source.setDatabase(target.getDatabase());
    }
    if (isNull(source.getSchema())) {
      source.setSchema(target.getSchema());
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

    // Migration
    if (isNull(source.getMigrationUsername())) {
      source.setMigrationUsername(target.getMigrationUsername());
    }
    if (isNull(source.getMigrationPassword())) {
      source.setMigrationPassword(target.getMigrationPassword());
    }

    return source;
  }
}
