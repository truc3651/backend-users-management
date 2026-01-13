package com.example.mappers;

import com.example.dtos.FriendListResponseDto;
import com.example.dtos.FriendRequestResponseDto;
import com.example.dtos.Page;
import com.example.dtos.UserDto;
import com.example.entities.FriendRequestEntity;
import com.example.graph.UserNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendMapper {
    @Mapping(source = "addressee.id", target = "addresseeId")
    @Mapping(source = "addressee.email", target = "addresseeEmail")
    FriendRequestResponseDto toFriendRequestResponseDto(FriendRequestEntity entity);

    List<FriendRequestResponseDto> toFriendRequestResponseDtoList(List<FriendRequestEntity> entities);

    UserDto toUserDto(UserNode userNode);

    List<UserDto> toUserDtoList(List<UserNode> userNodes);

    default Page<UserDto> toUserDtoPage(org.springframework.data.domain.Page<UserNode> page) {
        return Page.<UserDto>builder()
                .items(toUserDtoList(page.getContent()))
                .totalElements(page.getTotalElements())
                .build();
    }

    default FriendListResponseDto fromUserNodeEntityToDto(List<UserNode> friends) {
        FriendListResponseDto dto = new FriendListResponseDto();
        dto.setFriends(toUserDtoList(friends));
        dto.setCount((long) friends.size());
        return dto;
    }
}
