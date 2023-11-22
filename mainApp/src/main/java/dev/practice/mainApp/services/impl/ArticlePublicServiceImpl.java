package dev.practice.mainApp.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.practice.mainApp.client.StatsClient;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.StatisticRecord;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.services.ArticlePublicService;
import dev.practice.mainApp.utils.Validations;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePublicServiceImpl implements ArticlePublicService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final StatsClient statsClient;
    private final Validations validations;

    @Override
    public ArticleShortDto getArticleById(Long articleId, HttpServletRequest request) {
        Article article = validations.checkArticleExist(articleId);
        validations.checkArticleIsPublished(article);

        createRecordAndSave(request);

        List<StatisticRecord> responses = sendRequestToStatistic(statsClient,
                List.of(String.format("/api/v1/public/articles/%s",
                        articleId)));

        Article savedArticle = setViewsToArticleAndSave(responses, article);
        log.info("Return article with ID = " + articleId);

        return ArticleMapper.toArticleShortDto(savedArticle);
    }

    @Override
    public List<ArticleShortDto> getAllArticles(Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);

        List<Article> articles = articleRepository.findAllByStatusOrderByPublishedDesc(
                ArticleStatus.PUBLISHED, pageable);

        List<String> uris = createListOfUris(articles);
        List<StatisticRecord> responses = sendRequestToStatistic(statsClient, uris);
        List<Article> savedArticles = setViewsToArticlesAndSave(responses, articles);

        return ArticleMapper.toListArticleShort(savedArticles);
    }


    @Override
    public List<ArticleShortDto> getAllArticlesByUserId(Long userId, Integer from, Integer size) {
        validations.checkUserExist(userId);
        PageRequest pageable = PageRequest.of(
                from / size, size, Sort.by("published").descending());

        List<Article> articles = articleRepository.findAllByAuthorUserIdAndStatus(
                userId, ArticleStatus.PUBLISHED, pageable);

        List<String> uris = createListOfUris(articles);
        List<StatisticRecord> responses = sendRequestToStatistic(statsClient, uris);
        List<Article> savedArticles = setViewsToArticlesAndSave(responses, articles);

        return ArticleMapper.toListArticleShort(savedArticles);
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

        List<Article> articles = tagRepository.getReferenceById(tagId).getArticles()
                .stream().filter((x) -> x.getPublished() != null).toList();

        List<String> uris = createListOfUris(articles);
        List<StatisticRecord> responses = sendRequestToStatistic(statsClient, uris);
        List<Article> savedArticles = setViewsToArticlesAndSave(responses, articles);

        return savedArticles.stream()
                .map(ArticleMapper::toArticleShortDto)
                .sorted(Comparator.comparing(ArticleShortDto::getPublished))
                .collect(Collectors.toList());
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

    private List<String> createListOfUris(List<Article> articles) {
        List<String> uris = new ArrayList<>();
        articles.forEach(e -> {
            uris.add(String.format("/api/v1/public/articles/%s", e.getArticleId()));
        });
        return uris;
    }

    private List<StatisticRecord> sendRequestToStatistic(StatsClient client, List<String> uris) {
        List<StatisticRecord> responses;
        try {
            responses = statsClient.getStats(LocalDateTime.now().minusYears(100),
                    LocalDateTime.now(), uris);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        return responses;
    }

    private List<Article> setViewsToArticlesAndSave(List<StatisticRecord> responses, List<Article> articles) {
        Map<Long, Long> count = new HashMap<>();
        responses.forEach(e -> {
            String[] arr = e.getUri().split("/");
            Long id = Long.parseLong(arr[arr.length - 1]);

            if (!count.containsKey(id)) {
                count.put(id, 1L);
            } else {
                count.put(id, count.get(id) + 1);
            }

        });
        articles.forEach(e -> {
            e.setViews(count.getOrDefault(e.getArticleId(), 0L));
        });
        return articleRepository.saveAll(articles);
    }

    private Article setViewsToArticleAndSave(List<StatisticRecord> responses, Article article) {
        if (responses.isEmpty()) {
            article.setViews(0L);
        } else {
            article.setViews((long) responses.size());
        }
        return articleRepository.save(article);
    }
}