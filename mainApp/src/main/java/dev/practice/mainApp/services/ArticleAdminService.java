package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.article.ArticleFullDto;

import java.util.List;

public interface ArticleAdminService {
    List<ArticleFullDto> getAllArticlesByUserId(String login, Long authorId, Integer from, Integer size, String status);

    ArticleFullDto publishArticle(String login, Long articleId, boolean publish);
}
