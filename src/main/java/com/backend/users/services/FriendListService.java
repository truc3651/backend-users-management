package com.backend.users.services;

import com.backend.users.dtos.AddFriendToListRequestDto;
import com.backend.users.dtos.FriendListResponseDto;
import com.backend.users.dtos.RemoveFriendFromListRequestDto;
import com.backend.users.graph.UserNode;
import com.backend.users.mappers.FriendMapper;
import com.backend.users.repositories.FriendRequestRepository;
import com.backend.users.repositories.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendListService {
    private final UserNodeRepository userNodeRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendMapper friendMapper;

    @Transactional
    public void addFriendToList(Long userId, AddFriendToListRequestDto request) {
        Long friendId = request.getFriendId();
        String listName = request.getListName();

        validateAddingFriendToList(userId, friendId);

        List<String> existingLists = userNodeRepository.findListsContainingFriend(userId, friendId);
        if (!existingLists.contains(listName)) {
            userNodeRepository.addToFriendList(userId, friendId, listName);
        }
    }

    @Transactional
    public void removeFromFriendList(Long userId, RemoveFriendFromListRequestDto request) {
        Long friendId = request.getFriendId();
        String listName = request.getListName();

        List<String> existingLists = userNodeRepository.findListsContainingFriend(userId, friendId);
        if (existingLists.contains(listName)) {
            userNodeRepository.removeFromFriendList(userId, friendId, listName);
        }
    }

    public FriendListResponseDto getFriendsByList(Long userId, String listName) {
        List<UserNode> friendEntities = userNodeRepository.findFriendsByList(userId, listName);
        return friendMapper.fromUserNodeEntityToDto(friendEntities);
    }

    public List<String> getAllFriendListNames(Long userId) {
        return userNodeRepository.findAllFriendListNames(userId);
    }

    private void validateAddingFriendToList(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Cannot add yourself to a friend list");
        }
        if (!friendRequestRepository.areFriends(userId, friendId)) {
            throw new IllegalArgumentException("Can only add friends to friend lists");
        }
    }
}
