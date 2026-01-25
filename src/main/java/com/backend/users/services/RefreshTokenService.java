package com.backend.users.services;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.users.entities.RefreshTokenEntity;
import com.backend.users.entities.UserEntity;
import com.backend.users.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${jwt.refresh-token-expiration}")
  private Long refreshTokenExpiration;

  public RefreshTokenEntity createRefreshToken(UserEntity user) {
    RefreshTokenEntity refreshToken = new RefreshTokenEntity();
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setUser(user);
    refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenExpiration / 1000));

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshTokenEntity> validateRefreshToken(String token) {
    Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByToken(token);

    if (refreshToken.isPresent()) {
      RefreshTokenEntity rt = refreshToken.get();
      if (rt.getExpiresAt().isBefore(OffsetDateTime.now())) {
        refreshTokenRepository.delete(rt);
        return Optional.empty();
      }
    }

    return refreshToken;
  }

  @Transactional
  public void deleteUserRefreshTokens(UserEntity user) {
    refreshTokenRepository.deleteByUser(user);
  }

  @Transactional
  public void cleanupExpiredTokens() {
    refreshTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
  }
}
