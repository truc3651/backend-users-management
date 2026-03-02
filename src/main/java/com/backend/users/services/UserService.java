package com.backend.users.services;

import static com.backend.users.utils.Constants.USER_RESOURCE_NAME;

import org.springframework.stereotype.Service;

import com.backend.core.cache.ReactiveCacheTemplate;
import com.backend.core.dtos.UserDto;
import com.backend.core.exceptions.ResourceNotFoundException;
import com.backend.users.entities.UserEntity;
import com.backend.users.mappers.UserMapper;
import com.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ReactiveCacheTemplate<UserDto> coreUserCache;

  public Mono<UserEntity> findUserById(Long id) {
    return userRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException(id, USER_RESOURCE_NAME)));
  }

  public Mono<UserDto> getUserById(Long id) {
    return coreUserCache.get(String.valueOf(id), this::loadUserFromDb);
  }

  public Mono<Void> evictUserCache(Long id) {
    return coreUserCache.evict(String.valueOf(id));
  }

  private Mono<UserDto> loadUserFromDb(String id) {
    return userRepository
        .findById(Long.valueOf(id))
        .map(userMapper::toDto)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException(id, USER_RESOURCE_NAME)));
  }
}
