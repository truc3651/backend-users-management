package com.backend.users.mappers;

import com.backend.core.web.page.Page;
import com.backend.users.dtos.UserDto;
import com.backend.users.dtos.FriendListResponseDto;
import com.backend.users.dtos.FriendRequestResponseDto;
import com.backend.users.entities.FriendRequestEntity;
import com.backend.users.graph.UserNode;
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
