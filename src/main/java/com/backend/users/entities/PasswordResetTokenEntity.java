package com.backend.users.entities;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("t_password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenEntity {
  @Id private Long id;

  private String token;

  @Column("expires_at")
  private OffsetDateTime expiresAt;

  private boolean used;

  @Column("user_id")
  private Long userId;

  @CreatedDate
  @Column("created_at")
  private OffsetDateTime createdAt;
}
