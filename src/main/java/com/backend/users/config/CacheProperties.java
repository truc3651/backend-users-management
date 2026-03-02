package com.backend.users.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.caches")
public class CacheProperties {
  private CacheConfig user = new CacheConfig("user:", Duration.ofMinutes(10));
  private CacheConfig friends = new CacheConfig("friends:", Duration.ofMinutes(15));
  private CacheConfig suggestions = new CacheConfig("suggestions:", Duration.ofMinutes(30));

  @Data
  public static class CacheConfig {
    private String keyPrefix;
    private Duration ttl;
    private boolean enabled = true;

    public CacheConfig(String keyPrefix, Duration ttl) {
      this.keyPrefix = keyPrefix;
      this.ttl = ttl;
    }
  }
}
