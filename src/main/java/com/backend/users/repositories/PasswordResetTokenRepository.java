package com.backend.users.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.users.entities.PasswordResetTokenEntity;
import com.backend.users.entities.UserEntity;

@Repository
public interface PasswordResetTokenRepository
    extends JpaRepository<PasswordResetTokenEntity, Long> {
  Optional<PasswordResetTokenEntity> findByToken(String token);

  void deleteByUser(UserEntity user);

  void deleteByExpiresAtBefore(OffsetDateTime dateTime);
}
