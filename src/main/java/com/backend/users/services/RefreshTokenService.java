package com.backend.users.services;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.users.entities.RefreshTokenEntity;
import com.backend.users.entities.UserEntity;
import com.backend.users.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${jwt.refresh-token-expiration}")
  private Long refreshTokenExpiration;

  public Mono<RefreshTokenEntity> createRefreshToken(UserEntity user) {
    RefreshTokenEntity refreshToken = new RefreshTokenEntity();
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setUserId(user.getId());
    refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
    refreshToken.setCreatedAt(OffsetDateTime.now());

    return refreshTokenRepository.save(refreshToken);
  }

  public Mono<RefreshTokenEntity> validateRefreshToken(String token) {
    return refreshTokenRepository
        .findByToken(token)
        .flatMap(
            rt -> {
              if (rt.getExpiresAt().isBefore(OffsetDateTime.now())) {
                return refreshTokenRepository.delete(rt).then(Mono.empty());
              }
              return Mono.just(rt);
            });
  }

  public Mono<Void> deleteUserRefreshTokens(Long userId) {
    return refreshTokenRepository.deleteByUserId(userId);
  }

  public Mono<Void> cleanupExpiredTokens() {
    return refreshTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
  }
}
