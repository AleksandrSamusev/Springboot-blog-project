package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.services.ArticlePublicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePublicServiceImpl implements ArticlePublicService {
    private final ArticleRepository articleRepository;

    @Override
    public ArticleShortDto getArticleById(Long articleId) {
        return null;
    }

    @Override
    public List<ArticleShortDto> getAllArticles() {
        return null;
    }
}
