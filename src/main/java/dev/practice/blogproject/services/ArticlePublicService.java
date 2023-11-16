package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.article.ArticleShortDto;

import java.util.List;

public interface ArticlePublicService {
    ArticleShortDto getArticleById(Long articleId);

    List<ArticleShortDto> getAllArticles();
}
