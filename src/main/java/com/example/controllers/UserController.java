package com.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dtos.ChangePasswordRequestDto;
import com.example.dtos.RefreshTokenRequestDto;
import com.example.dtos.RefreshTokenResponseDto;
import com.example.dtos.UserDto;
import com.example.entities.UserEntity;
import com.example.services.AuthService;

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