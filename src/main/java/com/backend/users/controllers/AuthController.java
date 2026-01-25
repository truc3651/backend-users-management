package com.backend.users.controllers;

import org.springframework.web.bind.annotation.*;

import com.backend.core.dto.ValidateTokenRequestDto;
import com.backend.core.dto.ValidateTokenResponseDto;
import com.backend.users.dtos.ForgotPasswordRequestDto;
import com.backend.users.dtos.LoginRequestDto;
import com.backend.users.dtos.LoginResponseDto;
import com.backend.users.dtos.RegisterRequestDto;
import com.backend.users.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public void register(@Valid @RequestBody RegisterRequestDto request) {
    authService.register(request);
  }

  @PostMapping("/login")
  public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
    return authService.login(request);
  }

  @PostMapping("/forgot-password")
  public void forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
    authService.forgotPassword(request);
  }

  @PostMapping("/validate-token")
  public ValidateTokenResponseDto validateToken(
      @Valid @RequestBody ValidateTokenRequestDto request) {
    return authService.validateToken(request);
  }
}
