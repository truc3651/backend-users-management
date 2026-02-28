package com.backend.users.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableReactiveNeo4jRepositories(basePackages = "com.backend.users.repositories")
@EnableTransactionManagement
public class Neo4jConfig {
  @Bean("neo4jTransactionManager")
  public ReactiveTransactionManager neo4jTransactionManager(
      Driver driver, ReactiveDatabaseSelectionProvider databaseSelectionProvider) {
    return new ReactiveNeo4jTransactionManager(driver, databaseSelectionProvider);
  }
}
