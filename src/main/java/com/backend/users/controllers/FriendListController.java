package com.backend.users.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.backend.users.dtos.AddFriendToListRequestDto;
import com.backend.users.dtos.FriendListResponseDto;
import com.backend.users.dtos.RemoveFriendFromListRequestDto;
import com.backend.users.entities.UserEntity;
import com.backend.users.services.FriendListService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/friend-lists")
@RequiredArgsConstructor
public class FriendListController {
  private final FriendListService friendListService;

  @PostMapping
  public void addToFriendList(
      @AuthenticationPrincipal UserEntity currentUser,
      @Valid @RequestBody AddFriendToListRequestDto request) {
    friendListService.addFriendToList(currentUser.getId(), request);
  }

  @DeleteMapping
  public void removeFromFriendList(
      @AuthenticationPrincipal UserEntity currentUser,
      @Valid @RequestBody RemoveFriendFromListRequestDto request) {
    friendListService.removeFromFriendList(currentUser.getId(), request);
  }

  @GetMapping("/names")
  public List<String> getAllListNames(@AuthenticationPrincipal UserEntity currentUser) {
    return friendListService.getAllFriendListNames(currentUser.getId());
  }

  @GetMapping("/{listName}")
  public FriendListResponseDto getFriendsByList(
      @AuthenticationPrincipal UserEntity currentUser, @PathVariable String listName) {
    return friendListService.getFriendsByList(currentUser.getId(), listName);
  }
}
