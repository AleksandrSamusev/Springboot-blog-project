package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.article.ArticleShortDto;

import java.util.List;

public interface ArticlePublicService {
    ArticleShortDto getArticleById(Long articleId);

    List<ArticleShortDto> getAllArticles(Integer from, Integer size);

    List<ArticleShortDto> getAllArticlesByUserId(Long userId, Integer from, Integer size);

    ArticleShortDto likeArticle(Long articleId);

    List<ArticleShortDto> getAllArticlesByTag(Long tagId);
}
