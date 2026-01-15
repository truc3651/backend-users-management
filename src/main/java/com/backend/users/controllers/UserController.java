package com.backend.users.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.users.dtos.ChangePasswordRequestDto;
import com.backend.users.dtos.RefreshTokenRequestDto;
import com.backend.users.dtos.RefreshTokenResponseDto;
import com.backend.core.dto.UserDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.AuthService;

@RestController
@RequestMapping("/v1/api/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @GetMapping("/me")
    public UserDto getProfile(@AuthenticationPrincipal UserEntity currentUser) {
        return authService.getProfile(currentUser);
    }

    @PostMapping("/change-password")
    public void changePassword(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody ChangePasswordRequestDto request) {
        authService.changePassword(currentUser, request);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        return authService.refreshAccessToken(request);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserEntity currentUser) {
        authService.logout(currentUser);
    }
}