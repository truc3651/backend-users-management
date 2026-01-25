package com.backend.users.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponseDto {
  private String name;
  private Long count;
  private List<UserDto> friends;
}
