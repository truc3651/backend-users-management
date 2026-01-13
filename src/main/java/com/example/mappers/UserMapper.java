package com.example.mappers;

import org.mapstruct.Mapper;

import com.example.dtos.UserDto;
import com.example.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity user);
}
