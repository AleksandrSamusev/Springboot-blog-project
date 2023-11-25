package dev.practice.mainApp.utils;

import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final MessageRepository messageRepository;
    private final UserDetailsService userDetailsService;

    public User checkUserExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("User with id %d wasn't found", userId));
        }
        return user.get();
    }

    public User checkUserExistsByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user.isEmpty()) {
            log.error("User with username or email {} wasn't found", usernameOrEmail);
            throw new ResourceNotFoundException(String.format("User with username or email %s wasn't found",
                    usernameOrEmail));
        }
        return user.get();
    }

    public void checkUserIsNotBanned(User user) {
        if (user.getIsBanned()) {
            log.error("User with id {} is blocked", user.getUserId());
            throw new ActionForbiddenException(String.format("User with id %d is blocked", user.getUserId()));
        }
    }

    public void checkUserIsAuthor(Article article, String username) {
        if (!article.getAuthor().getUsername().equals(username)) {
            log.error("Article with id {} is not belongs to user with username {}", article.getArticleId(), username);
            throw new ActionForbiddenException(String.format(
                    "Article with id %d is not belongs to user with username %s. Action is forbidden",
                    article.getArticleId(), username));
        }
    }

    public void isUserAuthorized(Long userId, User currentUser) {
        if (!userId.equals(currentUser.getUserId())) {
            log.error("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
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

    public Tag isTagExists(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (tag.isEmpty()) {
            log.error("Tag with given ID = " + tagId + " not found");
            throw new ResourceNotFoundException("Tag with given ID = " + tagId + " not found");
        }
        return tag.get();
    }

    public Comment isCommentExists(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            log.error("ResourceNotFoundException. Comment with given Id = " + commentId + " not found");
            throw new ResourceNotFoundException("Comment with given Id = " + commentId + " not found");
        }
        return comment.get();
    }

    public void checkUserIsCommentAuthor(User user, Comment comment) {
        if (!user.getUserId().equals(comment.getCommentAuthor().getUserId())) {
            log.error("ActionForbiddenException. Action forbidden for given user");
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }

    public Message checkIfMessageExists(Long id) {
        Optional<Message> message = messageRepository.findById(id);
        if (message.isEmpty()) {
            log.error("ResourceNotFoundException. Message with given ID = " + id + " not found");
            throw new ResourceNotFoundException("Message with given ID = " + id + " not found");
        }
        return message.get();
    }

    public void checkSenderIsNotRecipient(Long recipientId, Long currentUserId) {
        if (recipientId.equals(currentUserId)) {
            log.error("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    public Boolean isAdmin(String username) {
        UserDetails details = userDetailsService.loadUserByUsername(username);
        return details != null && details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public Boolean usernameAlreadyExists(String username) {
        String formattedUsername = username.trim().replaceAll("\\s+", "").toLowerCase();
        return userRepository.findAll().stream()
                .anyMatch(n -> n.getUsername().toLowerCase().equals(formattedUsername));
    }
}