package com.example.services;

import com.example.dtos.*;
import com.example.entities.PasswordResetTokenEntity;
import com.example.entities.RefreshTokenEntity;
import com.example.entities.UserEntity;
import com.example.enums.Role;
import com.example.mappers.UserMapper;
import com.example.repositories.UserRepository;
import com.example.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.neo4j.cypherdsl.core.Use;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.utils.Constants.BEARER;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(user);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponseDto(accessToken, refreshToken.getToken());
    }

    @Transactional
    public RefreshTokenResponseDto refreshAccessToken(RefreshTokenRequestDto request) {
        RefreshTokenEntity refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));

        UserEntity user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateToken(user);
        return new RefreshTokenResponseDto(newAccessToken);
    }

    @Transactional
    public void logout(UserEntity user) {
        refreshTokenService.deleteUserRefreshTokens(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        passwordResetService.deleteUserPasswordResetTokens(user);
        PasswordResetTokenEntity resetToken = passwordResetService.createPasswordResetToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Transactional
    public void changePassword(UserEntity user, ChangePasswordRequestDto request) {
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenService.deleteUserRefreshTokens(user);
    }

    public UserDto getProfile(UserEntity user) {
        return userMapper.toDto(user);
    }
}