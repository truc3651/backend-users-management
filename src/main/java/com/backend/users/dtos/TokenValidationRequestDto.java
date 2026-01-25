package com.backend.users.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenValidationRequestDto {
  @NotBlank private String token;
}
