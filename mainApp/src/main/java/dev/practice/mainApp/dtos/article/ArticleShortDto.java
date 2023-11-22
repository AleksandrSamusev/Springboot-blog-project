package dev.practice.mainApp.dtos.article;

import dev.practice.mainApp.dtos.comment.CommentShortDto;
import dev.practice.mainApp.dtos.tag.TagShortDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
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

    @NotNull(message = "Article ID cannot be null")
    private Long articleId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotNull(message = "Author cannot be null")
    private UserShortDto author;
    private LocalDateTime published;
    private Long likes;
    private Long views;
    private Set<CommentShortDto> comments;
    private Set<TagShortDto> tags;

}
