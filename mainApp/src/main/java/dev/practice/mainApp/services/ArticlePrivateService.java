package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ArticlePrivateService {
    ArticleFullDto createArticle(long userId, ArticleNewDto newArticle);

    ArticleFullDto updateArticle(Long userId, Long articleId, ArticleUpdateDto updateArticle);

    Optional<?> getArticleById(Long userId, Long articleId);

    void deleteArticle(long userId, long articleId);

    List<ArticleFullDto> getAllArticlesByUserId(Long userId, Integer from, Integer size, String status);

    ArticleFullDto publishArticle(Long userId, Long articleId);
}
