package com.backend.users.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.core.dtos.UserDto;
import com.backend.users.dtos.ChangePasswordRequestDto;
import com.backend.users.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/change-password")
  public Mono<Void> changePassword(
      @AuthenticationPrincipal UserDto currentUser,
      @Valid @RequestBody ChangePasswordRequestDto request) {
    return userService.changePassword(currentUser, request);
  }
}
