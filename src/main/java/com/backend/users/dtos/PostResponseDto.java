package com.backend.users.dtos;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
  private Long id;
  private Long userId;
  private String userEmail;
  private String content;
  private OffsetDateTime createdAt;
}
