package com.backend.users.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

import com.backend.core.web.page.Page;
import com.backend.users.dtos.FriendRequestResponseDto;
import com.backend.users.dtos.UserDto;
import com.backend.users.entities.FriendRequestEntity;
import com.backend.users.graph.UserNode;

@Mapper(componentModel = "spring")
public interface FriendMapper {
  FriendRequestResponseDto toFriendRequestResponseDto(FriendRequestEntity entity);

  UserDto toUserDto(UserNode userNode);

  default Page<UserDto> toUserDtoPage(List<UserDto> items, Long totalElements) {
    return Page.<UserDto>builder().items(items).totalElements(totalElements).build();
  }
}
