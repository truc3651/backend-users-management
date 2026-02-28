package com.backend.users.neo4j;

// @Slf4j
// @Configuration
// @ConditionalOnClass({Driver.class})
public class DefaultNeo4jDriverConfig {
  //    @Autowired
  //    protected Neo4jDriverFactory neo4jDriverFactory;
  //
  //    @Bean
  //    protected Config neo4jCommonConfig() {
  //        return Config.builder()
  //                .withMaxConnectionPoolSize(50)
  //                // If pool is exhausted, request waits up to 60 seconds
  //                .withConnectionAcquisitionTimeout(60, TimeUnit.SECONDS)
  //                // How long to wait when opening a NEW connection to Neo4j
  //                .withConnectionTimeout(30, TimeUnit.SECONDS)
  //                // Maximum lifetime of a connection before it's CLOSED and recreated
  //                .withMaxConnectionLifetime(1, TimeUnit.HOURS)
  //                // Timeout for checking if an IDLE connection is still alive before using it
  //                .withConnectionLivenessCheckTimeout(5, TimeUnit.SECONDS)
  //                .build();
  //    }
  //
  //    @Bean(WRITER)
  //    protected Driver writerNeo4jDriver() {
  //        return neo4jDriverFactory.getDriver(WRITER, neo4jCommonConfig());
  //    }
  //
  //    @Bean(READER)
  //    protected Driver readerNeo4jDriver() {
  //        return neo4jDriverFactory.getDriver(READER, neo4jCommonConfig());
  //    }
  //
  //    @Bean(MIGRATION)
  //    protected Driver migrationNeo4jDriver() {
  //        return neo4jDriverFactory.getDriver(MIGRATION, neo4jCommonConfig());
  //    }
  //
  //    @Bean
  //    protected Driver routingNeo4jDriver() {
  //        final Map<String, Driver> driverMap = new HashMap<>();
  //        driverMap.put(WRITER, writerNeo4jDriver());
  //        driverMap.put(READER, readerNeo4jDriver());
  //
  //        return new ReadWriteReplicaRoutingNeo4jDriver(driverMap, writerNeo4jDriver());
  //    }
  //
  //    @Bean
  //    @Primary
  //    protected Driver neo4jDriver() {
  //        return routingNeo4jDriver();
  //    }
}
