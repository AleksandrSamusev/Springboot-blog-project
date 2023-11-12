package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;

public interface ArticlePrivateService {
    ArticleFullDto createArticle(long userId, ArticleNewDto newArticle);

    ArticleFullDto updateArticle(long userId, long articleId, ArticleUpdateDto updateArticle);

    ArticleFullDto getArticleById(long userId, long articleId);

    void deleteArticle(long userId, long articleId);
}
