package com.backend.users.neo4j;

import java.util.Collection;
import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Neo4jDriverType {
  public static final String READER = "readerNeo4jDriver";
  public static final String WRITER = "writerNeo4jDriver";
  public static final String MIGRATION = "migrationNeo4jDriver";

  public static final Collection<String> HEALTH_CONTRIBUTING_DRIVERS = List.of(READER, WRITER);
}
