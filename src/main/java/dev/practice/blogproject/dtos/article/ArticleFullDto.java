package dev.practice.blogproject.dtos.article;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.models.ArticleStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleFullDto {

    @NotNull(message = "Article ID cannot be null")
    private Long articleId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotNull(message = "Author cannot be null")
    private UserShortDto author;

    @NotNull(message = "Created parameter cannot be null")
    private LocalDateTime created;
    private LocalDateTime published;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;
    private Long likes;
    private Set<CommentFullDto> comments;
    private Set<TagShortDto> tags;

}
