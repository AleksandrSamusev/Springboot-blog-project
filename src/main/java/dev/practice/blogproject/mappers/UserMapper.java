package dev.practice.blogproject.mappers;


import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserFullDto toUserDto(User user) {
        UserFullDto dto = new UserFullDto();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAbout(user.getAbout());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setBirthDate(user.getBirthDate());
        dto.setRole(user.getRole());
        dto.setIsBanned(user.getIsBanned());
        dto.setComments(user.getComments().stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toSet()));
        dto.setSentMessages(user.getSentMessages().stream().map(MessageMapper::toMessageShortDto)
                .collect(Collectors.toSet()));
        dto.setReceivedMessages(user.getReceivedMessages().stream().map(MessageMapper::toMessageShortDto)
                .collect(Collectors.toSet()));
        dto.setArticles(user.getArticles().stream().map(ArticleMapper::toArticleShortDto).collect(Collectors.toSet()));
        return dto;
    }

    public static User toUser(UserFullDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAbout(dto.getAbout());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setBirthDate(dto.getBirthDate());
        user.setRole(dto.getRole());
        user.setIsBanned(dto.getIsBanned());
        dto.setComments(user.getComments().stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toSet()));
        dto.setSentMessages(user.getSentMessages().stream().map(MessageMapper::toMessageShortDto)
                .collect(Collectors.toSet()));
        dto.setReceivedMessages(user.getReceivedMessages().stream().map(MessageMapper::toMessageShortDto)
                .collect(Collectors.toSet()));
        dto.setArticles(user.getArticles().stream().map(ArticleMapper::toArticleShortDto).collect(Collectors.toSet()));
        return user;
    }


    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getUserId(),
                user.getUsername());
    }

    public static List<User> toUsers(List<UserFullDto> dtos) {
        return dtos.stream().map(UserMapper::toUser).collect(Collectors.toList());
    }

    public static List<UserFullDto> toDtos(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
