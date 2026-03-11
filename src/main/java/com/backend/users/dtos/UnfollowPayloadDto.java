package com.backend.users.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class UnfollowPayloadDto extends UserPayloadDto {
  private Long followedId;

  public UnfollowPayloadDto(Long userId, Long followedId) {
    super(userId);
    this.followedId = followedId;
  }
}
