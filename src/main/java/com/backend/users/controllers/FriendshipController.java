package com.backend.users.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.backend.users.dtos.FriendRequestResponseDto;
import com.backend.users.dtos.SendFriendRequestDto;
import com.backend.users.dtos.UserDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.FriendshipService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {
  private final FriendshipService friendshipService;

  @PostMapping("/requests")
  public void sendFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser,
      @Valid @RequestBody SendFriendRequestDto request) {
    friendshipService.sendFriendRequest(currentUser, request.getAddresseeId());
  }

  @PostMapping("/requests/{requestId}/accept")
  public void acceptFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long requestId) {
    friendshipService.acceptFriendRequest(currentUser, requestId);
  }

  @PostMapping("/requests/{requestId}/reject")
  public void rejectFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long requestId) {
    friendshipService.rejectFriendRequest(currentUser, requestId);
  }

  @PostMapping("/requests/{requestId}/cancel")
  public void cancelFriendRequest(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long requestId) {
    friendshipService.cancelFriendRequest(currentUser, requestId);
  }

  @DeleteMapping("/{friendId}")
  public void unfriend(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long friendId) {
    friendshipService.unfriend(currentUser, friendId);
  }

  @GetMapping("/requests/pending")
  public List<FriendRequestResponseDto> getPendingFriendRequests(
      @AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getPendingFriendRequests(currentUser.getId());
  }

  @GetMapping("/requests/sent")
  public List<FriendRequestResponseDto> getSentFriendRequests(
      @AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getSentFriendRequests(currentUser.getId());
  }

  @GetMapping("/friends")
  public List<UserDto> getFriends(@AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getFriends(currentUser.getId());
  }

  @GetMapping("/suggestions")
  public List<UserDto> getFriendSuggestions(@AuthenticationPrincipal UserEntity currentUser) {
    return friendshipService.getFriendSuggestions(currentUser.getId());
  }
}
