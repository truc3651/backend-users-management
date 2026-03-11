package com.backend.users.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.core.dtos.UserDto;
import com.backend.users.dtos.ChangePasswordRequestDto;
import com.backend.users.dtos.LogoutRequestDto;
import com.backend.users.dtos.RefreshTokenRequestDto;
import com.backend.users.dtos.RefreshTokenResponseDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/user")
@RequiredArgsConstructor
public class UserController {
  private final AuthService authService;

  @PostMapping("/change-password")
  public Mono<Void> changePassword(@AuthenticationPrincipal UserEntity currentUser, @Valid @RequestBody ChangePasswordRequestDto request) {
    return authService.changePassword(currentUser, request);
  }

  @PostMapping("/refresh")
  public Mono<RefreshTokenResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
    return authService.refreshAccessToken(request);
  }

  @PostMapping("/logout")
  public Mono<Void> logout(@Valid @RequestBody LogoutRequestDto request) {
    return authService.logout(request);
  }
}
