package com.backend.users.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.users.services.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class InternalController {
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/cleanup/refresh-tokens")
  public Mono<Void> cleanupRefreshTokens() {
    return refreshTokenService.cleanupExpiredTokens();
  }
}
