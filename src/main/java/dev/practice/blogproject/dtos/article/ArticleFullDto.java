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

    @NotNull
    private Long articleId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private UserShortDto author;

    @NotNull
    private LocalDateTime created;
    private LocalDateTime published;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;
    private Long likes;
    private Set<CommentFullDto> comments;
    private Set<TagShortDto> tags;

}
