package com.backend.users.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.backend.users.dtos.FriendRequestResponseDto;
import com.backend.users.dtos.SendFriendRequestDto;
import com.backend.users.dtos.UserDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.FriendshipService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {
  private final FriendshipService friendshipService;

  @PostMapping("/requests")
  public Mono<Void> sendFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser,
      @Valid @RequestBody SendFriendRequestDto request) {
    return friendshipService.sendFriendRequest(currentUser, request.getAddresseeId());
  }

  @PostMapping("/requests/{requestId}/accept")
  public Mono<Void> acceptFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long requestId) {
    return friendshipService.acceptFriendRequest(currentUser, requestId);
  }

  @PostMapping("/requests/{requestId}/reject")
  public Mono<Void> rejectFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long requestId) {
    return friendshipService.rejectFriendRequest(currentUser, requestId);
  }

  @PostMapping("/requests/{requestId}/cancel")
  public Mono<Void> cancelFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long requestId) {
    return friendshipService.cancelFriendRequest(currentUser, requestId);
  }

  @GetMapping("/requests/pending")
  public Flux<FriendRequestResponseDto> getPendingFriendRequests(
      @AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getPendingFriendRequests(currentUser.getId());
  }

  @GetMapping("/requests/sent")
  public Flux<FriendRequestResponseDto> getSentFriendRequests(
      @AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getSentFriendRequests(currentUser.getId());
  }

  @GetMapping("/friends")
  public Flux<UserDto> getFriends(@AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getFriends(currentUser.getId());
  }

  @GetMapping("/suggestions")
  public Flux<UserDto> getFriendSuggestions(@AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getFriendSuggestions(currentUser.getId());
  }
}
