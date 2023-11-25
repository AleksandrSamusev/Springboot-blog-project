package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.Tag;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.CommentRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticlePrivateService;
import dev.practice.mainApp.services.TagService;
import dev.practice.mainApp.utils.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePrivateServiceImpl implements ArticlePrivateService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final CommentRepository commentRepository;
    private final Validations validations;

    @Override
    public ArticleFullDto createArticle(String username, ArticleNewDto newArticle) {
        User user = userRepository.findByUsername(username);
        validations.checkUserIsNotBanned(user);
        validations.checkTitleNotExist(newArticle.getTitle(), null);

        Article savedArticle = articleRepository.save(ArticleMapper.toArticleFromNew(newArticle, user));
        if (newArticle.getTags() != null && !newArticle.getTags().isEmpty()) {
            ArticleFullDto articleWithTags = tagService.addTagsToArticle(
                    user.getUserId(), savedArticle.getArticleId(), newArticle.getTags().stream().toList());

            user.getArticles().add(savedArticle);
            userRepository.save(user);

            log.info("Article with id {} was created by user with id {}",
                    articleWithTags.getArticleId(), user.getUserId());
            return articleWithTags;
        }

        user.getArticles().add(savedArticle);
        userRepository.save(user);

        log.info("Article with id {} was created by user with id {}", savedArticle.getArticleId(), user.getUserId());
        return ArticleMapper.toArticleFullDto(savedArticle);
    }

    @Override
    public ArticleFullDto updateArticle(String username, Long articleId, ArticleUpdateDto updateArticle) {
        Article article = validations.checkArticleExist(articleId);
        validations.checkUserIsAuthor(article, username);
        if (updateArticle.getTitle() != null && !updateArticle.getTitle().trim().isBlank()) {
            validations.checkTitleNotExist(updateArticle.getTitle(), articleId);
        }
        if (!article.getComments().isEmpty()) {
            commentRepository.deleteAll(article.getComments());
        }

        log.info("Article with id {} was updated", articleId);
        return ArticleMapper.toArticleFullDto(articleRepository.save(
                ArticleMapper.toArticleFromUpdate(article, updateArticle)));
    }

    @Override
    public Optional<?> getArticleById(String username, Long articleId) {
        Article article = validations.checkArticleExist(articleId);

        if (!article.getAuthor().getUsername().equals(username)) {
            if (article.getStatus() != ArticleStatus.PUBLISHED) {
                log.error("Article with id {} is not published yet. Current status is {}", articleId,
                        article.getStatus());
                throw new ActionForbiddenException(String.format("Article with id %d is not published yet", articleId));
            }
            return Optional.of(ArticleMapper.toArticleShortDto(article)); // for any registered user
        }
        return Optional.of(ArticleMapper.toArticleFullDto(article)); // for author and admin
    }

    @Override
    public List<ArticleFullDto> getAllArticlesByUserId(String username, Integer from, Integer size, String status) {
        PageRequest pageable = PageRequest.of(from / size, size);

        return switch (status) {
            case "PUBLISHED" -> {
                pageable.withSort(Sort.by("published").descending());
                yield ArticleMapper.toListArticleFull(articleRepository.findAllByAuthorUsernameAndStatus(
                        username, ArticleStatus.PUBLISHED, pageable));
            }
            case "MODERATING" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(articleRepository.findAllByAuthorUsernameAndStatus(
                        username, ArticleStatus.MODERATING, pageable));
            }
            case "REJECTED" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(articleRepository.findAllByAuthorUsernameAndStatus(
                        username, ArticleStatus.REJECTED, pageable));
            }
            case "CREATED" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(articleRepository.findAllByAuthorUsernameAndStatus(
                        username, ArticleStatus.CREATED, pageable));
            }
            case "ALL" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(
                        articleRepository.findAllByAuthorUsername(username, pageable));
            }
            default -> {
                log.info("Incorrect status: {}", status);
                throw new InvalidParameterException(String.format("Unknown status: %s", status));
            }
        };
    }


    @Override
    public void deleteArticle(String username, Long articleId) {
        Article article = validations.checkArticleExist(articleId);

        if (!validations.isAdmin(username)) {
            validations.checkUserIsAuthor(article, username);
        }

        if (!article.getTags().isEmpty()) {
            for (Tag tag : article.getTags()) {
                tag.getArticles().remove(article);
                tagRepository.save(tag);
            }
        }

        articleRepository.delete(article);
        log.info("Article with id {} was deleted", articleId);
    }

    @Override
    public ArticleFullDto publishArticle(String username, Long articleId) {
        User user = userRepository.findByUsername(username);
        validations.checkUserIsNotBanned(user);
        Article article = validations.checkArticleExist(articleId);
        validations.checkUserIsAuthor(article, username);
        article.setStatus(ArticleStatus.MODERATING);

        log.info("Article with id {} was sent to moderation", articleId);
        return ArticleMapper.toArticleFullDto(articleRepository.save(article));
    }
}