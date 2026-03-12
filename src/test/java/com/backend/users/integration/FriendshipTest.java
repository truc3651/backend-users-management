package com.backend.users.integration;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Friendship E2E Tests")
class FriendshipTest extends BaseTest {

  //  private UserEntity userA;
  //  private UserEntity userB;
  //  private UserEntity userC;
  //
  //  @BeforeEach
  //  void setUp() {
  //    userA = createUser("userA@test.com");
  //    userB = createUser("userB@test.com");
  //    userC = createUser("userC@test.com");
  //
  //    createUserNode(userA.getId(), userA.getEmail());
  //    createUserNode(userB.getId(), userB.getEmail());
  //    createUserNode(userC.getId(), userC.getEmail());
  //  }
  //
  //  @Nested
  //  @DisplayName("Send Friend Request")
  //  class SendFriendRequestTests {
  //
  //    @Test
  //    @DisplayName("FR-HP-01: Should successfully send friend request")
  //    void shouldSendFriendRequest() {
  //      SendFriendRequestDto request = new SendFriendRequestDto();
  //      request.setAddresseeId(userB.getId());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests")
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      FriendRequestEntity savedRequest =
  //          friendRequestRepository
  //              .findByIdAndStatus(userA.getId(), userB.getId(),
  // FriendRequestStatus.PENDING.name())
  //              .block();
  //
  //      assertThat(savedRequest).isNotNull();
  //      assertThat(savedRequest.getStatus()).isEqualTo(FriendRequestStatus.PENDING);
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-01: Should reject sending request to self")
  //    void shouldRejectSendingRequestToSelf() {
  //      SendFriendRequestDto request = new SendFriendRequestDto();
  //      request.setAddresseeId(userA.getId());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests")
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-02: Should reject request when already friends")
  //    void shouldRejectWhenAlreadyFriends() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.ACCEPTED);
  //
  //      SendFriendRequestDto request = new SendFriendRequestDto();
  //      request.setAddresseeId(userB.getId());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests")
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-03: Should reject duplicate pending request")
  //    void shouldRejectDuplicatePendingRequest() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      SendFriendRequestDto request = new SendFriendRequestDto();
  //      request.setAddresseeId(userB.getId());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests")
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-04: Should return 404 for non-existent addressee")
  //    void shouldReturn404ForNonExistentAddressee() {
  //      SendFriendRequestDto request = new SendFriendRequestDto();
  //      request.setAddresseeId(99999L);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests")
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isNotFound();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-13: Should reject bidirectional pending request")
  //    void shouldRejectBidirectionalPendingRequest() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      SendFriendRequestDto request = new SendFriendRequestDto();
  //      request.setAddresseeId(userA.getId());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests")
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Accept Friend Request")
  //  class AcceptFriendRequestTests {
  //
  //    @Test
  //    @DisplayName("FR-HP-02: Should successfully accept friend request")
  //    void shouldAcceptFriendRequest() {
  //      FriendRequestEntity pendingRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/accept", pendingRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      FriendRequestEntity updatedRequest =
  //          friendRequestRepository.findById(pendingRequest.getId()).block();
  //
  //      assertThat(updatedRequest.getStatus()).isEqualTo(FriendRequestStatus.ACCEPTED);
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-05: Should return 404 for non-existent request")
  //    void shouldReturn404ForNonExistentRequest() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/accept", 99999L)
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isNotFound();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-06: Should reject accepting already accepted request")
  //    void shouldRejectAcceptingAlreadyAcceptedRequest() {
  //      FriendRequestEntity acceptedRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.ACCEPTED);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/accept", acceptedRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-07: Should reject accepting rejected request")
  //    void shouldRejectAcceptingRejectedRequest() {
  //      FriendRequestEntity rejectedRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.REJECTED);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/accept", rejectedRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-08: Should reject accepting cancelled request")
  //    void shouldRejectAcceptingCancelledRequest() {
  //      FriendRequestEntity cancelledRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.CANCELLED);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/accept", cancelledRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-09: Should forbid accepting as wrong user")
  //    void shouldForbidAcceptingAsWrongUser() {
  //      FriendRequestEntity pendingRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/accept", pendingRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userC))
  //          .exchange()
  //          .expectStatus()
  //          .isForbidden();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Reject Friend Request")
  //  class RejectFriendRequestTests {
  //
  //    @Test
  //    @DisplayName("FR-HP-03: Should successfully reject friend request")
  //    void shouldRejectFriendRequest() {
  //      FriendRequestEntity pendingRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/reject", pendingRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      FriendRequestEntity updatedRequest =
  //          friendRequestRepository.findById(pendingRequest.getId()).block();
  //
  //      assertThat(updatedRequest.getStatus()).isEqualTo(FriendRequestStatus.REJECTED);
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-10: Should forbid requester from rejecting")
  //    void shouldForbidRequesterFromRejecting() {
  //      FriendRequestEntity pendingRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/reject", pendingRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isForbidden();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Cancel Friend Request")
  //  class CancelFriendRequestTests {
  //
  //    @Test
  //    @DisplayName("FR-HP-04: Should successfully cancel friend request")
  //    void shouldCancelFriendRequest() {
  //      FriendRequestEntity pendingRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/cancel", pendingRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      FriendRequestEntity updatedRequest =
  //          friendRequestRepository.findById(pendingRequest.getId()).block();
  //
  //      assertThat(updatedRequest.getStatus()).isEqualTo(FriendRequestStatus.CANCELLED);
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-11: Should forbid addressee from cancelling")
  //    void shouldForbidAddresseeFromCancelling() {
  //      FriendRequestEntity pendingRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/cancel", pendingRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isForbidden();
  //    }
  //
  //    @Test
  //    @DisplayName("FR-EC-12: Should reject cancelling non-pending request")
  //    void shouldRejectCancellingNonPendingRequest() {
  //      FriendRequestEntity acceptedRequest =
  //          createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.ACCEPTED);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/friendships/requests/{requestId}/cancel", acceptedRequest.getId())
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Get Friend Requests")
  //  class GetFriendRequestsTests {
  //
  //    @Test
  //    @DisplayName("FR-HP-05: Should get pending friend requests")
  //    void shouldGetPendingFriendRequests() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //      createFriendRequest(userC.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .get()
  //          .uri("/v1/api/friendships/requests/pending")
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBodyList(FriendRequestResponseDto.class)
  //          .hasSize(2);
  //    }
  //
  //    @Test
  //    @DisplayName("FR-HP-06: Should get sent friend requests")
  //    void shouldGetSentFriendRequests() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //      createFriendRequest(userA.getId(), userC.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .get()
  //          .uri("/v1/api/friendships/requests/sent")
  //          .header("Authorization", "Bearer " + generateToken(userA))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBodyList(FriendRequestResponseDto.class)
  //          .hasSize(2);
  //    }
  //
  //    @Test
  //    @DisplayName("Should only return pending requests, not accepted/rejected")
  //    void shouldOnlyReturnPendingRequests() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //      createFriendRequest(userC.getId(), userB.getId(), FriendRequestStatus.ACCEPTED);
  //
  //      webTestClient
  //          .get()
  //          .uri("/v1/api/friendships/requests/pending")
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBodyList(FriendRequestResponseDto.class)
  //          .hasSize(1);
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Security Tests")
  //  class SecurityTests {
  //
  //    @Test
  //    @DisplayName("SEC-01: Should return 401 for unauthenticated request")
  //    void shouldReturn401ForUnauthenticatedRequest() {
  //      webTestClient
  //          .get()
  //          .uri("/v1/api/friendships/requests/pending")
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //    }
  //
  //    @Test
  //    @DisplayName("SEC-02: Should only return own pending requests")
  //    void shouldOnlyReturnOwnPendingRequests() {
  //      createFriendRequest(userA.getId(), userB.getId(), FriendRequestStatus.PENDING);
  //      createFriendRequest(userA.getId(), userC.getId(), FriendRequestStatus.PENDING);
  //
  //      webTestClient
  //          .get()
  //          .uri("/v1/api/friendships/requests/pending")
  //          .header("Authorization", "Bearer " + generateToken(userB))
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBodyList(FriendRequestResponseDto.class)
  //          .hasSize(1)
  //          .consumeWith(
  //              response -> {
  //                FriendRequestResponseDto dto = response.getResponseBody().get(0);
  //                assertThat(dto.getAddresseeId()).isEqualTo(userB.getId());
  //              });
  //    }
  //  }
}
