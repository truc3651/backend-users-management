package com.backend.users.services;

import java.util.Map;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.core.dtos.UserDto;
import com.backend.core.dtos.ValidateTokenRequestDto;
import com.backend.core.dtos.ValidateTokenResponseDto;
import com.backend.core.exceptions.ValidationException;
import com.backend.users.dtos.ChangePasswordRequestDto;
import com.backend.users.dtos.ForgotPasswordRequestDto;
import com.backend.users.dtos.LoginRequestDto;
import com.backend.users.dtos.LoginResponseDto;
import com.backend.users.dtos.LogoutRequestDto;
import com.backend.users.dtos.RefreshTokenRequestDto;
import com.backend.users.dtos.RefreshTokenResponseDto;
import com.backend.users.dtos.RegisterRequestDto;
import com.backend.users.dtos.ResetPasswordRequestDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.enums.JwtPayloadFields;
import com.backend.users.repositories.UserRepository;
import com.backend.users.ses.MailService;
import com.backend.users.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
  private final RefreshTokenService refreshTokenService;
  private final PasswordResetService passwordResetService;
  private final MailService mailService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final ReactiveAuthenticationManager authenticationManager;

  @Transactional
  public Mono<Void> register(RegisterRequestDto request) {
    return userRepository
        .existsByEmail(request.getEmail())
        .flatMap(
            exists -> {
              if (exists) {
                return Mono.error(new ValidationException("Email already exists"));
              }

              UserEntity user = new UserEntity();
              user.setEmail(request.getEmail());
              user.setPassword(passwordEncoder.encode(request.getPassword()));
              return userRepository
                  .save(user)
                  .doOnSuccess(u -> mailService.sendWelcomeMail(u.getEmail()).subscribe())
                  .then();
            });
  }

  @Transactional
  public Mono<LoginResponseDto> login(LoginRequestDto request) {
    return authenticationManager
        .authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()))
        .flatMap(
            authentication -> {
              UserEntity user = (UserEntity) authentication.getPrincipal();
              String accessToken = jwtUtil.generateToken(user);
              return refreshTokenService
                  .createRefreshToken(user)
                  .map(refreshToken -> new LoginResponseDto(accessToken, refreshToken.getToken()));
            });
  }

  @Transactional
  public Mono<Void> forgotPassword(ForgotPasswordRequestDto request) {
    return userRepository
        .findByEmail(request.getEmail())
        .switchIfEmpty(
            Mono.error(
                new UsernameNotFoundException("User not found with email: " + request.getEmail())))
        .flatMap(
            user ->
                passwordResetService
                    .createPasswordResetToken(user)
                    .doOnSuccess(
                        rp ->
                            mailService
                                .sendResetPasswordMail(user.getEmail(), rp.getToken())
                                .subscribe()))
        .then();
  }

  public Mono<Void> resetPassword(ResetPasswordRequestDto request) {
    return passwordResetService
        .validatePasswordResetToken(request.getToken())
        .switchIfEmpty(Mono.error(new ValidationException("Invalid or expired reset token")))
        .flatMap(
            token ->
                userRepository
                    .findById(token.getUserId())
                    .switchIfEmpty(Mono.error(new ValidationException("User not found")))
                    .flatMap(
                        user -> {
                          user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                          return userRepository.save(user);
                        })
                    .then(passwordResetService.deletePasswordResetToken(request.getToken())))
        .then();
  }

  public Mono<ValidateTokenResponseDto> validateToken(ValidateTokenRequestDto request) {
    return Mono.fromCallable(
        () -> {
          String token = request.getToken();
          boolean isInvalid = jwtUtil.isTokenExpired(token);
          if (isInvalid) {
            return ValidateTokenResponseDto.builder().valid(false).build();
          }
          Map<String, Object> extractPayload = jwtUtil.extractPayload(token);
          UserDto user =
              new UserDto(
                  (Long) extractPayload.get(JwtPayloadFields.ID.getName()),
                  (String) extractPayload.get(JwtPayloadFields.EMAIL.getName()));
          return ValidateTokenResponseDto.builder()
              .valid(true)
              .expiresAt(jwtUtil.extractExpiration(token))
              .user(user)
              .build();
        });
  }

  @Transactional
  public Mono<Void> changePassword(UserEntity currentUser, ChangePasswordRequestDto request) {
    Long userId = currentUser.getId();
    return userRepository
        .findById(userId)
        .flatMap(
            user -> {
              user.setPassword(passwordEncoder.encode(request.getNewPassword()));
              return userRepository.save(user);
            })
        .then();
  }

  @Transactional
  public Mono<RefreshTokenResponseDto> refreshAccessToken(RefreshTokenRequestDto request) {
    return refreshTokenService
        .validateRefreshToken(request.getRefreshToken())
        .switchIfEmpty(Mono.error(new ValidationException("Invalid or expired refresh token")))
        .flatMap(
            refreshToken ->
                userRepository
                    .findById(refreshToken.getUserId())
                    .map(user -> new RefreshTokenResponseDto(jwtUtil.generateToken(user))));
  }

  @Transactional
  public Mono<Void> logout(LogoutRequestDto request) {
    // accessToken is short live, let it expires itself without any intervention
    return refreshTokenService.deleteRefreshToken(request.getRefreshToken());
  }
}
