package dev.practice.blogproject.dtos;

import dev.practice.blogproject.models.ArticleStatus;
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

    private Long articleId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private UserShortDto author;
    private LocalDateTime created;
    private LocalDateTime published;
    private ArticleStatus status;
    private Long likes;
    private Set<CommentFullDto> comments;
    private Set<TagShortDto> tags;
}
