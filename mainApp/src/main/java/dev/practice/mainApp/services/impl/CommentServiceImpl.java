package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.comment.CommentFullDto;
import dev.practice.mainApp.dtos.comment.CommentNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.mappers.CommentMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.Comment;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.CommentRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public CommentFullDto createComment(Long articleId, CommentNewDto dto, Long userId) {
        isUserExists(userId);
        isUserBanned(userId);
        isArticleExists(articleId);
        isArticlePublished(articleId);
        User user = userRepository.findById(userId).get();
        Article article = articleRepository.findById(articleId).get();
        Comment comment = CommentMapper.toComment(dto, user, article);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment with ID = " + savedComment.getCommentId() + " saved");
        return CommentMapper.toCommentFullDto(savedComment);
    }

    @Override
    public CommentFullDto updateComment(CommentNewDto dto, Long commentId, Long userId) {
        isCommentExists(commentId);
        isUserExists(userId);
        Comment comment = commentRepository.findById(commentId).get();
        isActionAllowed(userId, comment);
        comment.setComment(dto.getComment());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with ID = " + updatedComment.getCommentId() + " updated");
        return CommentMapper.toCommentFullDto(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        isCommentExists(commentId);
        isUserExists(userId);
        Comment comment = commentRepository.findById(commentId).get();
        User user = userRepository.findById(userId).get();
        if (userId.equals(comment.getCommentAuthor().getUserId())
                || user.getRole().name().equals("ADMIN")) {
            log.info("Comment with ID = " + commentId + " deleted");
            commentRepository.deleteById(commentId);
        } else {
            log.info("ActionForbiddenException. Action forbidden for given user");
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }

    @Override
    public CommentFullDto getCommentById(Long commentId) {
        isCommentExists(commentId);
        log.info("Return comment with ID = " + commentId);
        return CommentMapper.toCommentFullDto(commentRepository.findById(commentId).get());
    }

    @Override
    public List<CommentFullDto> getAllCommentsToArticle(Long articleId) {
        isArticleExists(articleId);
        log.info("Returned all comments to article with ID = " + articleId);
        return articleRepository.findById(articleId).get().getComments()
                .stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }

    private void isUserBanned(Long userId) {
        if (userRepository.getReferenceById(userId).getIsBanned().equals(Boolean.TRUE)) {
            throw new ActionForbiddenException("Action forbidden. User with ID = " + userId + " is banned");
        }
    }

    private void isUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("ResourceNotFoundException. User with given Id = " + userId + " not found");
            throw new ResourceNotFoundException("User with given Id = " + userId + " not found");
        }
    }

    private void isArticleExists(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            log.info("ResourceNotFoundException. Article with given Id = " + articleId + " not found");
            throw new ResourceNotFoundException("Article with given Id = " + articleId + " not found");
        }
    }

    private void isArticlePublished(Long articleId) {
        isArticleExists(articleId);
        if (!articleRepository.getReferenceById(articleId).getStatus().equals(ArticleStatus.PUBLISHED)) {
            throw new ActionForbiddenException("Article with ID = " + articleId + " is not published yet!");
        }
    }

    private void isCommentExists(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            log.info("ResourceNotFoundException. Comment with given Id = " + commentId + " not found");
            throw new ResourceNotFoundException("Comment with given Id = " + commentId + " not found");
        }
    }

    private void isActionAllowed(Long userId, Comment comment) {
        if (!userId.equals(comment.getCommentAuthor().getUserId())) {
            log.info("ActionForbiddenException. Action forbidden for given user");
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }
}
