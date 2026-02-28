package com.backend.users.dtos;

import java.time.OffsetDateTime;

import com.backend.users.enums.FriendRequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponseDto {
  private Long id;
  private Long requesterId;
  private Long addresseeId;
  private FriendRequestStatus status;
  private OffsetDateTime createdAt;
}
