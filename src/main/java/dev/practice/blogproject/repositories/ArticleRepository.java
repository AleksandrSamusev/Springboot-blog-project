package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findArticlesByTitleIgnoreCase(String title);
    List<Article> findArticlesByStatusOrderByPublishedDesc(ArticleStatus status);
}
