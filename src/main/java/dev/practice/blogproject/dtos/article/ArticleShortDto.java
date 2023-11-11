package dev.practice.blogproject.dtos.article;

import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
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
@NoArgsConstructor
@AllArgsConstructor
public class ArticleShortDto {

    @NotNull
    private Long articleId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private UserShortDto author;
    private LocalDateTime published;
    private Long likes;
    private Set<CommentShortDto> comments;
    private Set<TagShortDto> tags;

}
