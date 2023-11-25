package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ArticlePrivateService {
    ArticleFullDto createArticle(String username, ArticleNewDto newArticle);

    ArticleFullDto updateArticle(String username, Long articleId, ArticleUpdateDto updateArticle);

    Optional<?> getArticleById(String username, Long articleId);

    void deleteArticle(String username, Long articleId);

    List<ArticleFullDto> getAllArticlesByUserId(String username, Integer from, Integer size, String status);

    ArticleFullDto publishArticle(String username, Long articleId);
}
