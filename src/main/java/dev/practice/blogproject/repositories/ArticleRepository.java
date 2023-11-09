package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
