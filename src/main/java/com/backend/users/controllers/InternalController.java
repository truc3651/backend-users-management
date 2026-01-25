package com.backend.users.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.users.services.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class InternalController {
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/cleanup/refresh-tokens")
  public void cleanupRefreshTokens() {
    refreshTokenService.cleanupExpiredTokens();
  }
}
