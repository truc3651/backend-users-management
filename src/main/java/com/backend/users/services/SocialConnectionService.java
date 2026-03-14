package com.backend.users.services;

import org.springframework.stereotype.Service;

import com.backend.core.dtos.UserDto;
import com.backend.core.web.page.Page;
import com.backend.users.dtos.BlockPayloadDto;
import com.backend.users.dtos.FollowPayloadDto;
import com.backend.users.dtos.UnblockPayloadDto;
import com.backend.users.dtos.UnfollowPayloadDto;
import com.backend.users.kafka.KafkaPublisher;
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

  public Mono<Void> follow(UserDto currentUser, Long followedId) {
    FollowPayloadDto payload = new FollowPayloadDto(currentUser.getId(), followedId);
    return kafkaPublisher.sendFollowEvent(payload);
  }

  public Mono<Void> unfollow(UserDto currentUser, Long followedId) {
    UnfollowPayloadDto payload = new UnfollowPayloadDto(currentUser.getId(), followedId);
    return kafkaPublisher.sendUnfollowEvent(payload);
  }

  public Mono<Page<UserDto>> getFollowing(UserDto currentUser, Long offset, Integer pageSize) {
    Long userId = currentUser.getId();

    return userNodeRepository
        .findFollowingPaginated(userId, offset, pageSize)
        .map(friendMapper::toUserDto)
        .collectList()
        .zipWith(userNodeRepository.countFollowing(userId))
        .map(tuple -> friendMapper.toUserDtoPage(tuple.getT1(), tuple.getT2()));
  }

  public Mono<Page<UserDto>> getFollowers(Long userId, Long offset, Integer pageSize) {
    return userNodeRepository
        .findFollowersPaginated(userId, offset, pageSize)
        .map(friendMapper::toUserDto)
        .collectList()
        .zipWith(userNodeRepository.countFollowers(userId))
        .map(tuple -> friendMapper.toUserDtoPage(tuple.getT1(), tuple.getT2()));
  }

  public Mono<Void> block(UserDto currentUser, Long blockedId) {
    BlockPayloadDto payload = new BlockPayloadDto(currentUser.getId(), blockedId);
    return kafkaPublisher.sendBlockEvent(payload);
  }

  public Mono<Void> unblock(UserDto currentUser, Long blockedId) {
    UnblockPayloadDto payload = new UnblockPayloadDto(currentUser.getId(), blockedId);
    return kafkaPublisher.sendUnblockEvent(payload);
  }

  public Mono<Page<UserDto>> getBlockedUsers(UserDto currentUser, Long offset, Integer pageSize) {
    Long userId = currentUser.getId();
    return userNodeRepository
        .findBlockedUsersPaginated(userId, offset, pageSize)
        .map(friendMapper::toUserDto)
        .collectList()
        .zipWith(userNodeRepository.countBlockedUsers(userId))
        .map(tuple -> friendMapper.toUserDtoPage(tuple.getT1(), tuple.getT2()));
  }
}
