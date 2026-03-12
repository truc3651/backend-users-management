package com.backend.users.integration;

import org.junit.jupiter.api.DisplayName;

@DisplayName("User Controller E2E Tests")
class UserControllerTest extends BaseTest {
  //
  //  private UserEntity testUser;
  //
  //  @BeforeEach
  //  void setUp() {
  //    testUser = createUser("testuser@test.com", "originalPassword");
  //  }
  //
  //  @Nested
  //  @DisplayName("Change Password")
  //  class ChangePasswordTests {
  //
  //    @Test
  //    @DisplayName("Should successfully change password")
  //    void shouldChangePassword() {
  //      ChangePasswordRequestDto request = new ChangePasswordRequestDto();
  //      request.setNewPassword("newSecurePassword123");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .header("Authorization", "Bearer " + generateToken(testUser))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      UserEntity updatedUser = userRepository.findById(testUser.getId()).block();
  //      assertThat(passwordEncoder.matches("newSecurePassword123", updatedUser.getPassword()))
  //          .isTrue();
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject change password without authentication")
  //    void shouldRejectChangePasswordWithoutAuth() {
  //      ChangePasswordRequestDto request = new ChangePasswordRequestDto();
  //      request.setNewPassword("newPassword");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject change password with blank new password")
  //    void shouldRejectChangePasswordWithBlankNewPassword() {
  //      ChangePasswordRequestDto request = new ChangePasswordRequestDto();
  //      request.setNewPassword("");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .header("Authorization", "Bearer " + generateToken(testUser))
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject change password with invalid token")
  //    void shouldRejectChangePasswordWithInvalidToken() {
  //      ChangePasswordRequestDto request = new ChangePasswordRequestDto();
  //      request.setNewPassword("newPassword");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .header("Authorization", "Bearer invalid-token")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Refresh Token")
  //  class RefreshTokenTests {
  //
  //    @Test
  //    @DisplayName("Should successfully refresh access token")
  //    void shouldRefreshAccessToken() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //
  //      RefreshTokenRequestDto request = new RefreshTokenRequestDto();
  //      request.setRefreshToken(refreshToken.getToken());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk()
  //          .expectBody(RefreshTokenResponseDto.class)
  //          .consumeWith(
  //              response -> {
  //                RefreshTokenResponseDto body = response.getResponseBody();
  //                assertThat(body).isNotNull();
  //                assertThat(body.getAccessToken()).isNotBlank();
  //              });
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject refresh with expired token")
  //    void shouldRejectRefreshWithExpiredToken() {
  //      RefreshTokenEntity expiredToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().minusHours(1));
  //
  //      RefreshTokenRequestDto request = new RefreshTokenRequestDto();
  //      request.setRefreshToken(expiredToken.getToken());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject refresh with invalid token")
  //    void shouldRejectRefreshWithInvalidToken() {
  //      RefreshTokenRequestDto request = new RefreshTokenRequestDto();
  //      request.setRefreshToken("invalid-refresh-token");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject refresh with blank token")
  //    void shouldRejectRefreshWithBlankToken() {
  //      RefreshTokenRequestDto request = new RefreshTokenRequestDto();
  //      request.setRefreshToken("");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("New access token should be valid")
  //    void newAccessTokenShouldBeValid() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //
  //      RefreshTokenRequestDto request = new RefreshTokenRequestDto();
  //      request.setRefreshToken(refreshToken.getToken());
  //
  //      String newAccessToken =
  //          webTestClient
  //              .post()
  //              .uri("/v1/api/user/refresh")
  //              .contentType(MediaType.APPLICATION_JSON)
  //              .bodyValue(request)
  //              .exchange()
  //              .expectStatus()
  //              .isOk()
  //              .expectBody(RefreshTokenResponseDto.class)
  //              .returnResult()
  //              .getResponseBody()
  //              .getAccessToken();
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .header("Authorization", "Bearer " + newAccessToken)
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(new ChangePasswordRequestDto("anotherPassword"))
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Logout")
  //  class LogoutTests {
  //
  //    @Test
  //    @DisplayName("Should successfully logout and invalidate refresh token")
  //    void shouldLogoutAndInvalidateRefreshToken() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //
  //      LogoutRequestDto request = new LogoutRequestDto(refreshToken.getToken());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/logout")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      RefreshTokenEntity deletedToken =
  //          refreshTokenRepository.findByToken(refreshToken.getToken()).block();
  //      assertThat(deletedToken).isNull();
  //    }
  //
  //    @Test
  //    @DisplayName("Should handle logout with non-existent token gracefully")
  //    void shouldHandleLogoutWithNonExistentToken() {
  //      LogoutRequestDto request = new LogoutRequestDto("non-existent-token");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/logout")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject logout with blank token")
  //    void shouldRejectLogoutWithBlankToken() {
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/logout")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue("{\"refreshToken\": \"\"}")
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //
  //    @Test
  //    @DisplayName("Refresh token should not work after logout")
  //    void refreshTokenShouldNotWorkAfterLogout() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //      String tokenValue = refreshToken.getToken();
  //
  //      LogoutRequestDto logoutRequest = new LogoutRequestDto(tokenValue);
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/logout")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(logoutRequest)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
  //      refreshRequest.setRefreshToken(tokenValue);
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(refreshRequest)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Security Tests")
  //  class SecurityTests {
  //
  //    @Test
  //    @DisplayName("Change password requires authentication")
  //    void changePasswordRequiresAuthentication() {
  //      ChangePasswordRequestDto request = new ChangePasswordRequestDto();
  //      request.setNewPassword("newPassword");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isUnauthorized();
  //    }
  //
  //    @Test
  //    @DisplayName("Refresh endpoint does not require authentication")
  //    void refreshEndpointDoesNotRequireAuthentication() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //
  //      RefreshTokenRequestDto request = new RefreshTokenRequestDto();
  //      request.setRefreshToken(refreshToken.getToken());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //
  //    @Test
  //    @DisplayName("Logout endpoint does not require authentication")
  //    void logoutEndpointDoesNotRequireAuthentication() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //
  //      LogoutRequestDto request = new LogoutRequestDto(refreshToken.getToken());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/logout")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(request)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Token Lifecycle Tests")
  //  class TokenLifecycleTests {
  //
  //    @Test
  //    @DisplayName("Full login-refresh-logout cycle should work")
  //    void fullLoginRefreshLogoutCycleShouldWork() {
  //      RefreshTokenEntity refreshToken =
  //          createRefreshToken(testUser, OffsetDateTime.now().plusDays(7));
  //
  //      RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
  //      refreshRequest.setRefreshToken(refreshToken.getToken());
  //
  //      String newAccessToken =
  //          webTestClient
  //              .post()
  //              .uri("/v1/api/user/refresh")
  //              .contentType(MediaType.APPLICATION_JSON)
  //              .bodyValue(refreshRequest)
  //              .exchange()
  //              .expectStatus()
  //              .isOk()
  //              .expectBody(RefreshTokenResponseDto.class)
  //              .returnResult()
  //              .getResponseBody()
  //              .getAccessToken();
  //
  //      assertThat(newAccessToken).isNotBlank();
  //
  //      ChangePasswordRequestDto changeRequest = new ChangePasswordRequestDto();
  //      changeRequest.setNewPassword("cycleTestPassword");
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/change-password")
  //          .header("Authorization", "Bearer " + newAccessToken)
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(changeRequest)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      LogoutRequestDto logoutRequest = new LogoutRequestDto(refreshToken.getToken());
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/logout")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(logoutRequest)
  //          .exchange()
  //          .expectStatus()
  //          .isOk();
  //
  //      webTestClient
  //          .post()
  //          .uri("/v1/api/user/refresh")
  //          .contentType(MediaType.APPLICATION_JSON)
  //          .bodyValue(refreshRequest)
  //          .exchange()
  //          .expectStatus()
  //          .isBadRequest();
  //    }
  //  }
}
