package dev.practice.blogproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "comments")
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "comment")
    @NotBlank
    @Length(max = 500)
    private String comment;

    @Column(name = "created")
    private LocalDateTime created;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentAuthor;
}
