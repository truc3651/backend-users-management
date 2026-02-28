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
public class UnfollowEventDto extends UserEventDto {
  private Long followedId;

  public UnfollowEventDto(Long userId, Long followedId) {
    super(userId);
    this.followedId = followedId;
  }
}
