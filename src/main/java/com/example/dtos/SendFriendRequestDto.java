package com.example.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendFriendRequestDto {
    @NotNull
    private Long addresseeId;
}
