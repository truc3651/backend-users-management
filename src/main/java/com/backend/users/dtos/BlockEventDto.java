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
public class BlockEventDto extends UserEventDto {
  private Long blockedId;

  public BlockEventDto(Long userId, Long blockedId) {
    super(userId);
    this.blockedId = blockedId;
  }
}
