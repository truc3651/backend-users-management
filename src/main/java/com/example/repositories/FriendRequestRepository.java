package com.example.repositories;

import com.example.entities.FriendRequestEntity;
import com.example.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END
        FROM FriendRequestEntity fr
        WHERE (
            (fr.requester.id = :requesterId AND fr.addressee.id = :addresseeId)
            OR
            (fr.requester.id = :addresseeId AND fr.addressee.id = :requesterId)
        )
        AND fr.status = com.example.enums.FriendRequestStatus.ACCEPTED
    """)
    boolean areFriends(
            @Param("requesterId") Long requesterId,
            @Param("addresseeId") Long addresseeId);

    @Query("""
        SELECT fr FROM FriendRequestEntity fr
        WHERE fr.status = :status
        AND (
            (fr.requester.id = :requesterId AND fr.addressee.id = :addresseeId)
            OR
            (fr.requester.id = :addresseeId AND fr.addressee.id = :requesterId)
        )
    """)
    Optional<FriendRequestEntity> findByIdAndStatus(Long requesterId, Long addresseeId, FriendRequestStatus status);

    List<FriendRequestEntity> findByAddresseeIdAndStatus(Long addresseeId, FriendRequestStatus status);

    List<FriendRequestEntity> findByRequesterIdAndStatus(Long requesterId, FriendRequestStatus status);
}
