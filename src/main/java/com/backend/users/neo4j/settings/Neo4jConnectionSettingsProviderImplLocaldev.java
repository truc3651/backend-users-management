package com.backend.users.neo4j.settings;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("localdevNeo4jConnectionSettingsProvider")
@ConditionalOnBean(LocaldevNeo4jSettings.class)
@RequiredArgsConstructor
public class Neo4jConnectionSettingsProviderImplLocaldev
    implements Neo4jConnectionSettingsProvider {
  private final LocaldevNeo4jSettings localdevSettings;

  @Override
  public Neo4jConnectionSettings provide() {
    Neo4jConnectionSettings settings = new Neo4jConnectionSettings();

    settings.setWriterHost(localdevSettings.getNeo4jHost());
    settings.setWriterPort(localdevSettings.getNeo4jPort());
    settings.setWriterUsername(localdevSettings.getNeo4jUsername());
    settings.setWriterPassword(localdevSettings.getNeo4jPassword());

    settings.setReaderHost(localdevSettings.getNeo4jHost());
    settings.setReaderPort(localdevSettings.getNeo4jPort());
    settings.setReaderUsername(localdevSettings.getNeo4jUsername());
    settings.setReaderPassword(localdevSettings.getNeo4jPassword());

    settings.setDatabase(localdevSettings.getNeo4jDatabase());

    return settings;
  }
}
