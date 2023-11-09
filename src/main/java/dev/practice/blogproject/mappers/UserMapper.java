package dev.practice.blogproject.mappers;


import dev.practice.blogproject.dtos.UserDto;
import dev.practice.blogproject.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAbout(user.getAbout());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setBirthdate(user.getBirthdate());
        dto.setIsBanned(user.getIsBanned());
        dto.setComments(user.getComments());
        dto.setSentMessages(user.getSentMessages());
        dto.setReceivedMessages(user.getReceivedMessages());
        dto.setArticles(user.getArticles());
        return dto;
    }

    public static User toUser(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAbout(dto.getAbout());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setBirthdate(dto.getBirthdate());
        user.setIsBanned(dto.getIsBanned());
        user.setComments(dto.getComments());
        user.setSentMessages(dto.getSentMessages());
        user.setReceivedMessages(dto.getReceivedMessages());
        user.setArticles(dto.getArticles());
        return user;
    }

    public static List<User> toUsers(List<UserDto> dtos) {
        return dtos.stream().map(UserMapper::toUser).collect(Collectors.toList());
    }

    public static List<UserDto> toDtos(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
