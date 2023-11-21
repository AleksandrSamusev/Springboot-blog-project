package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.client.StatsClient;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.StatisticRecord;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticlePublicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePublicServiceImpl implements ArticlePublicService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final StatsClient statsClient;

    @Override
    public ArticleShortDto getArticleById(Long articleId, HttpServletRequest request) {
        Article article = checkArticleExist(articleId);
        checkArticleIsPublished(article);
        createRecordAndSave(request);
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
        checkUserExist(userId);
        PageRequest pageable = PageRequest.of(
                from / size, size, Sort.by("published").descending());

        return ArticleMapper.toListArticleShort(articleRepository.findAllByAuthorUserIdAndStatus(
                userId, ArticleStatus.PUBLISHED, pageable));
    }

    @Override
    public ArticleShortDto likeArticle(Long articleId) {
        checkArticleExist(articleId);
        checkArticleIsPublished(articleRepository.getReferenceById(articleId));
        Article article = articleRepository.getReferenceById(articleId);
        Long likes = article.getLikes();
        likes++;
        article.setLikes(likes);
        Article savedArticle = articleRepository.save(article);
        log.info("Add like to article with ID = " + articleId);
        return ArticleMapper.toArticleShortDto(savedArticle);
    }

    @Override
    public List<ArticleShortDto> getAllArticlesByTag(Long tagId) {
        isTagExists(tagId);
        return tagRepository.getReferenceById(tagId).getArticles()
                .stream()
                .filter((x) -> x.getPublished() != null)
                .map(ArticleMapper::toArticleShortDto)
                .sorted(Comparator.comparing(ArticleShortDto::getPublished))
                .collect(Collectors.toList());
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

    private void isTagExists(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag with given ID = " + tagId + " not found");
        }
    }

    private User checkUserExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("User with id %d wasn't found", userId));
        }
        return user.get();
    }

    private void createRecordAndSave(HttpServletRequest request) {
        StatisticRecord newRecord = new StatisticRecord(
                null,
                "mainApp",
                request.getRemoteAddr(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        statsClient.addStats(newRecord);
    }
}
