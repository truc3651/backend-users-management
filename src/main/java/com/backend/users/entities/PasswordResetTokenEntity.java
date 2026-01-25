package com.backend.users.entities;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
    name = "password_reset_token_sequence",
    sequenceName = "users.q_password_reset_tokens_id",
    allocationSize = 10)
public class PasswordResetTokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_sequence")
  private Long id;

  private String token;
  private OffsetDateTime expiresAt;
  private boolean used;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @CreationTimestamp private OffsetDateTime createdAt;
}
