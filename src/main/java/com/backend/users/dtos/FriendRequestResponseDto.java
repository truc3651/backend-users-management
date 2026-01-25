package com.backend.users.dtos;

import com.backend.users.enums.FriendRequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponseDto {
  private Long id;
  private Long addresseeId;
  private String addresseeEmail;
  private FriendRequestStatus status;
}
