package dev.practice.blogproject.dtos.user;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.models.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto {

    private Long userId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private Role role;
    private String about;

    @NotNull
    private Boolean isBanned;
    private Set<MessageFullDto> sentMessages = new HashSet<>();
    private Set<MessageFullDto> receivedMessages = new HashSet<>();
    private Set<ArticleShortDto> articles = new HashSet<>();
    private Set<CommentShortDto> comments = new HashSet<>();
}
