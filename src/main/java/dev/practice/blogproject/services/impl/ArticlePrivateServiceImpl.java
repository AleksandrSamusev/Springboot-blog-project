package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePrivateServiceImpl implements ArticlePrivateService {
    private final ArticleRepository articleRepository;

    @Override
    public ArticleFullDto createArticle(long userId, ArticleNewDto newArticle) {
        return null;
    }

    @Override
    public ArticleFullDto updateArticle(long userId, long articleId, ArticleUpdateDto updateArticle) {
        return null;
    }

    @Override
    public ArticleFullDto getArticleById(long userId, long articleId) {
        return null;
    }

    @Override
    public void deleteArticle(long userId, long articleId) {

    }
}
