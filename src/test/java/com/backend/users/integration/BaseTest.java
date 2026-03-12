package com.backend.users.integration;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.backend.users.entities.FriendRequestEntity;
import com.backend.users.entities.PasswordResetTokenEntity;
import com.backend.users.entities.RefreshTokenEntity;
import com.backend.users.entities.UserEntity;
import com.backend.users.enums.FriendRequestStatus;
import com.backend.users.graph.UserNode;
import com.backend.users.repositories.FriendRequestRepository;
import com.backend.users.repositories.PasswordResetTokenRepository;
import com.backend.users.repositories.RefreshTokenRepository;
import com.backend.users.repositories.UserNodeRepository;
import com.backend.users.repositories.UserRepository;
import com.backend.users.utils.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
public abstract class BaseTest {
  protected static final String DEFAULT_PASSWORD = "password123";

  @Autowired protected WebTestClient webTestClient;
  @Autowired protected UserRepository userRepository;
  @Autowired protected FriendRequestRepository friendRequestRepository;
  @Autowired protected UserNodeRepository userNodeRepository;
  @Autowired protected RefreshTokenRepository refreshTokenRepository;
  @Autowired protected PasswordResetTokenRepository passwordResetTokenRepository;
  @Autowired protected ReactiveRedisTemplate<String, String> redisTemplate;
  @Autowired protected PasswordEncoder passwordEncoder;
  @Autowired protected JwtUtil jwtUtil;

  @BeforeEach
  void cleanUp() {
    refreshTokenRepository.deleteAll().block();
    passwordResetTokenRepository.deleteAll().block();
    friendRequestRepository.deleteAll().block();
    userRepository.deleteAll().block();
    userNodeRepository.deleteAll().block();
    redisTemplate
        .getConnectionFactory()
        .getReactiveConnection()
        .serverCommands()
        .flushAll()
        .block();
  }

  protected UserEntity createUser(String email) {
    return createUser(email, DEFAULT_PASSWORD);
  }

  protected UserEntity createUser(String email, String password) {
    UserEntity user = new UserEntity();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    return userRepository.save(user).block();
  }

  protected RefreshTokenEntity createRefreshToken(UserEntity user, OffsetDateTime expiresAt) {
    RefreshTokenEntity token = new RefreshTokenEntity();
    token.setToken(java.util.UUID.randomUUID().toString());
    token.setUserId(user.getId());
    token.setExpiresAt(expiresAt);
    return refreshTokenRepository.save(token).block();
  }

  protected PasswordResetTokenEntity createPasswordResetToken(
      UserEntity user, OffsetDateTime expiresAt) {
    PasswordResetTokenEntity token = new PasswordResetTokenEntity();
    token.setToken(java.util.UUID.randomUUID().toString());
    token.setUserId(user.getId());
    token.setExpiresAt(expiresAt);
    return passwordResetTokenRepository.save(token).block();
  }

  protected UserNode createUserNode(Long userId, String email) {
    UserNode node = new UserNode();
    node.setId(userId);
    node.setEmail(email);
    return userNodeRepository.save(node).block();
  }

  protected FriendRequestEntity createFriendRequest(
      Long requesterId, Long addresseeId, FriendRequestStatus status) {
    FriendRequestEntity request = new FriendRequestEntity();
    request.setRequesterId(requesterId);
    request.setAddresseeId(addresseeId);
    request.setStatus(status);
    return friendRequestRepository.save(request).block();
  }

  protected String generateToken(UserEntity user) {
    return jwtUtil.generateToken(user);
  }
}
