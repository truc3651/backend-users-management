package com.backend.users.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.users.entities.FriendRequestEntity;
import com.backend.users.enums.FriendRequestStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FriendRequestRepository extends R2dbcRepository<FriendRequestEntity, Long> {
  @Query(
      """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM t_friend_requests fr
        WHERE (
            (fr.requester_id = :requesterId AND fr.addressee_id = :addresseeId)
            OR
            (fr.requester_id = :addresseeId AND fr.addressee_id = :requesterId)
        )
        AND fr.status = 'ACCEPTED'
    """)
  Mono<Boolean> areFriends(
      @Param("requesterId") Long requesterId, @Param("addresseeId") Long addresseeId);

  @Query(
      """
        SELECT * FROM t_friend_requests fr
        WHERE fr.status = :status
        AND (
            (fr.requester_id = :requesterId AND fr.addressee_id = :addresseeId)
            OR
            (fr.requester_id = :addresseeId AND fr.addressee_id = :requesterId)
        )
    """)
  Mono<FriendRequestEntity> findByIdAndStatus(
      @Param("requesterId") Long requesterId,
      @Param("addresseeId") Long addresseeId,
      @Param("status") String status);

  Flux<FriendRequestEntity> findByAddresseeIdAndStatus(
      Long addresseeId, FriendRequestStatus status);

  Flux<FriendRequestEntity> findByRequesterIdAndStatus(
      Long requesterId, FriendRequestStatus status);
}
