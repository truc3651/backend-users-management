package com.backend.users.neo4j.settings;

import lombok.Data;

@Data
public class Neo4jConnectionSettings {
  private String writerHost;
  private String writerPort;
  private String writerUsername;
  private String writerPassword;

  private String readerHost;
  private String readerPort;
  private String readerUsername;
  private String readerPassword;

  private String database;
}
