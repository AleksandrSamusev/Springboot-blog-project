package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.article.ArticleFullDto;

import java.util.List;

public interface ArticleAdminService {
    List<ArticleFullDto> getAllArticlesByUserId(Long userId, Long authorId, Integer from, Integer size, String status);

    ArticleFullDto publishArticle(Long userId, Long articleId, boolean publish);
}
