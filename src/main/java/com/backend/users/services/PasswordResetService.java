package com.backend.users.services;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.users.entities.PasswordResetTokenEntity;
import com.backend.users.entities.UserEntity;
import com.backend.users.repositories.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
  private final PasswordResetTokenRepository passwordResetTokenRepository;

  @Value("${password.reset-token-expiration:900000}")
  private Long resetTokenExpiration;

  public Mono<PasswordResetTokenEntity> createPasswordResetToken(UserEntity user) {
    PasswordResetTokenEntity token = new PasswordResetTokenEntity();
    token.setToken(UUID.randomUUID().toString());
    token.setUserId(user.getId());
    token.setExpiresAt(OffsetDateTime.now().plusSeconds(resetTokenExpiration / 1000));
    token.setUsed(false);
    token.setCreatedAt(OffsetDateTime.now());

    return passwordResetTokenRepository.save(token);
  }

  public Mono<PasswordResetTokenEntity> validatePasswordResetToken(String token) {
    return passwordResetTokenRepository
        .findByToken(token)
        .filter(rt -> !rt.getExpiresAt().isBefore(OffsetDateTime.now()) && !rt.isUsed());
  }

  public Mono<PasswordResetTokenEntity> markTokenAsUsed(PasswordResetTokenEntity token) {
    token.setUsed(true);
    return passwordResetTokenRepository.save(token);
  }

  public Mono<Void> deleteUserPasswordResetTokens(Long userId) {
    return passwordResetTokenRepository.deleteByUserId(userId);
  }

  public Mono<Void> cleanupExpiredTokens() {
    return passwordResetTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
  }
}
