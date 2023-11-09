package dev.practice.blogproject.dtos;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private String comment;
    private LocalDateTime created = LocalDateTime.now();
    private Article article;
    private User user;
}
