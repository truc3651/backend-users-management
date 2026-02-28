package com.backend.users.neo4j.settings;

import lombok.Data;

@Data
public class Neo4jConnectionSettings {
  // Writer
  private String writerHost;
  private String writerPort;
  private String writerUsername;
  private String writerPassword;

  // Reader
  private String readerHost;
  private String readerPort;
  private String readerUsername;
  private String readerPassword;

  // Migration
  private String migrationUsername;
  private String migrationPassword;

  // Common
  private String database;
}
