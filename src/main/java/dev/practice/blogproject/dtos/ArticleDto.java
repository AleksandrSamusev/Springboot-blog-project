package dev.practice.blogproject.dtos;
import dev.practice.blogproject.models.ArticleStatuses;
import dev.practice.blogproject.models.Comment;
import dev.practice.blogproject.models.Tag;
import dev.practice.blogproject.models.User;
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
public class ArticleDto {
    private Long articleId;
    private String title;
    private String content;
    private User author;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime published;
    private ArticleStatuses status = ArticleStatuses.CREATED;
    private Long likes;
    private Set<Comment> comments;
    private Set<Tag> tags; //
}
