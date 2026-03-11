package com.backend.users.repositories;

import java.time.OffsetDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.backend.users.entities.RefreshTokenEntity;

import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshTokenEntity, Long> {
  Mono<RefreshTokenEntity> findByToken(String token);

  Mono<Void> deleteByToken(String token);
}
