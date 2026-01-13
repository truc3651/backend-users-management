package com.example.services;

import com.example.dtos.FriendRequestResponseDto;
import com.example.dtos.UserDto;
import com.example.entities.FriendRequestEntity;
import com.example.entities.UserEntity;
import com.example.enums.FriendRequestStatus;
import com.example.exceptions.ForbiddenException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.exceptions.ValidationException;
import com.example.graph.UserNode;
import com.example.mappers.FriendMapper;
import com.example.repositories.FriendRequestRepository;
import com.example.repositories.UserNodeRepository;
import com.example.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.utils.Constants.FRIEND_REQUEST_RESOURCE_NAME;
import static com.example.utils.Constants.USER_RESOURCE_NAME;

@Service
@Slf4j
public class FriendshipService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final UserNodeRepository userNodeRepository;
    private final FriendMapper friendMapper;
    private final UserService userService;
    private final PlatformTransactionManager jpaTransactionManager;
    private final PlatformTransactionManager neo4jTransactionManager;

    public FriendshipService(
            FriendRequestRepository friendRequestRepository,
            UserRepository userRepository,
            UserNodeRepository userNodeRepository,
            FriendMapper friendMapper,
            UserService userService,
            @Qualifier("transactionManager") PlatformTransactionManager jpaTransactionManager,
            @Qualifier("neo4jTransactionManager") PlatformTransactionManager neo4jTransactionManager) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
        this.friendMapper = friendMapper;
        this.userService = userService;
        this.jpaTransactionManager = jpaTransactionManager;
        this.neo4jTransactionManager = neo4jTransactionManager;
    }

    @Transactional
    public void sendFriendRequest(UserEntity currentUser, Long addresseeId) {
        Long requesterId = currentUser.getId();
        UserEntity addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new ResourceNotFoundException(addresseeId, USER_RESOURCE_NAME));
        validateSending(requesterId, addresseeId);

        FriendRequestEntity friendRequest = new FriendRequestEntity();
        friendRequest.setRequester(currentUser);
        friendRequest.setAddressee(addressee);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    public void acceptFriendRequest(UserEntity currentUser, Long requestId) {
        Long currentUserId = currentUser.getId();

        // Execute JPA transaction first
        TransactionTemplate jpaTemplate = new TransactionTemplate(jpaTransactionManager);
        FriendRequestEntity savedRequest = jpaTemplate.execute(status -> {
            FriendRequestEntity friendRequest = findFriendRequestById(requestId);
            validateDefaultOperations(friendRequest, currentUserId);

            friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
            FriendRequestEntity saved = friendRequestRepository.save(friendRequest);
            friendRequestRepository.flush(); // Ensure it's persisted before moving to Neo4j
            return saved;
        });

        // Execute Neo4j transaction in separate transaction
        try {
            TransactionTemplate neo4jTemplate = new TransactionTemplate(neo4jTransactionManager);
            neo4jTemplate.executeWithoutResult(status -> {
                createFriendshipNodes(savedRequest);
            });
        } catch (Exception e) {
            log.error("Failed to create friendship in Neo4j for request {}. Attempting compensation...", requestId, e);

            // Compensating transaction: revert JPA change
            try {
                jpaTemplate.executeWithoutResult(status -> {
                    FriendRequestEntity friendRequest = findFriendRequestById(requestId);
                    friendRequest.setStatus(FriendRequestStatus.PENDING);
                    friendRequestRepository.save(friendRequest);
                });
                log.info("Successfully reverted friend request {} to PENDING status", requestId);
            } catch (Exception compensationError) {
                log.error("CRITICAL: Failed to compensate friend request {} after Neo4j failure. Manual intervention required.",
                         requestId, compensationError);
            }

            throw new RuntimeException("Failed to establish friendship relationship in graph database", e);
        }
    }

    @Transactional
    public void rejectFriendRequest(UserEntity currentUser, Long requestId) {
        Long currentUserId = currentUser.getId();
        FriendRequestEntity friendRequest = findFriendRequestById(requestId);
        validateDefaultOperations(friendRequest, currentUserId);

        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(friendRequest);
    }

    @Transactional
    public void cancelFriendRequest(UserEntity currentUser, Long requestId) {
        Long currentUserId = currentUser.getId();
        FriendRequestEntity friendRequest = findFriendRequestById(requestId);
        validateCanceling(friendRequest, currentUserId);

        friendRequest.setStatus(FriendRequestStatus.CANCELLED);
        friendRequestRepository.save(friendRequest);
    }

    public void unfriend(UserEntity currentUser, Long friendId) {
        Long currentUserId = currentUser.getId();
        validateUnfriend(currentUserId, friendId);

        // Delete from Neo4j first (supplementary data)
        TransactionTemplate neo4jTemplate = new TransactionTemplate(neo4jTransactionManager);
        neo4jTemplate.executeWithoutResult(status -> {
            userNodeRepository.deleteFriendship(currentUserId, friendId);
        });

        // Then update PostgreSQL (source of truth)
        // Note: This marks the relationship as terminated in the friend request table
        // If you want to also update the FriendRequestEntity status, add that logic here
    }

    @Transactional
    public List<FriendRequestResponseDto> getPendingFriendRequests(Long userId) {
        List<FriendRequestEntity> entities = friendRequestRepository.findByAddresseeIdAndStatus(userId, FriendRequestStatus.PENDING);
        return friendMapper.toFriendRequestResponseDtoList(entities);
    }

    @Transactional
    public List<FriendRequestResponseDto> getSentFriendRequests(Long userId) {
        List<FriendRequestEntity> entities =  friendRequestRepository.findByRequesterIdAndStatus(userId, FriendRequestStatus.PENDING);
        return friendMapper.toFriendRequestResponseDtoList(entities);
    }

    @Transactional
    public List<UserDto> getFriends(Long userId) {
        List<UserNode> userNodes = userNodeRepository.findFriendsByUserId(userId);
        return friendMapper.toUserDtoList(userNodes);
    }

    @Transactional
    public List<UserDto> getFriendSuggestions(Long userId) {
        List<UserNode> userNodes = userNodeRepository.findFriendsOfFriends(userId, 10);
        return friendMapper.toUserDtoList(userNodes);
    }

    private FriendRequestEntity findFriendRequestById(Long id) {
        return friendRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, FRIEND_REQUEST_RESOURCE_NAME));
    }

    private void validateSending(Long requesterId, Long addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new ValidationException("Cannot send friend request to yourself");
        }
        if (friendRequestRepository.areFriends(requesterId, addresseeId)) {
            throw new ValidationException("Users are already friends");
        }

        friendRequestRepository
            .findByIdAndStatus(requesterId, addresseeId, FriendRequestStatus.PENDING)
            .ifPresent(fr -> {
                throw new ValidationException("Friend request already sent");
            });
    }

    private void validateUnfriend(Long userId, Long friendId) {
        if (!friendRequestRepository.areFriends(userId, friendId)) {
            throw new ValidationException("Users are not friends");
        }
    }

    private void validateCanceling(FriendRequestEntity friendRequest, Long requesterId) {
        if (!friendRequest.getRequester().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not authorized to cancel this request");
        }
        validateFriendRequestPending(friendRequest);
    }

    private void validateDefaultOperations(FriendRequestEntity friendRequest, Long addresseeId) {
        if (!friendRequest.getAddressee().getId().equals(addresseeId)) {
            throw new ForbiddenException("You are not authorized to operate this request");
        }
        validateFriendRequestPending(friendRequest);
    }

    private void validateFriendRequestPending(FriendRequestEntity friendRequest) {
        if (!FriendRequestStatus.PENDING.equals(friendRequest.getStatus())) {
            throw new IllegalArgumentException("Friend request is not pending");
        }
    }

    private void createFriendshipNodes(FriendRequestEntity friendRequest) {
        userService.ensureUserNodeExists(friendRequest.getRequester());
        userService.ensureUserNodeExists(friendRequest.getAddressee());
        userNodeRepository.createFriendship(
                friendRequest.getRequester().getId(),
                friendRequest.getAddressee().getId()
        );
    }
}
