package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.ArticleStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findArticlesByTitleIgnoreCase(String title);

    List<Article> findAllByStatusOrderByPublishedDesc(ArticleStatus status, PageRequest pageable);

    List<Article> findAllByAuthorUserIdAndStatus(Long userId, ArticleStatus status, PageRequest pageable);

    List<Article> findAllByAuthorUserId(Long userId, PageRequest pageable);
}
