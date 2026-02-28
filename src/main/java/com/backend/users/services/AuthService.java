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
import com.backend.users.dtos.ChangePasswordRequestDto;
import com.backend.users.dtos.ForgotPasswordRequestDto;
import com.backend.users.dtos.LoginRequestDto;
import com.backend.users.dtos.LoginResponseDto;
import com.backend.users.dtos.RefreshTokenRequestDto;
import com.backend.users.dtos.RefreshTokenResponseDto;
import com.backend.users.dtos.RegisterRequestDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.enums.JwtPayloadFields;
import com.backend.users.enums.Role;
import com.backend.users.mappers.UserMapper;
import com.backend.users.repositories.UserRepository;
import com.backend.users.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final RefreshTokenService refreshTokenService;
  private final PasswordResetService passwordResetService;
  private final EmailService emailService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final ReactiveAuthenticationManager authenticationManager;
  private final UserMapper userMapper;

  @Transactional
  public Mono<Void> register(RegisterRequestDto request) {
    return userRepository
        .existsByEmail(request.getEmail())
        .flatMap(
            exists -> {
              if (exists) {
                return Mono.error(new IllegalArgumentException("Email already exists"));
              }

              UserEntity user = new UserEntity();
              user.setEmail(request.getEmail());
              user.setPassword(passwordEncoder.encode(request.getPassword()));
              user.setRole(Role.USER);
              return userRepository.save(user).then();
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
  public Mono<RefreshTokenResponseDto> refreshAccessToken(RefreshTokenRequestDto request) {
    return refreshTokenService
        .validateRefreshToken(request.getRefreshToken())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid or expired refresh token")))
        .flatMap(
            refreshToken ->
                userRepository
                    .findById(refreshToken.getUserId())
                    .map(user -> new RefreshTokenResponseDto(jwtUtil.generateToken(user))));
  }

  @Transactional
  public Mono<Void> logout(Long userId) {
    return refreshTokenService.deleteUserRefreshTokens(userId);
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
                    .deleteUserPasswordResetTokens(user.getId())
                    .then(passwordResetService.createPasswordResetToken(user))
                    .flatMap(
                        resetToken ->
                            emailService.sendPasswordResetEmail(
                                user.getEmail(), resetToken.getToken())));
  }

  @Transactional
  public Mono<Void> changePassword(UserEntity user, ChangePasswordRequestDto request) {
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    return userRepository
        .save(user)
        .then(refreshTokenService.deleteUserRefreshTokens(user.getId()));
  }

  public Mono<UserDto> getProfile(UserEntity user) {
    return Mono.just(userMapper.toDto(user));
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
          return ValidateTokenResponseDto.builder()
              .valid(true)
              .expiresAt(jwtUtil.extractExpiration(token))
              .id(Long.valueOf(extractPayload.get(JwtPayloadFields.ID.getName()).toString()))
              .email(extractPayload.get(JwtPayloadFields.EMAIL.getName()).toString())
              .build();
        });
  }
}
