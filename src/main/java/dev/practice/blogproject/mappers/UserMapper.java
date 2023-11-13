package dev.practice.blogproject.mappers;


import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserFullDto toUserFullDto(User user) {
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
        dto.setSentMessages(user.getSentMessages().stream().map(MessageMapper::toMessageFullDto)
                .collect(Collectors.toSet()));
        dto.setReceivedMessages(user.getReceivedMessages().stream().map(MessageMapper::toMessageFullDto)
                .collect(Collectors.toSet()));
        dto.setArticles(user.getArticles().stream().map(ArticleMapper::toArticleShortDto).collect(Collectors.toSet()));
        return dto;
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getUserId(),
                user.getUsername());
    }

    public static User toUser(UserNewDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setBirthDate(dto.getBirthDate());
        user.setAbout(dto.getAbout());
        user.setSentMessages(new HashSet<>());
        user.setReceivedMessages(new HashSet<>());
        user.setComments(new HashSet<>());
        user.setArticles(new HashSet<>());
        return user;
    }
}
