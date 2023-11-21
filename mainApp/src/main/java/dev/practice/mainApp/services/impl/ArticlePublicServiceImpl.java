package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.services.ArticlePublicService;
import dev.practice.mainApp.utils.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePublicServiceImpl implements ArticlePublicService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final Validations validations;

    @Override
    public ArticleShortDto getArticleById(Long articleId) {
        Article article = validations.checkArticleExist(articleId);
        validations.checkArticleIsPublished(article);
        log.info("Return article with ID = " + articleId);
        return ArticleMapper.toArticleShortDto(article);
    }

    @Override
    public List<ArticleShortDto> getAllArticles(Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return ArticleMapper.toListArticleShort(articleRepository.findAllByStatusOrderByPublishedDesc(
                ArticleStatus.PUBLISHED, pageable));
    }

    @Override
    public List<ArticleShortDto> getAllArticlesByUserId(Long userId, Integer from, Integer size) {
        validations.checkUserExist(userId);
        PageRequest pageable = PageRequest.of(
                from / size, size, Sort.by("published").descending());

        return ArticleMapper.toListArticleShort(articleRepository.findAllByAuthorUserIdAndStatus(
                userId, ArticleStatus.PUBLISHED, pageable));
    }

    @Override
    public ArticleShortDto likeArticle(Long articleId) {
        Article article = validations.checkArticleExist(articleId);
        validations.checkArticleIsPublished(article);
        Long likes = article.getLikes();
        likes++;
        article.setLikes(likes);
        Article savedArticle = articleRepository.save(article);
        log.info("Add like to article with ID = " + articleId);
        return ArticleMapper.toArticleShortDto(savedArticle);
    }

    @Override
    public List<ArticleShortDto> getAllArticlesByTag(Long tagId) {
        validations.isTagExists(tagId);
        return tagRepository.getReferenceById(tagId).getArticles()
                .stream()
                .filter((x) -> x.getPublished() != null)
                .map(ArticleMapper::toArticleShortDto)
                .sorted(Comparator.comparing(ArticleShortDto::getPublished))
                .collect(Collectors.toList());
    }
}
