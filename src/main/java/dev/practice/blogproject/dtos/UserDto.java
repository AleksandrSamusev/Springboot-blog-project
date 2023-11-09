package dev.practice.blogproject.dtos;

import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Comment;
import dev.practice.blogproject.models.Message;
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
public class UserDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDate birthdate;
    private String about;
    private Boolean isBanned;
    private Set<Message> sentMessages;
    private Set<Message> receivedMessages;
    private Set<Article> articles;
    private Set<Comment> comments;
}
