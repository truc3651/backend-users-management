package com.backend.users.entities;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.users.enums.FriendRequestStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_friend_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
    name = "friend_request_sequence",
    sequenceName = "users.q_friend_requests_id",
    allocationSize = 10)
public class FriendRequestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_request_sequence")
  private Long id;

  @OneToOne
  @JoinColumn(name = "requester_id")
  private UserEntity requester;

  @OneToOne
  @JoinColumn(name = "addressee_id")
  private UserEntity addressee;

  @Enumerated(EnumType.STRING)
  private FriendRequestStatus status = FriendRequestStatus.PENDING;

  @CreationTimestamp private OffsetDateTime createdAt;

  @UpdateTimestamp private OffsetDateTime updatedAt;
}
