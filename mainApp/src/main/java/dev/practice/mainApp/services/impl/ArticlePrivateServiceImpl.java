package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.CommentRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticlePrivateService;
import dev.practice.mainApp.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePrivateServiceImpl implements ArticlePrivateService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final CommentRepository commentRepository;

    @Override
    public ArticleFullDto createArticle(long userId, ArticleNewDto newArticle) {
        User user = checkUserExist(userId);
        checkUserIsNotBanned(user);
        checkTitleNotExist(newArticle.getTitle(), null);

        Article savedArticle = articleRepository.save(ArticleMapper.toArticleFromNew(newArticle, user));
        if (newArticle.getTags() != null && !newArticle.getTags().isEmpty()) {
            Set<Tag> tags = (checkTagExist(newArticle.getTags(), savedArticle.getArticleId()));
            if (!tags.isEmpty()) {
                Article article = articleRepository.getReferenceById(savedArticle.getArticleId());
                tags.addAll(article.getTags());
                article.setTags(tags);
                user.getArticles().add(article);
                userRepository.save(user);

                log.info("Article with id {} was created by user with id {}", article.getArticleId(), userId);
                return ArticleMapper.toArticleFullDto(articleRepository.save(article));
            }
        }

        user.getArticles().add(savedArticle);
        userRepository.save(user);

        log.info("Article with id {} was created by user with id {}", savedArticle.getArticleId(), userId);
        return ArticleMapper.toArticleFullDto(savedArticle);
    }

    @Override
    public ArticleFullDto updateArticle(Long userId, Long articleId, ArticleUpdateDto updateArticle) {
        checkUserExist(userId);
        Article article = checkArticleExist(articleId);
        checkUserIsAuthor(article, userId);
        if (updateArticle.getTitle() != null && !updateArticle.getTitle().trim().isBlank()) {
            checkTitleNotExist(updateArticle.getTitle(), articleId);
        }
        if (!article.getComments().isEmpty()) {
            commentRepository.deleteAll(article.getComments());
        }

        log.info("Article with id {} was updated", articleId);
        return ArticleMapper.toArticleFullDto(articleRepository.save(
                ArticleMapper.toArticleFromUpdate(article, updateArticle)));
    }

    @Override
    public Optional<?> getArticleById(Long userId, Long articleId) {
        Article article = checkArticleExist(articleId);
        User user = checkUserExist(userId);

        if (!article.getAuthor().getUserId().equals(userId) && user.getRole().equals(Role.USER)) {
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
    public List<ArticleFullDto> getAllArticlesByUserId(Long userId, Integer from, Integer size, String status) {
        checkUserExist(userId);
        PageRequest pageable = PageRequest.of(from / size, size);

        return switch (status) {
            case "PUBLISHED" -> {
                pageable.withSort(Sort.by("published").descending());
                yield ArticleMapper.toListArticleFull(
                        articleRepository.findAllByAuthorUserIdAndStatus(userId, ArticleStatus.PUBLISHED, pageable));
            }
            case "MODERATING" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(
                        articleRepository.findAllByAuthorUserIdAndStatus(userId, ArticleStatus.MODERATING, pageable));
            }
            case "REJECTED" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(
                        articleRepository.findAllByAuthorUserIdAndStatus(userId, ArticleStatus.REJECTED, pageable));
            }
            case "CREATED" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(
                        articleRepository.findAllByAuthorUserIdAndStatus(userId, ArticleStatus.CREATED, pageable));
            }
            case "ALL" -> {
                pageable.withSort(Sort.by("created").descending());
                yield ArticleMapper.toListArticleFull(
                        articleRepository.findAllByAuthorUserId(userId, pageable));
            }
            default -> {
                log.info("Incorrect status: {}", status);
                throw new InvalidParameterException(String.format("Unknown status: %s", status));
            }
        };
    }


    @Override
    public void deleteArticle(long userId, long articleId) {
        Article article = checkArticleExist(articleId);
        User user = checkUserExist(userId);

        if (user.getRole() != Role.ADMIN) {
            checkUserIsAuthor(article, userId);
        }

        if (!article.getTags().isEmpty()) {
            for (Tag tag : article.getTags()) {
                removeArticleFromTag(tag, article);
            }
        }

        articleRepository.delete(article);
        log.info("Article with id {} was deleted", articleId);
    }

    @Override
    public ArticleFullDto publishArticle(Long userId, Long articleId) {
        User user = checkUserExist(userId);
        checkUserIsNotBanned(user);
        Article article = checkArticleExist(articleId);
        checkUserIsAuthor(article, userId);
        article.setStatus(ArticleStatus.MODERATING);

        log.info("Article with id {} was sent to moderation", articleId);
        return ArticleMapper.toArticleFullDto(articleRepository.save(article));
    }


    private User checkUserExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("User with id %d wasn't found", userId));
        }
        return user.get();
    }

    private void checkUserIsNotBanned(User user) {
        if (user.getIsBanned()) {
            log.error("User with id {} is blocked", user.getUserId());
            throw new ActionForbiddenException(String.format("User with id %d is blocked", user.getUserId()));
        }
    }

    private void checkTitleNotExist(String title, Long articleId) {
        String prepTitle = title.trim().toLowerCase();
        Article article = articleRepository.findArticlesByTitleIgnoreCase(prepTitle);
        if (articleId == null) {
            if (article != null) {
                log.error("Article with title {} already exist", title);
                throw new InvalidParameterException(String.format("Article with title %s already exist", title));
            }
        } else {
            if (article != null && !article.getArticleId().equals(articleId)) {
                log.error("Article with title {} already exist", title);
                throw new InvalidParameterException(String.format("Article with title %s already exist", title));
            }
        }
    }

    private Article checkArticleExist(Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            log.error("Article with id {} wasn't found", articleId);
            throw new ResourceNotFoundException(String.format("Article with id %d wasn't found", articleId));
        }
        return article.get();
    }

    private void checkUserIsAuthor(Article article, long userId) {
        if (article.getAuthor().getUserId() != userId) {
            log.error("Article with id {} is not belongs to user with id {}", article.getArticleId(), userId);
            throw new ActionForbiddenException(String.format(
                    "Article with id %d is not belongs to user with id %d. Action is forbidden",
                    article.getArticleId(), userId));
        }
    }

    private Set<Tag> checkTagExist(Set<TagNewDto> tags, long articleId) {
        Set<Tag> allTags = new HashSet<>();
        for (TagNewDto newTag : tags) {
            newTag.setName(newTag.getName().trim().toLowerCase());

            Tag tag = tagRepository.findTagByName(newTag.getName());
            if (tag != null) {
                allTags.add(tag);
                Set<Article> articles = tag.getArticles();
                articles.add(articleRepository.getReferenceById(articleId));
                tag.setArticles(articles);
                tagRepository.save(tag);
                log.info("Tag with id {} was added to article with id {}", tag.getTagId(), articleId);
            } else {
                tagService.createTag(newTag, articleId);
            }
        }
        return allTags;
    }

    private void removeArticleFromTag(Tag tag, Article article) {
        tag.getArticles().remove(article);
        tagRepository.save(tag);
    }

}
