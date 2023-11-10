package dev.practice.blogproject.dtos.comment;

import dev.practice.blogproject.dtos.user.UserShortDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {

    private Long commentId;

    @NotBlank
    private String comment;
    private LocalDateTime created = LocalDateTime.now();

    @NotNull
    private Long articleId;

    @NotNull
    private UserShortDto commentAuthor;
}
