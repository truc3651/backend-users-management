package com.backend.users.neo4j;

import static com.backend.users.neo4j.Neo4jDriverType.READER;
import static com.backend.users.neo4j.Neo4jDriverType.WRITER;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.BaseSession;
import org.neo4j.driver.BookmarkManager;
import org.neo4j.driver.Driver;
import org.neo4j.driver.ExecutableQuery;
import org.neo4j.driver.Metrics;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.reactive.ReactiveSession;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadWriteReplicaRoutingNeo4jDriver implements Driver {
  private final Map<String, Driver> drivers;
  private final Driver defaultDriver;

  public ReadWriteReplicaRoutingNeo4jDriver(Map<String, Driver> drivers, Driver defaultDriver) {
    this.drivers = drivers;
    this.defaultDriver = defaultDriver;
  }

  private Driver getCurrentDriver() {
    boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
    String driverKey = isReadOnly ? READER : WRITER;
    Driver driver = drivers.get(driverKey);

    log.debug("Routing to {} driver (read-only: {})", driverKey, isReadOnly);

    return driver != null ? driver : defaultDriver;
  }

  @Override
  public boolean isEncrypted() {
    return getCurrentDriver().isEncrypted();
  }

  @Override
  public Session session() {
    return getCurrentDriver().session();
  }

  @Override
  public Session session(SessionConfig sessionConfig) {
    return getCurrentDriver().session(sessionConfig);
  }

  @Override
  public AsyncSession asyncSession() {
    return getCurrentDriver().asyncSession();
  }

  @Override
  public AsyncSession asyncSession(SessionConfig sessionConfig) {
    return getCurrentDriver().asyncSession(sessionConfig);
  }

  @Override
  public ReactiveSession reactiveSession() {
    return getCurrentDriver().reactiveSession();
  }

  @Override
  public ReactiveSession reactiveSession(SessionConfig sessionConfig) {
    return getCurrentDriver().reactiveSession(sessionConfig);
  }

  @Override
  public ExecutableQuery executableQuery(String query) {
    return getCurrentDriver().executableQuery(query);
  }

  @Override
  public BookmarkManager executableQueryBookmarkManager() {
    return getCurrentDriver().executableQueryBookmarkManager();
  }

  @Override
  public void close() {
    drivers.values().forEach(Driver::close);
  }

  @Override
  public CompletionStage<Void> closeAsync() {
    @SuppressWarnings("unchecked")
    CompletableFuture<Void>[] futures =
        drivers.values().stream()
            .map(d -> d.closeAsync().toCompletableFuture())
            .toArray(CompletableFuture[]::new);

    return CompletableFuture.allOf(futures);
  }

  @Override
  public Metrics metrics() {
    return getCurrentDriver().metrics();
  }

  @Override
  public boolean isMetricsEnabled() {
    return getCurrentDriver().isMetricsEnabled();
  }

  @Override
  public TypeSystem defaultTypeSystem() {
    return getCurrentDriver().defaultTypeSystem();
  }

  @Override
  public void verifyConnectivity() {
    drivers.values().forEach(Driver::verifyConnectivity);
  }

  @Override
  public CompletionStage<Void> verifyConnectivityAsync() {
    @SuppressWarnings("unchecked")
    CompletableFuture<Void>[] futures =
        drivers.values().stream()
            .map(d -> d.verifyConnectivityAsync().toCompletableFuture())
            .toArray(CompletableFuture[]::new);

    return CompletableFuture.allOf(futures);
  }

  @Override
  public boolean supportsMultiDb() {
    return getCurrentDriver().supportsMultiDb();
  }

  @Override
  public CompletionStage<Boolean> supportsMultiDbAsync() {
    return getCurrentDriver().supportsMultiDbAsync();
  }

  @Override
  public boolean supportsSessionAuth() {
    return getCurrentDriver().supportsSessionAuth();
  }

  @Override
  public boolean verifyAuthentication(AuthToken authToken) {
    return drivers.values().stream().allMatch(d -> d.verifyAuthentication(authToken));
  }

  @Override
  public <T extends BaseSession> T session(
      Class<T> sessionClass, SessionConfig sessionConfig, AuthToken authToken) {
    return getCurrentDriver().session(sessionClass, sessionConfig, authToken);
  }
}
