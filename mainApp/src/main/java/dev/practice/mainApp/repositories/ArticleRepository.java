package dev.practice.mainApp.repositories;

import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findArticlesByTitleIgnoreCase(String title);

    List<Article> findAllByStatusOrderByPublishedDesc(ArticleStatus status, PageRequest pageable);

    List<Article> findAllByAuthorUsernameAndStatus(String username, ArticleStatus status, PageRequest pageable);

    List<Article> findAllByAuthorUsername(String username, PageRequest pageable);
}
