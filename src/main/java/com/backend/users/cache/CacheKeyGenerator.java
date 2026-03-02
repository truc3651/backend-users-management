package com.backend.users.cache;

public final class CacheKeyGenerator {

  private CacheKeyGenerator() {}

  public static String forId(Long id) {
    return String.valueOf(id);
  }
}
