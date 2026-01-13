package com.example.controllers;

import com.example.dtos.AddFriendToListRequestDto;
import com.example.dtos.FriendListResponseDto;
import com.example.dtos.RemoveFriendFromListRequestDto;
import com.example.entities.UserEntity;
import com.example.services.FriendListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/friend-lists")
@RequiredArgsConstructor
public class FriendListController {
    private final FriendListService friendListService;

    @PostMapping
    public void addToFriendList(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody AddFriendToListRequestDto request) {
        friendListService.addFriendToList(
                currentUser.getId(),
                request
        );
    }

    @DeleteMapping
    public void removeFromFriendList(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody RemoveFriendFromListRequestDto request) {
        friendListService.removeFromFriendList(currentUser.getId(), request);
    }

    @GetMapping("/names")
    public List<String> getAllListNames(
            @AuthenticationPrincipal UserEntity currentUser) {
        return friendListService.getAllFriendListNames(currentUser.getId());
    }

    @GetMapping("/{listName}")
    public FriendListResponseDto getFriendsByList(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable String listName) {
        return friendListService.getFriendsByList(
                currentUser.getId(),
                listName
        );
    }
}
