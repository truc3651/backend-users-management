package com.backend.users.entities;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import com.backend.users.enums.FriendRequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("t_friend_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestEntity {
  @Id private Long id;
  private Long requesterId;
  private Long addresseeId;
  private FriendRequestStatus status;

  @CreatedDate private OffsetDateTime createdAt;

  @LastModifiedDate private OffsetDateTime updatedAt;
}
