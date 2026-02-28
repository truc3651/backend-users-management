package com.backend.users.neo4j.settings;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("localdevNeo4jConnectionSettingsProvider")
@ConditionalOnBean(LocaldevNeo4jSettings.class)
@RequiredArgsConstructor
public class Neo4jConnectionSettingsProviderImplLocaldev
    implements Neo4jConnectionSettingsProvider {
  private final LocaldevNeo4jSettings localdevNeo4jSettings;

  @Override
  public Neo4jConnectionSettings provide() {
    Neo4jConnectionSettings neo4jConnectionSettings = new Neo4jConnectionSettings();

    // Writer
    neo4jConnectionSettings.setWriterHost(localdevNeo4jSettings.getNeo4jHost());
    neo4jConnectionSettings.setWriterPort(localdevNeo4jSettings.getNeo4jPort());
    neo4jConnectionSettings.setWriterUsername(localdevNeo4jSettings.getNeo4jUsername());
    neo4jConnectionSettings.setWriterPassword(localdevNeo4jSettings.getNeo4jPassword());

    // Reader (in local dev, same as writer)
    neo4jConnectionSettings.setReaderHost(localdevNeo4jSettings.getNeo4jHost());
    neo4jConnectionSettings.setReaderPort(localdevNeo4jSettings.getNeo4jPort());
    neo4jConnectionSettings.setReaderUsername(localdevNeo4jSettings.getNeo4jUsername());
    neo4jConnectionSettings.setReaderPassword(localdevNeo4jSettings.getNeo4jPassword());

    // Migration
    neo4jConnectionSettings.setMigrationUsername(localdevNeo4jSettings.getNeo4jUsername());
    neo4jConnectionSettings.setMigrationPassword(localdevNeo4jSettings.getNeo4jPassword());

    // Common
    neo4jConnectionSettings.setDatabase(localdevNeo4jSettings.getNeo4jDatabase());

    return neo4jConnectionSettings;
  }
}
