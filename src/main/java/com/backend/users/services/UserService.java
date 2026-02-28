package com.backend.users.services;

import static com.backend.users.utils.Constants.USER_RESOURCE_NAME;

import org.springframework.stereotype.Service;

import com.backend.core.exceptions.ResourceNotFoundException;
import com.backend.users.entities.UserEntity;
import com.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;

  public Mono<UserEntity> findUserById(Long id) {
    return userRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException(id, USER_RESOURCE_NAME)));
  }
}
