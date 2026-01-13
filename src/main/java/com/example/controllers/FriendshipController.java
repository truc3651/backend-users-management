package com.example.controllers;

import com.example.dtos.FriendRequestResponseDto;
import com.example.dtos.SendFriendRequestDto;
import com.example.dtos.UserDto;
import com.example.entities.FriendRequestEntity;
import com.example.entities.UserEntity;
import com.example.graph.UserNode;
import com.example.services.FriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/requests")
    public void sendFriendRequest(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody SendFriendRequestDto request) {
        friendshipService.sendFriendRequest(
            currentUser,
            request.getAddresseeId()
        );
    }

    @PostMapping("/requests/{requestId}/accept")
    public void acceptFriendRequest(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long requestId) {
        friendshipService.acceptFriendRequest(
            currentUser,
            requestId
        );
    }

    @PostMapping("/requests/{requestId}/reject")
    public void rejectFriendRequest(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long requestId) {
        friendshipService.rejectFriendRequest(
                currentUser,
                requestId
        );
    }

    @PostMapping("/requests/{requestId}/cancel")
    public void cancelFriendRequest(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long requestId) {
        friendshipService.cancelFriendRequest(
                currentUser,
                requestId
        );
    }

    @DeleteMapping("/{friendId}")
    public void unfriend(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable Long friendId) {
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
    public List<UserDto> getFriends(
            @AuthenticationPrincipal UserEntity currentUser) {
        return friendshipService.getFriends(currentUser.getId());
    }

    @GetMapping("/suggestions")
    public List<UserDto> getFriendSuggestions(
            @AuthenticationPrincipal UserEntity currentUser) {
        return friendshipService.getFriendSuggestions(currentUser.getId());
    }
}
