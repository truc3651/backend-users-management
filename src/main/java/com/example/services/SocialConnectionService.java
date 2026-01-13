package com.example.services;

import com.example.dtos.BlockRequestDto;
import com.example.dtos.Page;
import com.example.dtos.UserDto;
import com.example.graph.UserNode;
import com.example.mappers.FriendMapper;
import com.example.repositories.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialConnectionService {
    private final UserNodeRepository userNodeRepository;
    private final UserService userService;
    private final FriendMapper friendMapper;

    @Transactional
    public void follow(Long followedId, Long followerId) {
        if (followedId.equals(followerId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        userService.ensureUserNodeExists(followerId);
        userService.ensureUserNodeExists(followedId);

        if (userNodeRepository.areBlocking(followerId, followedId)) {
            throw new IllegalArgumentException("Cannot follow this user due to blocking");
        }
        userNodeRepository.upsertFollow(followerId, followedId);
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) {
        userNodeRepository.deleteFollow(followerId, followedId);
    }

    public Page<UserDto> getFollowing(Long userId, Pageable pageable) {
        org.springframework.data.domain.Page<UserNode> page =
                userNodeRepository.findFollowingPaginated(userId, pageable);
        return friendMapper.toUserDtoPage(page);
    }

    public Page<UserDto> getFollowers(Long userId, Pageable pageable) {
        org.springframework.data.domain.Page<UserNode> page =
                userNodeRepository.findFollowersPaginated(userId, pageable);
        return friendMapper.toUserDtoPage(page);
    }

    @Transactional
    public void block(Long blockerId, BlockRequestDto request) {
        Long blockedId = request.getUserId();
        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("Cannot block yourself");
        }

        userService.ensureUserNodeExists(blockerId);
        userService.ensureUserNodeExists(blockedId);

        userNodeRepository.upsertBlock(blockerId, blockedId, request.getReason());
    }

    @Transactional
    public void unblock(Long blockerId, Long blockedId) {
        userNodeRepository.deleteBlock(blockerId, blockedId);
    }

    public Page<UserDto> getBlockedUsers(Long userId, Pageable pageable) {
        org.springframework.data.domain.Page<UserNode> page =
                userNodeRepository.findBlockedUsersPaginated(userId, pageable);
        return friendMapper.toUserDtoPage(page);
    }
}
