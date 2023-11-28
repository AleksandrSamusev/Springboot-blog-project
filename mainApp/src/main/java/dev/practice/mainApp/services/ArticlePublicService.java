package dev.practice.mainApp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ArticlePublicService {
    ArticleShortDto getArticleById(Long articleId, HttpServletRequest request) throws JsonProcessingException;

    List<ArticleShortDto> getAllArticles(Integer from, Integer size);

    List<ArticleShortDto> getAllArticlesByUserId(Long userId, Integer from, Integer size);

    ArticleShortDto likeArticle(Long articleId);

    List<ArticleShortDto> getAllArticlesByTag(Long tagId);
}
