package com.example.controllers;

import com.example.dtos.*;
import com.example.entities.UserEntity;
import com.example.graph.UserNode;
import com.example.services.SocialConnectionService;
import com.example.utils.OffsetBasedPageRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialConnectionController {
    private final SocialConnectionService socialConnectionService;

    @PostMapping("/follow/{followerId}")
    public void follow(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long followerId) {
        socialConnectionService.follow(currentUser.getId(), followerId);
    }

    @DeleteMapping("/follow/{followerId}")
    public void unfollow(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long followerId) {
        socialConnectionService.unfollow(currentUser.getId(), followerId);
    }

    @GetMapping("/following")
    public Page<UserDto> getFollowing(
            @AuthenticationPrincipal UserEntity currentUser,
            @RequestParam Long offset,
            @RequestParam Integer pageSize) {
        Pageable pageable = new OffsetBasedPageRequest(offset, pageSize);
        return socialConnectionService.getFollowing(currentUser.getId(), pageable);
    }

    @GetMapping("/followers")
    public Page<UserDto> getFollowers(
            @AuthenticationPrincipal UserEntity currentUser,
            @RequestParam Long offset,
            @RequestParam Integer pageSize) {
        Pageable pageable = new OffsetBasedPageRequest(offset, pageSize);
        return socialConnectionService.getFollowers(currentUser.getId(), pageable);
    }

    @PostMapping("/block")
    public void block(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody BlockRequestDto request) {
        socialConnectionService.block(currentUser.getId(), request);
    }

    @DeleteMapping("/block/{userId}")
    public ResponseEntity<Void> unblock(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long userId) {

        socialConnectionService.unblock(currentUser.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocked")
    public Page<UserDto> getBlockedUsers(
            @AuthenticationPrincipal UserEntity currentUser,
            @RequestParam Long offset,
            @RequestParam Integer pageSize) {
        Pageable pageable = new OffsetBasedPageRequest(offset, pageSize);
        return socialConnectionService.getBlockedUsers(currentUser.getId(), pageable);
    }
}
