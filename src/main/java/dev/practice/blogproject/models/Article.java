package dev.practice.blogproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "articles")
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "title")
    @NotNull
    @Length(max = 200)
    private String title;

    @Column(name = "content")
    @NotNull
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "published")
    private LocalDateTime published;

    @Enumerated(EnumType.STRING)
    private ArticleStatuses status = ArticleStatuses.CREATED;

    @Column(name = "likes")
    private Long likes;
}
