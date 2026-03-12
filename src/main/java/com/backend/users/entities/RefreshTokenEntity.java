package com.backend.users.entities;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("t_refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {
  @Id private String token;
  private Long userId;
  private OffsetDateTime expiresAt;

  @CreatedDate private OffsetDateTime createdAt;
}
