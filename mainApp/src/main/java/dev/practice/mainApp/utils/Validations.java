package dev.practice.mainApp.utils;

import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.CommentRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class Validations {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    public User checkUserExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("User with id %d wasn't found", userId));
        }
        return user.get();
    }

    public void checkUserIsNotBanned(User user) {
        if (user.getIsBanned()) {
            log.error("User with id {} is blocked", user.getUserId());
            throw new ActionForbiddenException(String.format("User with id %d is blocked", user.getUserId()));
        }
    }

    public void checkUserIsAdmin(Long userId) {
        if (userRepository.findById(userId).get().getRole() != Role.ADMIN) {
            log.error("User with id {} is not ADMIN", userId);
            throw new ActionForbiddenException(String.format(
                    "User with id %d is not ADMIN. Access is forbidden", userId));
        }
    }

    public void checkUserIsAuthor(Article article, long userId) {
        if (article.getAuthor().getUserId() != userId) {
            log.error("Article with id {} is not belongs to user with id {}", article.getArticleId(), userId);
            throw new ActionForbiddenException(String.format(
                    "Article with id %d is not belongs to user with id %d. Action is forbidden",
                    article.getArticleId(), userId));
        }
    }

    public Article checkArticleExist(Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            log.error("Article with id {} wasn't found", articleId);
            throw new ResourceNotFoundException(String.format("Article with id %d wasn't found", articleId));
        }
        return article.get();
    }

    public void checkArticleIsPublished(Article article) {
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            log.error("Article with id {} is not published yet. Current status is {}", article.getArticleId(),
                    article.getStatus());
            throw new ActionForbiddenException(String.format("Article with id %d is not published yet",
                    article.getArticleId()));
        }
    }

    public void checkTitleNotExist(String title, Long articleId) {
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

    public void isTagExists(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag with given ID = " + tagId + " not found");
        }
    }

    public Comment isCommentExists(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            log.info("ResourceNotFoundException. Comment with given Id = " + commentId + " not found");
            throw new ResourceNotFoundException("Comment with given Id = " + commentId + " not found");
        }
        return comment.get();
    }

    public void checkUserIsCommentAuthor(User user, Comment comment) {
        if (!user.getUserId().equals(comment.getCommentAuthor().getUserId())) {
            log.info("ActionForbiddenException. Action forbidden for given user");
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }
}