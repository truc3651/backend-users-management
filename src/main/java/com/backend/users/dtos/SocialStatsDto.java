package com.backend.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialStatsDto {
  private Long userId;
  private long friendsCount;
  private long followersCount;
  private long followingCount;
  private boolean isFollowingYou;
  private boolean isFollowedByYou;
  private boolean isFriend;
  private boolean isBlocked;
  private boolean isRestricted;
}
