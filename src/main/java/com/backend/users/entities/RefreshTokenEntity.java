package com.backend.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "t_refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "refresh_token_sequence", sequenceName = "users.q_refresh_tokens_id", allocationSize = 10)
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_sequence")
    private Long id;

    private String token;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
