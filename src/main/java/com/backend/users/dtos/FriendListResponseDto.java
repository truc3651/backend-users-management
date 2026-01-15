package com.backend.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponseDto {
    private String name;
    private Long count;
    private List<UserDto> friends;
}
