# Cache-Aside Pattern Implementation Plan

## Executive Summary

Implement comprehensive caching using the **Cache-Aside pattern** (also known as Lazy-Load) across the backend-users-management project, leveraging the existing `ReactiveCacheTemplate` from backend-core library.

---

## Current State Analysis

### What's Already Implemented
- `UserCacheConfig` creates a `ReactiveCacheTemplate<UserDto>` bean
- `UserService.getUserById()` uses cache-aside pattern via `cache.get(id, this::loadUserFromDb)`
- Single TTL configuration: `app.cache.ttl: 10m` with key-prefix: `user:`

### Gaps Identified
1. **No caching** for social graph queries (friends, followers, following, blocked users)
2. **No caching** for friend requests (pending/sent)
3. **No caching** for friend suggestions (expensive Neo4j 2-hop traversal)
4. **Missing cache invalidation** on write operations across services
5. **Single cache configuration** - all entities share the same TTL/prefix

---

## Architecture Design

### Cache Tier Structure (Aligned with System Design Book)

Based on the newsfeed.md reference architecture (Figure 11-8), implement a **multi-tier cache**:

| Cache Tier | Entity | Key Pattern | TTL | Rationale |
|------------|--------|-------------|-----|-----------|
| **User Cache** | UserDto | `user:{userId}` | 10m | User profile data (existing) |
| **Social Graph Cache** | Friends list | `friends:{userId}` | 15m | Friend relationships |
| **Action Cache** | Friend suggestions | `suggestions:{userId}` | 30m | Expensive computation, changes slowly |

### Cache-Aside Pattern Implementation

```
READ FLOW:
┌─────────┐    1. get(id)    ┌──────────┐
│ Service │ ───────────────► │  Cache   │
│         │ ◄─────────────── │ (Redis)  │
│         │   2a. HIT        └──────────┘
│         │                        │
│         │   2b. MISS             │
│         │ ◄──────────────────────┘
│         │    3. loadFromDb()
│         │ ───────────────► ┌──────────┐
│         │ ◄─────────────── │ Database │
│         │    4. data       └──────────┘
│         │    5. put(id, data)
│         │ ───────────────► ┌──────────┐
└─────────┘                  │  Cache   │
                             └──────────┘

WRITE FLOW (Invalidation):
┌─────────┐   1. update()   ┌──────────┐
│ Service │ ───────────────►│ Database │
│         │ ◄───────────────│          │
│         │   2. success    └──────────┘
│         │   3. evict(id)
│         │ ───────────────► ┌──────────┐
└─────────┘                  │  Cache   │
                             │ (delete) │
                             └──────────┘
```

---

## Implementation Plan

### Phase 1: Multi-Cache Configuration Infrastructure

**File: `src/main/java/com/backend/users/config/CacheProperties.java`** (NEW)

Create a dedicated properties class for multiple cache configurations:

```java
@ConfigurationProperties(prefix = "app.caches")
public class CacheProperties {
    private CacheConfig user = new CacheConfig("user:", Duration.ofMinutes(10));
    private CacheConfig friends = new CacheConfig("friends:", Duration.ofMinutes(15));
    private CacheConfig suggestions = new CacheConfig("suggestions:", Duration.ofMinutes(30));
    // getters, setters, inner CacheConfig class
}
```

**File: `src/main/resources/application.yaml`** (MODIFY)

Add cache configuration:

```yaml
app:
  caches:
    user:
      key-prefix: "user:"
      ttl: 10m
    friends:
      key-prefix: "friends:"
      ttl: 15m
    suggestions:
      key-prefix: "suggestions:"
      ttl: 30m
```

---

### Phase 2: Cache Bean Configuration

**File: `src/main/java/com/backend/users/config/UserCacheConfig.java`** (MODIFY → RENAME to `CacheConfig.java`)

Expand to create multiple cache template beans:

```java
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    @Bean
    public ReactiveCacheTemplate<UserDto> userCache(...);

    @Bean
    public ReactiveCacheTemplate<List<UserDto>> friendsCache(...);

    @Bean
    public ReactiveCacheTemplate<List<UserDto>> suggestionsCache(...);
}
```

---

### Phase 3: Service Layer Caching Implementation

#### 3.1 FriendshipService Caching

**File: `src/main/java/com/backend/users/services/FriendshipService.java`** (MODIFY)

| Method | Cache Operation |
|--------|-----------------|
| `getFriends(userId)` | Cache-aside: `friendsCache.get(userId, this::loadFriendsFromNeo4j)` |
| `getFriendSuggestions(userId)` | Cache-aside: `suggestionsCache.get(userId, this::loadSuggestionsFromNeo4j)` |
| `getPendingFriendRequests(userId)` | Cache-aside: `pendingRequestsCache.get(userId, this::loadPendingFromDb)` |
| `getSentFriendRequests(userId)` | Cache-aside with key `sent:{userId}` |
| `sendFriendRequest()` | **Evict**: addressee's pending requests cache |
| `acceptFriendRequest()` | **Evict**: both users' friends cache, suggestions cache |
| `rejectFriendRequest()` | **Evict**: addressee's pending requests cache |
| `cancelFriendRequest()` | **Evict**: requester's sent requests cache, addressee's pending cache |

#### 3.2 SocialConnectionService Caching

**File: `src/main/java/com/backend/users/services/SocialConnectionService.java`** (MODIFY)

| Method | Cache Operation |
|--------|-----------------|
| `getFollowing(userId, page)` | Cache-aside: `followingCache.get(userId:page, this::loadFollowingFromNeo4j)` |
| `getFollowers(userId, page)` | Cache-aside: `followersCache.get(userId:page, this::loadFollowersFromNeo4j)` |
| `getBlockedUsers(userId, page)` | Cache-aside: `blockedCache.get(userId:page, this::loadBlockedFromNeo4j)` |
| `follow()` | **Evict**: follower's following cache, followee's follower cache, counts |
| `unfollow()` | **Evict**: follower's following cache, followee's follower cache, counts |
| `block()` | **Evict**: blocker's blocked cache |
| `unblock()` | **Evict**: blocker's blocked cache |

#### 3.3 AuthService Cache Invalidation

**File: `src/main/java/com/backend/users/services/AuthService.java`** (MODIFY)

| Method | Cache Operation |
|--------|-----------------|
| `changePassword()` | **Evict**: user cache (profile may change) |
| `register()` | No cache operation needed (new user) |

---

### Phase 4: Cache Key Strategy

Create a utility class for consistent key generation:

**File: `src/main/java/com/backend/users/cache/CacheKeyGenerator.java`** (NEW)

```java
public final class CacheKeyGenerator {
    public static String userKey(Long userId) {
        return String.valueOf(userId);
    }

    public static String paginatedKey(Long userId, int page) {
        return userId + ":" + page;
    }

    public static String countKey(String type, Long userId) {
        return type + ":" + userId;
    }
}
```

---

### Phase 5: Paginated Cache Strategy

For paginated endpoints (followers, following, blocked), implement page-aware caching:

**Option A: Cache per page** (Recommended)
- Key: `followers:{userId}:{page}`
- Pros: Simple, works with existing `ReactiveCacheTemplate`
- Cons: First page change invalidates all pages

**Option B: Cache IDs only, hydrate separately**
- Cache: List of user IDs per user
- Hydrate: Use `userCache` for full objects
- Pros: Better cache hit rate
- Cons: More complexity, N+1 cache lookups

**Decision**: Use Option A for simplicity. On write operations, invalidate all pages using pattern-based deletion.

---

## Files to Modify/Create

| File | Action | Description |
|------|--------|-------------|
| `config/CacheProperties.java` | CREATE | Multi-cache configuration |
| `config/UserCacheConfig.java` | RENAME → `CacheConfig.java` | Add all cache beans |
| `cache/CacheKeyGenerator.java` | CREATE | Consistent key generation |
| `services/FriendshipService.java` | MODIFY | Add caching + invalidation |
| `services/SocialConnectionService.java` | MODIFY | Add caching + invalidation |
| `services/AuthService.java` | MODIFY | Add cache invalidation |
| `application.yaml` | MODIFY | Add cache configurations |
| `application-localdev.yml` | MODIFY | Ensure cache config present |

---

## Verification Plan

### 1. Unit Tests
- Mock `ReactiveCacheTemplate` in service tests
- Verify cache.get() called before DB
- Verify cache.evict() called on write operations

### 2. Integration Tests
- Start Redis container (Testcontainers)
- Verify cache population on first read
- Verify cache hit on second read
- Verify cache invalidation on writes

### 3. Manual Testing Checklist
```bash
# 1. Start local Redis
docker run -d -p 6379:6379 redis:7-alpine

# 2. Run application
./gradlew bootRun --args='--spring.profiles.active=localdev'

# 3. Test user caching
curl -X GET http://localhost:8090/v1/api/user/me  # First call: cache miss
curl -X GET http://localhost:8090/v1/api/user/me  # Second call: cache hit

# 4. Verify in Redis CLI
redis-cli
> KEYS user:*
> TTL user:1

# 5. Test cache invalidation
curl -X POST http://localhost:8090/v1/api/user/change-password
redis-cli KEYS user:*  # Should be empty (evicted)
```

---

## Trade-offs & Decisions

| Decision | Rationale |
|----------|-----------|
| **Delete-only eviction** | Prevents write-ordering issues in distributed systems (per backend-core design) |
| **TTL-based expiry** | Self-healing for stale data without complex invalidation |
| **Page-based pagination cache** | Simpler than ID-list + hydration approach |
| **5-30 min TTLs** | Balanced between freshness and cache hit rate |
| **No distributed locks** | Acceptable stale reads; DB is source of truth |
| **Graceful degradation** | Cache failures fall back to DB (already in ReactiveCacheTemplate) |

---

## Risk Mitigation

1. **Cache stampede**: ReactiveCacheTemplate's `get()` with fallback handles this via Mono.defer()
2. **Memory pressure**: TTL-based eviction prevents unbounded growth
3. **Inconsistency**: Delete-only eviction + TTL ensures eventual consistency
4. **Performance regression**: Fallback to DB on cache failure maintains availability

---

## Out of Scope

- Distributed cache invalidation via Kafka (can be added later)
- Cache warming strategies
- Cache metrics/monitoring (observability layer)
- Rate limiting integration with cache
