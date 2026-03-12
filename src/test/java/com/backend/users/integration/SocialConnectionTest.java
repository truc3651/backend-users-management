package com.backend.users.integration;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Social Connection E2E Tests")
class SocialConnectionTest extends BaseTest {

  //  private UserEntity userA;
  //  private UserEntity userB;
  //  private UserEntity userC;
  //  private UserNode nodeA;
  //  private UserNode nodeB;
  //  private UserNode nodeC;
  //
  //  @BeforeEach
  //  void setUp() {
  //    userA = createUser("userA@test.com");
  //    userB = createUser("userB@test.com");
  //    userC = createUser("userC@test.com");
  //
  //    nodeA = createUserNode(userA.getId(), userA.getEmail());
  //    nodeB = createUserNode(userB.getId(), userB.getEmail());
  //    nodeC = createUserNode(userC.getId(), userC.getEmail());
  //  }
  //
  //  @Nested
  //  @DisplayName("Follow User")
  //  class FollowUserTests {
  //
  //    @Test
  //    @DisplayName("BL-HP-01: Should successfully follow user")
  //    void shouldFollowUser() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/follow/{followedId}", userB.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //
  //    @Test
  //    @DisplayName("Should return 401 for unauthenticated follow request")
  //    void shouldReturn401ForUnauthenticatedFollowRequest() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/follow/{followedId}", userB.getId())
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Unfollow User")
  //  class UnfollowUserTests {
  //
  //    @Test
  //    @DisplayName("BL-HP-02: Should successfully unfollow user")
  //    void shouldUnfollowUser() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/unfollow/{followedId}", userB.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //
  //    @Test
  //    @DisplayName("BL-EC-04: Should handle unfollow when not following gracefully")
  //    void shouldHandleUnfollowWhenNotFollowing() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/unfollow/{followedId}", userB.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Get Following/Followers")
  //  class GetFollowingFollowersTests {
  //
  //    @BeforeEach
  //    void setupFollowRelationships() {
  //      nodeA.setFollowing(new ArrayList<>());
  //      FollowsRelationship followsB = new FollowsRelationship();
  //      followsB.setFollowedUser(nodeB);
  //      followsB.setCreatedAt(OffsetDateTime.now());
  //      nodeA.getFollowing().add(followsB);
  //
  //      FollowsRelationship followsC = new FollowsRelationship();
  //      followsC.setFollowedUser(nodeC);
  //      followsC.setCreatedAt(OffsetDateTime.now());
  //      nodeA.getFollowing().add(followsC);
  //
  //      userNodeRepository.save(nodeA).block();
  //    }
  //
  //    @Test
  //    @DisplayName("BL-HP-05: Should get following list with pagination")
  //    void shouldGetFollowingList() {
  //      webTestClient
  //          .get()
  //          .uri(
  //              uriBuilder ->
  //                  uriBuilder
  //                      .path("/v1/api/social/following")
  //                      .queryParam("offset", 0)
  //                      .queryParam("pageSize", 10)
  //                      .build())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBody()
  //          .jsonPath("$.content")
  //          .isArray()
  //          .jsonPath("$.total")
  //          .isEqualTo(2);
  //    }
  //
  //    @Test
  //    @DisplayName("BL-HP-06: Should get followers list with pagination")
  //    void shouldGetFollowersList() {
  //      webTestClient
  //          .get()
  //          .uri(
  //              uriBuilder ->
  //                  uriBuilder
  //                      .path("/v1/api/social/followers")
  //                      .queryParam("offset", 0)
  //                      .queryParam("pageSize", 10)
  //                      .build())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBody()
  //          .jsonPath("$.content")
  //          .isArray();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Block User")
  //  class BlockUserTests {
  //
  //    @Test
  //    @DisplayName("BL-HP-03: Should successfully block user (via Kafka event)")
  //    void shouldBlockUser() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/block/{blockedId}", userB.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Unblock User")
  //  class UnblockUserTests {
  //
  //    @Test
  //    @DisplayName("BL-HP-04: Should successfully unblock user")
  //    void shouldUnblockUser() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/unblock/{blockedId}", userB.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //
  //    @Test
  //    @DisplayName("BL-EC-07: Should handle unblock when not blocked gracefully")
  //    void shouldHandleUnblockWhenNotBlocked() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/social/unblock/{blockedId}", userB.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Get Blocked Users")
  //  class GetBlockedUsersTests {
  //
  //    @BeforeEach
  //    void setupBlockRelationships() {
  //      nodeA.setBlocked(new ArrayList<>());
  //      BlocksRelationship blocksB = new BlocksRelationship();
  //      blocksB.setBlockedUser(nodeB);
  //      blocksB.setCreatedAt(OffsetDateTime.now());
  //      nodeA.getBlocked().add(blocksB);
  //
  //      userNodeRepository.save(nodeA).block();
  //    }
  //
  //    @Test
  //    @DisplayName("BL-HP-07: Should get blocked users list")
  //    void shouldGetBlockedUsersList() {
  //      webTestClient
  //          .get()
  //          .uri(
  //              uriBuilder ->
  //                  uriBuilder
  //                      .path("/v1/api/social/blocked")
  //                      .queryParam("offset", 0)
  //                      .queryParam("pageSize", 10)
  //                      .build())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBody()
  //          .jsonPath("$.content")
  //          .isArray();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Security Tests")
  //  class SecurityTests {
  //
  //    @Test
  //    @DisplayName("Should return 401 for unauthenticated requests")
  //    void shouldReturn401ForUnauthenticatedRequests() {
  //      webTestClient
  //          .get()
  //          .uri(
  //              uriBuilder ->
  //                  uriBuilder
  //                      .path("/v1/api/social/following")
  //                      .queryParam("offset", 0)
  //                      .queryParam("pageSize", 10)
  //                      .build())
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //
  //      webTestClient
  //          .get()
  //          .uri(
  //              uriBuilder ->
  //                  uriBuilder
  //                      .path("/v1/api/social/blocked")
  //                      .queryParam("offset", 0)
  //                      .queryParam("pageSize", 10)
  //                      .build())
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //    }
  //  }
}
