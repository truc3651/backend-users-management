package com.backend.users.services;

import com.backend.users.entities.PasswordResetTokenEntity;
import com.backend.users.entities.UserEntity;
import com.backend.users.repositories.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${password.reset-token-expiration:900000}")
    private Long resetTokenExpiration;

    public PasswordResetTokenEntity createPasswordResetToken(UserEntity user) {
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(resetTokenExpiration / 1000));
        token.setUsed(false);

        return passwordResetTokenRepository.save(token);
    }

    public Optional<PasswordResetTokenEntity> validatePasswordResetToken(String token) {
        Optional<PasswordResetTokenEntity> resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken.isPresent()) {
            PasswordResetTokenEntity rt = resetToken.get();
            if (rt.getExpiresAt().isBefore(OffsetDateTime.now()) || rt.isUsed()) {
                return Optional.empty();
            }
        }

        return resetToken;
    }

    @Transactional
    public void markTokenAsUsed(PasswordResetTokenEntity token) {
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    @Transactional
    public void deleteUserPasswordResetTokens(UserEntity user) {
        passwordResetTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
    }
}
