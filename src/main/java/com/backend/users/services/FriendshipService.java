package com.backend.users.services;

import static com.backend.users.utils.Constants.FRIEND_REQUEST_RESOURCE_NAME;
import static com.backend.users.utils.Constants.USER_RESOURCE_NAME;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.core.exceptions.ForbiddenException;
import com.backend.core.exceptions.ResourceNotFoundException;
import com.backend.core.exceptions.ValidationException;
import com.backend.users.dtos.FriendRequestResponseDto;
import com.backend.users.dtos.UserDto;
import com.backend.users.entities.FriendRequestEntity;
import com.backend.users.entities.UserEntity;
import com.backend.users.enums.FriendRequestStatus;
import com.backend.users.mappers.FriendMapper;
import com.backend.users.repositories.FriendRequestRepository;
import com.backend.users.repositories.UserNodeRepository;
import com.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {
  private final FriendRequestRepository friendRequestRepository;
  private final UserRepository userRepository;
  private final UserNodeRepository userNodeRepository;
  private final FriendMapper friendMapper;

  @Transactional
  public Mono<Void> sendFriendRequest(UserEntity currentUser, Long addresseeId) {
    Long requesterId = currentUser.getId();

    return userRepository
        .findById(addresseeId)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException(addresseeId, USER_RESOURCE_NAME)))
        .flatMap(
            addressee ->
                validateSending(requesterId, addresseeId)
                    .then(
                        Mono.defer(
                            () -> {
                              FriendRequestEntity friendRequest = new FriendRequestEntity();
                              friendRequest.setRequesterId(requesterId);
                              friendRequest.setAddresseeId(addresseeId);
                              friendRequest.setStatus(FriendRequestStatus.PENDING);
                              return friendRequestRepository.save(friendRequest);
                            })))
        .then();
  }

  @Transactional
  public Mono<Void> acceptFriendRequest(UserEntity currentUser, Long requestId) {
    Long currentUserId = currentUser.getId();

    return findFriendRequestById(requestId)
        .flatMap(
            friendRequest -> {
              validateDefaultOperations(friendRequest, currentUserId);
              friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
              return friendRequestRepository.save(friendRequest);
            })
        .then();
  }

  @Transactional
  public Mono<Void> rejectFriendRequest(UserEntity currentUser, Long requestId) {
    Long currentUserId = currentUser.getId();

    return findFriendRequestById(requestId)
        .flatMap(
            friendRequest -> {
              validateDefaultOperations(friendRequest, currentUserId);
              friendRequest.setStatus(FriendRequestStatus.REJECTED);
              return friendRequestRepository.save(friendRequest);
            })
        .then();
  }

  @Transactional
  public Mono<Void> cancelFriendRequest(UserEntity currentUser, Long requestId) {
    Long currentUserId = currentUser.getId();

    return findFriendRequestById(requestId)
        .flatMap(
            friendRequest -> {
              validateCanceling(friendRequest, currentUserId);
              friendRequest.setStatus(FriendRequestStatus.CANCELLED);
              return friendRequestRepository.save(friendRequest);
            })
        .then();
  }

  public Flux<FriendRequestResponseDto> getPendingFriendRequests(Long userId) {
    return friendRequestRepository
        .findByAddresseeIdAndStatus(userId, FriendRequestStatus.PENDING)
        .map(friendMapper::toFriendRequestResponseDto);
  }

  public Flux<FriendRequestResponseDto> getSentFriendRequests(Long userId) {
    return friendRequestRepository
        .findByRequesterIdAndStatus(userId, FriendRequestStatus.PENDING)
        .map(friendMapper::toFriendRequestResponseDto);
  }

  public Flux<UserDto> getFriends(Long userId) {
    return userNodeRepository.findFriendsByUserId(userId).map(friendMapper::toUserDto);
  }

  public Flux<UserDto> getFriendSuggestions(Long userId) {
    return userNodeRepository.findFriendsOfFriends(userId, 10).map(friendMapper::toUserDto);
  }

  private Mono<FriendRequestEntity> findFriendRequestById(Long id) {
    return friendRequestRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException(id, FRIEND_REQUEST_RESOURCE_NAME)));
  }

  private Mono<Void> validateSending(Long requesterId, Long addresseeId) {
    if (requesterId.equals(addresseeId)) {
      return Mono.error(new ValidationException("Cannot send friend request to yourself"));
    }

    return friendRequestRepository
        .areFriends(requesterId, addresseeId)
        .flatMap(
            areFriends -> {
              if (areFriends) {
                return Mono.error(new ValidationException("Users are already friends"));
              }
              return friendRequestRepository
                  .findByIdAndStatus(requesterId, addresseeId, FriendRequestStatus.PENDING.name())
                  .flatMap(
                      fr ->
                          Mono.<Void>error(new ValidationException("Friend request already sent")))
                  .switchIfEmpty(Mono.empty());
            });
  }

  private void validateCanceling(FriendRequestEntity friendRequest, Long requesterId) {
    if (!friendRequest.getRequesterId().equals(requesterId)) {
      throw new ForbiddenException("You are not authorized to cancel this request");
    }
    validateFriendRequestPending(friendRequest);
  }

  private void validateDefaultOperations(FriendRequestEntity friendRequest, Long addresseeId) {
    if (!friendRequest.getAddresseeId().equals(addresseeId)) {
      throw new ForbiddenException("You are not authorized to operate this request");
    }
    validateFriendRequestPending(friendRequest);
  }

  private void validateFriendRequestPending(FriendRequestEntity friendRequest) {
    if (!FriendRequestStatus.PENDING.equals(friendRequest.getStatus())) {
      throw new IllegalArgumentException("Friend request is not pending");
    }
  }
}
