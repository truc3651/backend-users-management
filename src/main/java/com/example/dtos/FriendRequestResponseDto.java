package com.example.dtos;

import com.example.enums.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponseDto {
    private Long id;
    private Long addresseeId;
    private String addresseeEmail;
    private FriendRequestStatus status;
}
