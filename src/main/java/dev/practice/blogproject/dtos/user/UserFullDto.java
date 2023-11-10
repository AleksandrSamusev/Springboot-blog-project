package dev.practice.blogproject.dtos.user;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.dtos.message.MessageShortDto;
import dev.practice.blogproject.models.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    @Email
    private String email;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotNull
    private Role role;
    private String about;

    @NotNull
    private Boolean isBanned = false;
    private Set<MessageShortDto> sentMessages;
    private Set<MessageShortDto> receivedMessages;
    private Set<ArticleShortDto> articles;
    private Set<CommentShortDto> comments;
}
