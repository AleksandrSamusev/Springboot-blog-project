package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;

import java.util.Optional;

public interface ArticlePrivateService {
    ArticleFullDto createArticle(long userId, ArticleNewDto newArticle);

    ArticleFullDto updateArticle(Long userId, Long articleId, ArticleUpdateDto updateArticle);

    Optional<?> getArticleById(Long userId, Long articleId);

    void deleteArticle(long userId, long articleId);
}
