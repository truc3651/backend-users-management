package com.backend.users.mappers;

import org.mapstruct.Mapper;

import com.backend.core.dto.UserDto;
import com.backend.users.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(UserEntity user);
}
