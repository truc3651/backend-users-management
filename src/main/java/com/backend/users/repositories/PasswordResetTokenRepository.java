package com.backend.users.repositories;

import java.time.OffsetDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.users.entities.PasswordResetTokenEntity;

import reactor.core.publisher.Mono;

@Repository
public interface PasswordResetTokenRepository
    extends R2dbcRepository<PasswordResetTokenEntity, Long> {
  Mono<PasswordResetTokenEntity> findByToken(String token);

  @Modifying
  @Query("DELETE FROM t_password_reset_tokens WHERE user_id = :userId")
  Mono<Void> deleteByUserId(@Param("userId") Long userId);

  @Modifying
  @Query("DELETE FROM t_password_reset_tokens WHERE expires_at < :dateTime")
  Mono<Void> deleteByExpiresAtBefore(@Param("dateTime") OffsetDateTime dateTime);
}
