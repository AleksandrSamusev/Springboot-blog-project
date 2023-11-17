package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.ArticleMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.services.ArticlePublicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePublicServiceImpl implements ArticlePublicService {
    private final ArticleRepository articleRepository;

    @Override
    public ArticleShortDto getArticleById(Long articleId) {
        Article article = checkArticleExist(articleId);
        checkArticleIsPublished(article);
        log.info("Return article with ID = " + articleId);
        return ArticleMapper.toArticleShortDto(article);
    }

    @Override
    public List<ArticleShortDto> getAllArticles() {
        log.info("Return a list od all articles");
        return ArticleMapper.toListArticleShort(articleRepository.findArticlesByStatusOrderByPublishedDesc(
                ArticleStatus.PUBLISHED));
    }

    private Article checkArticleExist(Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            log.error("Article with id {} wasn't found", articleId);
            throw new ResourceNotFoundException(String.format("Article with id %d wasn't found", articleId));
        }
        return article.get();
    }

    private void checkArticleIsPublished(Article article) {
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            log.error("Article with id {} is not published yet. Current status is {}", article.getArticleId(),
                    article.getStatus());
            throw new ActionForbiddenException(String.format("Article with id %d is not published yet",
                    article.getArticleId()));
        }
    }
}
