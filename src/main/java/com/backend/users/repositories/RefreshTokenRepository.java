package com.backend.users.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.users.entities.RefreshTokenEntity;
import com.backend.users.entities.UserEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  Optional<RefreshTokenEntity> findByToken(String token);

  void deleteByUser(UserEntity user);

  void deleteByExpiresAtBefore(OffsetDateTime now);
}
