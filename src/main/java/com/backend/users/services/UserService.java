package com.backend.users.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.core.dtos.UserDto;
import com.backend.users.dtos.ChangePasswordRequestDto;
import com.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Mono<Void> changePassword(UserDto currentUser, ChangePasswordRequestDto request) {
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
}
