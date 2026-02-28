package com.backend.users.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.core.web.page.Page;
import com.backend.users.clients.KafkaPublisher;
import com.backend.users.dtos.BlockEventDto;
import com.backend.users.dtos.FollowEventDto;
import com.backend.users.dtos.UnblockEventDto;
import com.backend.users.dtos.UnfollowEventDto;
import com.backend.users.dtos.UserDto;
import com.backend.users.mappers.FriendMapper;
import com.backend.users.repositories.UserNodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialConnectionService {
  private final UserNodeRepository userNodeRepository;
  private final FriendMapper friendMapper;
  private final KafkaPublisher kafkaPublisher;

  public Mono<Void> follow(Long followerId, Long followedId) {
    FollowEventDto payload = new FollowEventDto(followerId, followedId);
    return kafkaPublisher.sendFollowEvent(payload);
  }

  public Mono<Void> unfollow(Long followerId, Long followedId) {
    UnfollowEventDto payload = new UnfollowEventDto(followerId, followedId);
    return kafkaPublisher.sendUnfollowEvent(payload);
  }

  public Mono<Page<UserDto>> getFollowing(Long userId, Pageable pageable) {
    long skip = pageable.getOffset();
    int limit = pageable.getPageSize();

    return userNodeRepository
        .findFollowingPaginated(userId, skip, limit)
        .map(friendMapper::toUserDto)
        .collectList()
        .zipWith(userNodeRepository.countFollowing(userId))
        .map(tuple -> friendMapper.toUserDtoPage(tuple.getT1(), tuple.getT2(), pageable));
  }

  public Mono<Page<UserDto>> getFollowers(Long userId, Pageable pageable) {
    long skip = pageable.getOffset();
    int limit = pageable.getPageSize();

    return userNodeRepository
        .findFollowersPaginated(userId, skip, limit)
        .map(friendMapper::toUserDto)
        .collectList()
        .zipWith(userNodeRepository.countFollowers(userId))
        .map(tuple -> friendMapper.toUserDtoPage(tuple.getT1(), tuple.getT2(), pageable));
  }

  public Mono<Void> block(Long userId, Long blockedId) {
    BlockEventDto payload = new BlockEventDto(userId, blockedId);
    return kafkaPublisher.sendBlockEvent(payload);
  }

  public Mono<Void> unblock(Long userId, Long blockedId) {
    UnblockEventDto payload = new UnblockEventDto(userId, blockedId);
    return kafkaPublisher.sendUnblockEvent(payload);
  }

  public Mono<Page<UserDto>> getBlockedUsers(Long userId, Pageable pageable) {
    long skip = pageable.getOffset();
    int limit = pageable.getPageSize();

    return userNodeRepository
        .findBlockedUsersPaginated(userId, skip, limit)
        .map(friendMapper::toUserDto)
        .collectList()
        .zipWith(userNodeRepository.countBlockedUsers(userId))
        .map(tuple -> friendMapper.toUserDtoPage(tuple.getT1(), tuple.getT2(), pageable));
  }
}
