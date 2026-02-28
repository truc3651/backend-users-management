package com.backend.users.dtos;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
  private int code;
  private String error;
  private String message;
  private String path;
  private List<Object> details;
  @Builder.Default private OffsetDateTime timestamp = OffsetDateTime.now();
}
