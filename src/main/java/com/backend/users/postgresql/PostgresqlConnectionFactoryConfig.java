package com.backend.users.postgresql;

import static com.backend.users.postgresql.PostgresqlDataSourceType.READER;
import static com.backend.users.postgresql.PostgresqlDataSourceType.WRITER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass({ConnectionFactory.class})
@EnableTransactionManagement
@EnableR2dbcAuditing
public class PostgresqlConnectionFactoryConfig {
  @Autowired protected PostgresqlConnectionFactory connectionFactory;

  @Bean(WRITER)
  protected ConnectionFactory writerConnectionFactory() {
    return connectionFactory.getConnectionFactory(WRITER);
  }

  @Bean(READER)
  protected ConnectionFactory readerConnectionFactory() {
    return connectionFactory.getConnectionFactory(READER);
  }

  @Bean
  @Primary
  protected ConnectionFactory connectionFactory() {
    return new PostgresqlReadWriteRoutingConnectionFactory(
        writerConnectionFactory(), readerConnectionFactory());
  }

  @Bean
  @Primary
  protected ReactiveTransactionManager transactionManager() {
    return new R2dbcTransactionManager(connectionFactory());
  }

  @Bean
  protected TransactionalOperator transactionalOperator(
      ReactiveTransactionManager transactionManager) {
    return TransactionalOperator.create(transactionManager);
  }
}
