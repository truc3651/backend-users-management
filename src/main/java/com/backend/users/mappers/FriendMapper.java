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

  default FriendRequestResponseDto toFriendRequestResponseDto(FriendRequestEntity entity) {
    FriendRequestResponseDto dto = new FriendRequestResponseDto();
    dto.setId(entity.getId());
    dto.setRequesterId(entity.getRequesterId());
    dto.setAddresseeId(entity.getAddresseeId());
    dto.setStatus(entity.getStatus());
    dto.setCreatedAt(entity.getCreatedAt());
    return dto;
  }

  UserDto toUserDto(UserNode userNode);

  default Page<UserDto> toUserDtoPage(List<UserDto> items, Long totalElements, Pageable pageable) {
    return Page.<UserDto>builder().items(items).totalElements(totalElements).build();
  }
}
