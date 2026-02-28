package com.backend.users.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.backend.core.web.page.OffsetBasedPageRequest;
import com.backend.core.web.page.Page;
import com.backend.users.clients.KafkaPublisher;
import com.backend.users.dtos.UserDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.SocialConnectionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialConnectionController {
  private final SocialConnectionService socialConnectionService;
  private final KafkaPublisher kafkaPublisher;

  @PostMapping("/follow/{followedId}")
  public Mono<Void> follow(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long followedId) {
    return socialConnectionService.follow(currentUser.getId(), followedId);
  }

  @PostMapping("/unfollow/{followedId}")
  public Mono<Void> unfollow(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long followedId) {
    return socialConnectionService.unfollow(currentUser.getId(), followedId);
  }

  @GetMapping("/following")
  public Mono<Page<UserDto>> getFollowing(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam Long offset,
      @RequestParam Integer pageSize) {
    Pageable pageable = new OffsetBasedPageRequest(offset, pageSize);
    return socialConnectionService.getFollowing(currentUser.getId(), pageable);
  }

  @GetMapping("/followers")
  public Mono<Page<UserDto>> getFollowers(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam Long offset,
      @RequestParam Integer pageSize) {
    Pageable pageable = new OffsetBasedPageRequest(offset, pageSize);
    return socialConnectionService.getFollowers(currentUser.getId(), pageable);
  }

  @PostMapping("/unfollow/{blockedId}")
  public Mono<Void> block(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long blockedId) {
    return socialConnectionService.block(currentUser.getId(), blockedId);
  }

  @PostMapping("/unfollow/{blockedId}")
  public Mono<Void> unblock(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable Long blockedId) {
    return socialConnectionService.unblock(currentUser.getId(), blockedId);
  }

  @GetMapping("/blocked")
  public Mono<Page<UserDto>> getBlockedUsers(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam Long offset,
      @RequestParam Integer pageSize) {
    Pageable pageable = new OffsetBasedPageRequest(offset, pageSize);
    return socialConnectionService.getBlockedUsers(currentUser.getId(), pageable);
  }
}
