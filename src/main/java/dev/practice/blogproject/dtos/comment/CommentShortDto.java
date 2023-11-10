package dev.practice.blogproject.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {

    private Long commentId;

    @NotBlank
    private String comment;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private Long articleId;

    @NotNull
    private Long commentAuthorId;
}
