package com.backend.users.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

public class DockerCheckTest {
  @BeforeAll
  static void setup() {
    System.setProperty("docker.host", "unix:///var/run/docker.sock");
  }

  @Test
  void dockerWorks() {
    try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")) {
      postgres.start();
      System.out.println(postgres.getJdbcUrl());
    }
  }
}
