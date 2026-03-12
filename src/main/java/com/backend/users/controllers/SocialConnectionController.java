package com.backend.users.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.backend.core.web.page.Page;
import com.backend.users.dtos.UserDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.SocialConnectionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/social")
@RequiredArgsConstructor
public class SocialConnectionController {
  private final SocialConnectionService socialConnectionService;

  @PostMapping("/follow/{followedId}")
  public Mono<Void> follow(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long followedId) {
    return socialConnectionService.follow(currentUser, followedId);
  }

  @PostMapping("/unfollow/{followedId}")
  public Mono<Void> unfollow(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long followedId) {
    return socialConnectionService.unfollow(currentUser, followedId);
  }

  @GetMapping("/following")
  public Mono<Page<UserDto>> getFollowing(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam Long offset,
      @RequestParam Integer pageSize) {
    return socialConnectionService.getFollowing(currentUser, offset, pageSize);
  }

  @GetMapping("/followers")
  public Mono<Page<UserDto>> getFollowers(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam Long offset,
      @RequestParam Integer pageSize) {
    return socialConnectionService.getFollowers(currentUser.getId(), offset, pageSize);
  }

  @PostMapping("/block/{blockedId}")
  public Mono<Void> block(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long blockedId) {
    return socialConnectionService.block(currentUser, blockedId);
  }

  @PostMapping("/unblock/{blockedId}")
  public Mono<Void> unblock(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long blockedId) {
    return socialConnectionService.unblock(currentUser, blockedId);
  }

  @GetMapping("/blocked")
  public Mono<Page<UserDto>> getBlockedUsers(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam Long offset,
      @RequestParam Integer pageSize) {
    return socialConnectionService.getBlockedUsers(currentUser, offset, pageSize);
  }
}
