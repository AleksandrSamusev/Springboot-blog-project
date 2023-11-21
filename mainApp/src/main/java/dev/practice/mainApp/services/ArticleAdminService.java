package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.article.ArticleFullDto;

import java.util.List;

public interface ArticleAdminService {
    List<ArticleFullDto> getAllArticlesByUserId(Long userId, Long authorId, Integer from, Integer size, String status);

    ArticleFullDto publishArticle(Long userId, Long articleId, boolean publish);
}
